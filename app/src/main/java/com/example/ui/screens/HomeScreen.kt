package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatteryHealth
import com.example.data.model.BatteryInfo
import com.example.data.model.CableQuality
import com.example.ui.components.AmpereGauge
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.ElectricRed
import com.example.ui.viewmodel.UiState
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun HomeScreen(
    uiState: UiState,
    onTestVoiceAlert: () -> Unit,
    onSetSimulationPreset: (Int, Boolean) -> Unit,
    onToggleSimulationMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val info = uiState.batteryInfo
    val lang = uiState.language
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 5-Second Analysis Countdown Banner
        AnimatedVisibility(
            visible = uiState.isAnalyzingCable && uiState.countdownSeconds != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val sec = uiState.countdownSeconds ?: 5
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = ElectricCyan.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("countdown_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$sec",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.analyzingCountdown(lang, sec),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (5 - sec + 1) / 5f },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = ElectricCyan,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Ampere Circular Gauge
        AmpereGauge(
            batteryInfo = info,
            language = lang,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Estimated Time Banner
        if (info.isCharging) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = ElectricGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.estTimeToFull(lang, info.estimatedFullChargeMinutes),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.estTimeToDischarge(lang, info.estimatedDischargeMinutes),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Cable Quality Assessment Card
        CableQualityCard(
            quality = info.cableQuality,
            currentMa = info.currentNowMa,
            language = lang,
            onTestVoice = onTestVoiceAlert
        )

        // 4 Main Battery Cards Grid (Level, Temp, Voltage, Health)
        FourBatteryCardsGrid(
            info = info,
            language = lang
        )

        // Cable Testing Sandbox / Simulator Controls (Very helpful on emulator & hardware test)
        CableSandboxControls(
            isSimulationMode = uiState.isSimulationMode,
            currentPreset = uiState.simulatedMaPreset,
            language = lang,
            onSelectPreset = onSetSimulationPreset,
            onToggleMode = onToggleSimulationMode
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CableQualityCard(
    quality: CableQuality,
    currentMa: Int,
    language: AppLanguage,
    onTestVoice: () -> Unit
) {
    val (bgColor, borderColor, icon, statusText, voiceQuote) = when (quality) {
        CableQuality.FAST -> {
            Tuple5(
                ElectricGreen.copy(alpha = 0.12f),
                ElectricGreen,
                Icons.Default.CheckCircle,
                Strings.fastCharging(language),
                if (language == AppLanguage.URDU) Strings.voiceFastUrdu() else Strings.voiceFastEnglish()
            )
        }
        CableQuality.NORMAL -> {
            Tuple5(
                ElectricCyan.copy(alpha = 0.12f),
                ElectricCyan,
                Icons.Default.Speed,
                Strings.normalCharging(language),
                if (language == AppLanguage.URDU) Strings.voiceNormalUrdu() else Strings.voiceNormalEnglish()
            )
        }
        CableQuality.SLOW_FAULTY -> {
            Tuple5(
                ElectricRed.copy(alpha = 0.12f),
                ElectricRed,
                Icons.Default.Warning,
                Strings.slowOrFaulty(language),
                if (language == AppLanguage.URDU) Strings.voiceSlowUrdu() else Strings.voiceSlowEnglish()
            )
        }
        CableQuality.DISCHARGING -> {
            Tuple5(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                Icons.Default.Cable,
                Strings.discharging(language),
                if (language == AppLanguage.URDU) "چارجر منسلک نہیں ہے" else "Charger not connected"
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .testTag("cable_quality_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.cableStatus(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = borderColor
                        )
                    }
                }

                // Test voice button
                Button(
                    onClick = onTestVoice,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = borderColor),
                    modifier = Modifier.testTag("test_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (quality == CableQuality.FAST || quality == CableQuality.NORMAL) Color.Black else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.testVoiceBtn(language),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (quality == CableQuality.FAST || quality == CableQuality.NORMAL) Color.Black else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Speech quote bubble
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "\"$voiceQuote\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FourBatteryCardsGrid(
    info: BatteryInfo,
    language: AppLanguage
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Card 1: Battery Level %
            MetricCard(
                icon = Icons.Default.BatteryChargingFull,
                iconColor = if (info.levelPercent > 20) ElectricGreen else ElectricRed,
                label = Strings.batteryLevel(language),
                value = "${info.levelPercent}%",
                subtext = "${info.technology} • ${info.plugged.name}",
                modifier = Modifier.weight(1f).testTag("battery_level_card")
            )

            // Card 2: Temperature °C
            val tempColor = when {
                info.temperatureC >= 42f -> ElectricRed
                info.temperatureC >= 37f -> ElectricAmber
                else -> ElectricCyan
            }
            MetricCard(
                icon = Icons.Default.DeviceThermostat,
                iconColor = tempColor,
                label = Strings.temperature(language),
                value = "%.1f °C".format(info.temperatureC),
                subtext = "%.1f °F".format((info.temperatureC * 9 / 5) + 32),
                modifier = Modifier.weight(1f).testTag("temperature_card")
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Card 3: Voltage V
            MetricCard(
                icon = Icons.Default.ElectricBolt,
                iconColor = ElectricAmber,
                label = Strings.voltage(language),
                value = "%.2f V".format(info.voltageV),
                subtext = "${(info.voltageV * 1000).toInt()} mV",
                modifier = Modifier.weight(1f).testTag("voltage_card")
            )

            // Card 4: Battery Health
            val healthText = when (info.health) {
                BatteryHealth.GOOD -> Strings.healthGood(language)
                BatteryHealth.OVERHEAT -> Strings.healthOverheat(language)
                BatteryHealth.DEAD -> Strings.healthDead(language)
                BatteryHealth.OVER_VOLTAGE -> Strings.healthOverVoltage(language)
                BatteryHealth.COLD -> Strings.healthCold(language)
                else -> Strings.healthGood(language)
            }
            val healthColor = if (info.health == BatteryHealth.GOOD) ElectricGreen else ElectricRed
            MetricCard(
                icon = Icons.Default.Favorite,
                iconColor = healthColor,
                label = Strings.batteryHealth(language),
                value = healthText,
                subtext = "Optimal Lifespan",
                modifier = Modifier.weight(1f).testTag("battery_health_card")
            )
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CableSandboxControls(
    isSimulationMode: Boolean,
    currentPreset: Int,
    language: AppLanguage,
    onSelectPreset: (Int, Boolean) -> Unit,
    onToggleMode: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .testTag("simulation_sandbox_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Strings.simulationMode(language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = Strings.simulationHelp(language),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Presets chips for immediate testing
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Preset 1: Fast (>1500mA)
                FilterChip(
                    selected = isSimulationMode && currentPreset >= 1500,
                    onClick = { onSelectPreset(2100, true) },
                    label = { Text("⚡ Fast 2100mA", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricGreen.copy(alpha = 0.25f),
                        selectedLabelColor = ElectricGreen
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Preset 2: Normal (800-1500mA)
                FilterChip(
                    selected = isSimulationMode && currentPreset in 800..1499,
                    onClick = { onSelectPreset(1150, true) },
                    label = { Text("🔌 Normal 1150mA", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan.copy(alpha = 0.25f),
                        selectedLabelColor = ElectricCyan
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Preset 3: Faulty / Slow (<800mA)
                FilterChip(
                    selected = isSimulationMode && currentPreset in 1..799,
                    onClick = { onSelectPreset(420, true) },
                    label = { Text("⚠️ Slow 420mA", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricRed.copy(alpha = 0.25f),
                        selectedLabelColor = ElectricRed
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Preset 4: Unplugged / Discharging (-350mA)
                FilterChip(
                    selected = isSimulationMode && currentPreset < 0,
                    onClick = { onSelectPreset(-350, false) },
                    label = { Text("🔋 Discharging", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedLabelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            if (isSimulationMode) {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { onToggleMode(false) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.URDU) "حقیقی بیٹری سینسر پر واپس جائیں" else "Switch to Live Battery Sensors",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
