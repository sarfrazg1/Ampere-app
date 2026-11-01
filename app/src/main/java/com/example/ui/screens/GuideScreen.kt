package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.ElectricRed
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun GuideScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .testTag("guide_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.12f),
                                ElectricGreen.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Strings.guidePageTitle(language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = Strings.guideHeader(language),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElectricGreen
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = Strings.guideSubtitle(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 5 Core Points
        Text(
            text = if (language == AppLanguage.URDU) "بنیادی اصول و نکات (5 پوائنٹس)" else "Core Principles (5 Key Points)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Point 1
        GuidePointCard(
            number = "1",
            icon = Icons.Default.ElectricMeter,
            iconTint = ElectricCyan,
            title = if (language == AppLanguage.URDU) "کرنٹ کی پیمائش (mA)" else "Electric Current Measurement (mA)",
            body = Strings.point1(language)
        )

        // Point 2
        GuidePointCard(
            number = "2",
            icon = Icons.Default.CheckCircle,
            iconTint = ElectricGreen,
            title = if (language == AppLanguage.URDU) "زیادہ کرنٹ (+1500mA) = عمدہ کیبل و چارجر" else "High Current (+1500mA) = Good Cable & Charger",
            body = Strings.point2(language)
        )

        // Point 3
        GuidePointCard(
            number = "3",
            icon = Icons.Default.Warning,
            iconTint = ElectricRed,
            title = if (language == AppLanguage.URDU) "کم کرنٹ (<500mA) = خراب کیبل یا سلو چارجر" else "Low Current (<500mA) = Faulty Cable / Slow Charger",
            body = Strings.point3(language)
        )

        // Point 4
        GuidePointCard(
            number = "4",
            icon = Icons.Default.HealthAndSafety,
            iconTint = ElectricAmber,
            title = if (language == AppLanguage.URDU) "وولٹیج اور ٹمپریچر سے بیٹری کی ہیلتھ" else "Voltage & Temperature for Battery Health",
            body = Strings.point4(language)
        )

        // Point 5: Formula Card with Math Box
        FormulaCard(language = language)

        // Quality Benchmark Reference Card
        BenchmarkTableCard(language = language)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GuidePointCard(
    number: String,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    body: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = iconTint
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun FormulaCard(
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("formula_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ElectricCyan.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "5",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ElectricCyan
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == AppLanguage.URDU) "مکمل چارج وقت کا فارمولا" else "Full Charge Time Formula",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mathematical Equation Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Full Charge Time = Remaining mAh ÷ Current mA",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = ElectricCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "مکمل چارج وقت = باقی بیٹری (mAh) ÷ چارجنگ کرنٹ (mA)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = Strings.point5(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BenchmarkTableCard(
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (language == AppLanguage.URDU) "کیبل کوالٹی اور کرنٹ کی حدیں" else "Cable Quality Benchmark Standards",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            BenchmarkRow(
                range = "> 1500 mA",
                label = Strings.fastCharging(language),
                desc = if (language == AppLanguage.URDU) "بہترین فاسٹ چارجنگ کیبل اور اڈاپٹر" else "High-speed charging cable & fast adapter",
                color = ElectricGreen
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )

            BenchmarkRow(
                range = "800 - 1500 mA",
                label = Strings.normalCharging(language),
                desc = if (language == AppLanguage.URDU) "معیاری / اوسط رفتار کیبل" else "Standard charging cable",
                color = ElectricCyan
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )

            BenchmarkRow(
                range = "< 800 mA",
                label = Strings.slowOrFaulty(language),
                desc = if (language == AppLanguage.URDU) "کیبل تبدیل کریں یا ہائی واٹ چارجر استعمال کریں" else "Replace cable or use higher wattage adapter",
                color = ElectricRed
            )
        }
    }
}

@Composable
private fun BenchmarkRow(
    range: String,
    label: String,
    desc: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f))
                .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(vertical = 6.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = range,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = color
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
