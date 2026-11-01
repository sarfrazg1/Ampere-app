package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CableQuality
import com.example.data.model.ChargingHistoryItem
import com.example.data.model.PluggedType
import com.example.util.AppLanguage
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("smart_charging_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_VOICE_ENABLED = "voice_alert_enabled"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_HISTORY = "charging_history_json"
    }

    var language: AppLanguage
        get() {
            val code = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
            return if (code == "ur") AppLanguage.URDU else AppLanguage.ENGLISH
        }
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE, value.code).apply()
        }

    var isVoiceAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_VOICE_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_VOICE_ENABLED, value).apply()
        }

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
        }

    fun saveHistory(items: List<ChargingHistoryItem>) {
        val array = JSONArray()
        items.take(30).forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("timestamp", item.timestamp)
                put("currentMa", item.currentMa)
                put("levelPercent", item.levelPercent)
                put("voltageV", item.voltageV.toDouble())
                put("temperatureC", item.temperatureC.toDouble())
                put("quality", item.quality.name)
                put("plugged", item.plugged.name)
                put("isCharging", item.isCharging)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    fun loadHistory(): List<ChargingHistoryItem> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<ChargingHistoryItem>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ChargingHistoryItem(
                        id = obj.optString("id", "${System.currentTimeMillis()}-$i"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        currentMa = obj.optInt("currentMa", 0),
                        levelPercent = obj.optInt("levelPercent", 50),
                        voltageV = obj.optDouble("voltageV", 4.0).toFloat(),
                        temperatureC = obj.optDouble("temperatureC", 30.0).toFloat(),
                        quality = runCatching {
                            CableQuality.valueOf(obj.optString("quality", "NORMAL"))
                        }.getOrDefault(CableQuality.NORMAL),
                        plugged = runCatching {
                            PluggedType.valueOf(obj.optString("plugged", "AC"))
                        }.getOrDefault(PluggedType.AC),
                        isCharging = obj.optBoolean("isCharging", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
