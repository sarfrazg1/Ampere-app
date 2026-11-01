package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatteryInfo
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.ElectricRed
import com.example.util.AppLanguage
import com.example.util.Strings
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AmpereGauge(
    batteryInfo: BatteryInfo,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isCharging = batteryInfo.isCharging
    val currentMa = batteryInfo.currentNowMa

    val primaryColor = if (isCharging) ElectricGreen else ElectricRed
    val secondaryColor = if (isCharging) ElectricCyan else Color(0xFFFF8A80)

    // Gauge range mapping:
    // If charging: 0 mA to 3000 mA -> 0 to 1
    // If discharging: -1000 mA to 0 mA -> 0 to 1
    val targetFraction = if (isCharging) {
        (currentMa.toFloat() / 3000f).coerceIn(0.05f, 1.0f)
    } else {
        (kotlin.math.abs(currentMa).toFloat() / 1000f).coerceIn(0.05f, 1.0f)
    }

    val animatedSweepFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "gauge_sweep"
    )

    // Pulse animation for subtle live glow
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(280.dp)
            .testTag("ampere_gauge_container"),
        contentAlignment = Alignment.Center
    ) {
        // Background glow disk
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.16f),
                            primaryColor.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Canvas for arc and tick marks
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            val strokeWidth = 18.dp.toPx()
            val startAngle = 135f
            val totalSweep = 270f
            val activeSweep = totalSweep * animatedSweepFraction

            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val arcTopLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background track
            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active arc with vibrant gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        secondaryColor,
                        primaryColor,
                        primaryColor
                    )
                ),
                startAngle = startAngle,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw radial tick marks around the gauge
            val radius = (size.minDimension / 2f) - (strokeWidth * 1.15f)
            val center = Offset(size.width / 2f, size.height / 2f)
            val tickCount = 15
            for (i in 0..tickCount) {
                val angleDeg = startAngle + (totalSweep * (i.toFloat() / tickCount.toFloat()))
                val angleRad = (angleDeg * PI / 180.0).toFloat()

                val innerR = radius - 8.dp.toPx()
                val outerR = radius - 1.dp.toPx()

                val startP = Offset(
                    x = center.x + innerR * cos(angleRad),
                    y = center.y + innerR * sin(angleRad)
                )
                val endP = Offset(
                    x = center.x + outerR * cos(angleRad),
                    y = center.y + outerR * sin(angleRad)
                )

                val tickActive = (i.toFloat() / tickCount.toFloat()) <= animatedSweepFraction
                drawDistanceTick(
                    start = startP,
                    end = endP,
                    color = if (tickActive) primaryColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.2f),
                    width = if (tickActive) 3f else 1.5f
                )
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Status Icon & Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(primaryColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isCharging) Icons.Default.Bolt else Icons.Default.ElectricMeter,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isCharging) Strings.charging(language) else Strings.discharging(language),
                    style = MaterialTheme.typography.labelMedium,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Big Animated mA Value
            val sign = if (currentMa > 0) "+" else ""
            Text(
                text = "$sign$currentMa",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = primaryColor
            )

            Text(
                text = "mA",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Min / Avg / Max Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricPill(
                    label = Strings.minMa(language),
                    value = "${batteryInfo.minCurrentMa}",
                    color = primaryColor.copy(alpha = 0.8f)
                )
                MetricPill(
                    label = Strings.avgMa(language),
                    value = "${batteryInfo.currentAvgMa}",
                    color = ElectricCyan
                )
                MetricPill(
                    label = Strings.maxMa(language),
                    value = "${batteryInfo.maxCurrentMa}",
                    color = primaryColor
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDistanceTick(
    start: Offset,
    end: Offset,
    color: Color,
    width: Float
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = width,
        cap = StrokeCap.Round
    )
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
