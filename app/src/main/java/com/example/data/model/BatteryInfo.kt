package com.example.data.model

enum class BatteryHealth {
    GOOD,
    OVERHEAT,
    DEAD,
    OVER_VOLTAGE,
    COLD,
    UNSPECIFIED_FAILURE,
    UNKNOWN
}

enum class BatteryStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN
}

enum class PluggedType {
    AC,
    USB,
    WIRELESS,
    DOCK,
    UNPLUGGED
}

enum class CableQuality {
    FAST,       // > 1500 mA
    NORMAL,     // 800 - 1500 mA
    SLOW_FAULTY,// < 800 mA
    DISCHARGING
}

data class BatteryInfo(
    val currentNowMa: Int = 0,             // e.g. +1450 or -320
    val currentAvgMa: Int = 0,
    val minCurrentMa: Int = 0,
    val maxCurrentMa: Int = 0,
    val levelPercent: Int = 50,            // 0 - 100
    val temperatureC: Float = 28.0f,       // in Celsius
    val voltageV: Float = 4.0f,            // in Volts
    val health: BatteryHealth = BatteryHealth.GOOD,
    val status: BatteryStatus = BatteryStatus.DISCHARGING,
    val plugged: PluggedType = PluggedType.UNPLUGGED,
    val technology: String = "Li-ion",
    val isCharging: Boolean = false,
    val estimatedFullChargeMinutes: Int = 0,
    val estimatedDischargeMinutes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    val cableQuality: CableQuality
        get() = when {
            !isCharging -> CableQuality.DISCHARGING
            currentNowMa >= 1500 -> CableQuality.FAST
            currentNowMa in 800..1499 -> CableQuality.NORMAL
            else -> CableQuality.SLOW_FAULTY
        }
}
