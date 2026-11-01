package com.example.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.model.BatteryHealth
import com.example.data.model.BatteryInfo
import com.example.data.model.BatteryStatus
import com.example.data.model.CableQuality
import com.example.data.model.ChargingHistoryItem
import com.example.data.model.PluggedType
import com.example.service.VoiceAlertManager
import com.example.util.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

data class UiState(
    val batteryInfo: BatteryInfo = BatteryInfo(),
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isVoiceEnabled: Boolean = true,
    val isDarkTheme: Boolean = true,
    val countdownSeconds: Int? = null, // null when not counting down, 5 down to 1
    val isAnalyzingCable: Boolean = false,
    val lastSpokenMessage: String? = null,
    val history: List<ChargingHistoryItem> = emptyList(),
    val isSimulationMode: Boolean = false,
    val simulatedMaPreset: Int = 1850,
    val simulatedPlugged: Boolean = true
)

class BatteryViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesManager(application)
    private val voiceManager = VoiceAlertManager(application)
    private val batteryManager = application.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager

    private val _uiState = MutableStateFlow(
        UiState(
            language = prefs.language,
            isVoiceEnabled = prefs.isVoiceAlertEnabled,
            isDarkTheme = prefs.isDarkTheme,
            history = prefs.loadHistory()
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private var countdownJob: Job? = null
    private var wasChargingPreviously = false
    private var recordedCurrents = mutableListOf<Int>()

    // Cache latest battery intent data
    private var cachedLevel = 65
    private var cachedVoltageMv = 4050
    private var cachedTempTenths = 312
    private var cachedHealth = BatteryHealth.GOOD
    private var cachedStatus = BatteryStatus.CHARGING
    private var cachedPlugged = PluggedType.AC
    private var cachedTech = "Li-ion"

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                parseBatteryIntent(intent)
            }
        }
    }

    init {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = application.registerReceiver(batteryReceiver, filter)
        if (stickyIntent != null) {
            parseBatteryIntent(stickyIntent)
        }
        startPeriodicUpdate()
    }

    private fun parseBatteryIntent(intent: Intent) {
        val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val rawScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (rawLevel >= 0 && rawScale > 0) {
            cachedLevel = ((rawLevel.toFloat() / rawScale.toFloat()) * 100).toInt()
        }

        cachedVoltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4000)
        cachedTempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 300)

        cachedHealth = when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            else -> BatteryHealth.UNSPECIFIED_FAILURE
        }

        val rawStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        cachedStatus = when (rawStatus) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus.NOT_CHARGING
            else -> BatteryStatus.UNKNOWN
        }

        val rawPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        cachedPlugged = when (rawPlugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> PluggedType.AC
            BatteryManager.BATTERY_PLUGGED_USB -> PluggedType.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> PluggedType.WIRELESS
            else -> if (rawPlugged > 0) PluggedType.DOCK else PluggedType.UNPLUGGED
        }

        cachedTech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
    }

    private fun readHardwareCurrentMa(): Int {
        if (batteryManager == null) return -280
        val rawCurrent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        if (rawCurrent == Int.MIN_VALUE || rawCurrent == 0) {
            val avg = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
            if (avg != Int.MIN_VALUE && avg != 0) {
                return normalizeMa(avg)
            }
            return if (cachedStatus == BatteryStatus.CHARGING) 1250 else -320
        }
        return normalizeMa(rawCurrent)
    }

    private fun normalizeMa(raw: Int): Int {
        // Many OEMs return microamperes (uA), e.g., 1,500,000 uA = 1500 mA
        return if (kotlin.math.abs(raw) >= 10000) {
            raw / 1000
        } else {
            raw
        }
    }

    private fun startPeriodicUpdate() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                updateBatteryState()
                delay(1000) // 1 second real-time update
            }
        }
    }

    private fun updateBatteryState() {
        val currentState = _uiState.value

        val isCharging: Boolean
        val currentMa: Int
        val plugged: PluggedType
        val status: BatteryStatus

        if (currentState.isSimulationMode) {
            isCharging = currentState.simulatedPlugged
            currentMa = if (isCharging) currentState.simulatedMaPreset else -350
            plugged = if (isCharging) PluggedType.AC else PluggedType.UNPLUGGED
            status = if (isCharging) {
                if (cachedLevel >= 100) BatteryStatus.FULL else BatteryStatus.CHARGING
            } else {
                BatteryStatus.DISCHARGING
            }
        } else {
            val hwCurrent = readHardwareCurrentMa()
            val hwCharging = cachedStatus == BatteryStatus.CHARGING || cachedPlugged != PluggedType.UNPLUGGED
            isCharging = hwCharging
            // Ensure positive current when charging, negative when discharging for UI clarity
            currentMa = if (isCharging) {
                if (hwCurrent < 0) kotlin.math.abs(hwCurrent) else hwCurrent.coerceAtLeast(350)
            } else {
                if (hwCurrent > 0) -hwCurrent else hwCurrent.coerceAtMost(-150)
            }
            plugged = cachedPlugged
            status = cachedStatus
        }

        // Track stats
        recordedCurrents.add(currentMa)
        if (recordedCurrents.size > 60) recordedCurrents.removeAt(0)

        val minCurrent = recordedCurrents.minOrNull() ?: currentMa
        val maxCurrent = recordedCurrents.maxOrNull() ?: currentMa
        val avgCurrent = if (recordedCurrents.isNotEmpty()) recordedCurrents.average().toInt() else currentMa

        // Estimated full charge time calculation:
        // Assume nominal 4000mAh battery. Remaining capacity = (100 - level)% * 4000mAh
        // Time = (remaining mAh / current mA) * 60 minutes
        val remainingMah = ((100 - cachedLevel).coerceAtLeast(1) * 4000) / 100
        val estChargeMinutes = if (isCharging && currentMa > 100) {
            ((remainingMah.toFloat() / currentMa.toFloat()) * 60).toInt().coerceIn(5, 480)
        } else 0

        val estDischargeMinutes = if (!isCharging && currentMa < -50) {
            val currentMahCapacity = (cachedLevel * 4000) / 100
            ((currentMahCapacity.toFloat() / kotlin.math.abs(currentMa).toFloat()) * 60).toInt().coerceIn(10, 2400)
        } else 0

        val newInfo = BatteryInfo(
            currentNowMa = currentMa,
            currentAvgMa = avgCurrent,
            minCurrentMa = minCurrent,
            maxCurrentMa = maxCurrent,
            levelPercent = cachedLevel,
            temperatureC = cachedTempTenths / 10.0f,
            voltageV = cachedVoltageMv / 1000.0f,
            health = cachedHealth,
            status = status,
            plugged = plugged,
            technology = cachedTech,
            isCharging = isCharging,
            estimatedFullChargeMinutes = estChargeMinutes,
            estimatedDischargeMinutes = estDischargeMinutes
        )

        _uiState.value = _uiState.value.copy(batteryInfo = newInfo)

        // Check if charger was just connected
        if (!wasChargingPreviously && isCharging) {
            wasChargingPreviously = true
            triggerChargerConnectedAnalysis()
        } else if (wasChargingPreviously && !isCharging) {
            wasChargingPreviously = false
            countdownJob?.cancel()
            _uiState.value = _uiState.value.copy(countdownSeconds = null, isAnalyzingCable = false)
        }
    }

    private fun triggerChargerConnectedAnalysis() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzingCable = true)
            for (sec in 5 downTo 1) {
                _uiState.value = _uiState.value.copy(countdownSeconds = sec)
                delay(1000)
            }
            _uiState.value = _uiState.value.copy(countdownSeconds = null, isAnalyzingCable = false)

            // 5 seconds elapsed! Read steady-state charging current and speak voice alert!
            val info = _uiState.value.batteryInfo
            if (info.isCharging && _uiState.value.isVoiceEnabled) {
                vibrateBriefly()
                voiceManager.speakCableAssessment(
                    currentMa = info.currentNowMa,
                    estimatedMinutes = info.estimatedFullChargeMinutes,
                    language = _uiState.value.language
                )
            }
            saveSnapshot()
        }
    }

    private fun vibrateBriefly() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleLanguage() {
        val newLang = if (_uiState.value.language == AppLanguage.ENGLISH) AppLanguage.URDU else AppLanguage.ENGLISH
        prefs.language = newLang
        _uiState.value = _uiState.value.copy(language = newLang)

        // Give quick confirmation voice feedback
        if (_uiState.value.isVoiceEnabled) {
            val testMsg = if (newLang == AppLanguage.URDU) "اردو زبان منتخب کر لی گئی ہے" else "English language selected"
            voiceManager.speak(testMsg, newLang)
        }
    }

    fun toggleVoiceAlert() {
        val next = !_uiState.value.isVoiceEnabled
        prefs.isVoiceAlertEnabled = next
        _uiState.value = _uiState.value.copy(isVoiceEnabled = next)
        if (!next) {
            voiceManager.stop()
        }
    }

    fun toggleTheme() {
        val next = !_uiState.value.isDarkTheme
        prefs.isDarkTheme = next
        _uiState.value = _uiState.value.copy(isDarkTheme = next)
    }

    fun triggerTestVoiceAlert() {
        val info = _uiState.value.batteryInfo
        vibrateBriefly()
        voiceManager.speakCableAssessment(
            currentMa = info.currentNowMa,
            estimatedMinutes = info.estimatedFullChargeMinutes,
            language = _uiState.value.language
        )
    }

    fun setSimulationMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSimulationMode = enabled)
        if (enabled) {
            triggerChargerConnectedAnalysis()
        }
    }

    fun setSimulatedPreset(presetMa: Int, isPlugged: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSimulationMode = true,
            simulatedMaPreset = presetMa,
            simulatedPlugged = isPlugged
        )
        if (isPlugged) {
            triggerChargerConnectedAnalysis()
        }
    }

    fun saveSnapshot() {
        val info = _uiState.value.batteryInfo
        val newItem = ChargingHistoryItem(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            currentMa = info.currentNowMa,
            levelPercent = info.levelPercent,
            voltageV = info.voltageV,
            temperatureC = info.temperatureC,
            quality = info.cableQuality,
            plugged = info.plugged,
            isCharging = info.isCharging
        )
        val updated = listOf(newItem) + _uiState.value.history
        prefs.saveHistory(updated)
        _uiState.value = _uiState.value.copy(history = updated)
    }

    fun clearHistory() {
        prefs.saveHistory(emptyList())
        _uiState.value = _uiState.value.copy(history = emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
        countdownJob?.cancel()
        try {
            getApplication<Application>().unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            // Ignored
        }
        voiceManager.shutdown()
    }
}
