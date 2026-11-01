package com.example.data.model

data class ChargingHistoryItem(
    val id: String,
    val timestamp: Long,
    val currentMa: Int,
    val levelPercent: Int,
    val voltageV: Float,
    val temperatureC: Float,
    val quality: CableQuality,
    val plugged: PluggedType,
    val isCharging: Boolean
)
