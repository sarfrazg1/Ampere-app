package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.SmartChargingTheme
import com.example.ui.viewmodel.BatteryViewModel
import com.example.util.AppLanguage
import com.example.util.Strings

class MainActivity : ComponentActivity() {
    private val viewModel: BatteryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            var currentTab by rememberSaveable { mutableIntStateOf(0) }

            // Back handler: if on tabs 1, 2, or 3, return to home tab first
            BackHandler(enabled = currentTab != 0) {
                currentTab = 0
            }

            SmartChargingTheme(darkTheme = uiState.isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = ElectricGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Strings.appTitle(uiState.language),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            actions = {
                                // Theme toggle
                                IconButton(
                                    onClick = { viewModel.toggleTheme() },
                                    modifier = Modifier.testTag("theme_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Toggle Theme",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Voice mute toggle
                                IconButton(
                                    onClick = { viewModel.toggleVoiceAlert() },
                                    modifier = Modifier.testTag("voice_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isVoiceEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                                        contentDescription = "Toggle Voice Alerts",
                                        tint = if (uiState.isVoiceEnabled) ElectricGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Language Toggle Pill ("EN | اردو")
                                LanguageTogglePill(
                                    currentLanguage = uiState.language,
                                    onToggle = { viewModel.toggleLanguage() }
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("bottom_navigation_bar")
                        ) {
                            val items = listOf(
                                TabItem(Strings.tabHome(uiState.language), Icons.Default.Home, "tab_home"),
                                TabItem(Strings.tabStats(uiState.language), Icons.Default.BarChart, "tab_stats"),
                                TabItem(Strings.tabGuide(uiState.language), Icons.AutoMirrored.Filled.MenuBook, "tab_guide"),
                                TabItem(Strings.tabAbout(uiState.language), Icons.Default.Info, "tab_about")
                            )

                            items.forEachIndexed { index, tab ->
                                NavigationBarItem(
                                    selected = currentTab == index,
                                    onClick = { currentTab = index },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricGreen,
                                        selectedTextColor = ElectricGreen,
                                        indicatorColor = ElectricGreen.copy(alpha = 0.15f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_crossfade"
                        ) { targetTab ->
                            when (targetTab) {
                                0 -> HomeScreen(
                                    uiState = uiState,
                                    onTestVoiceAlert = { viewModel.triggerTestVoiceAlert() },
                                    onSetSimulationPreset = { ma, plugged -> viewModel.setSimulatedPreset(ma, plugged) },
                                    onToggleSimulationMode = { enabled -> viewModel.setSimulationMode(enabled) }
                                )
                                1 -> HistoryScreen(
                                    uiState = uiState,
                                    onSaveSnapshot = { viewModel.saveSnapshot() },
                                    onClearHistory = { viewModel.clearHistory() }
                                )
                                2 -> GuideScreen(
                                    language = uiState.language
                                )
                                3 -> AboutScreen(
                                    uiState = uiState,
                                    onToggleVoiceAlert = { viewModel.toggleVoiceAlert() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageTogglePill(
    currentLanguage: AppLanguage,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onToggle() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("language_toggle_button"),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "EN | اردو",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (currentLanguage == AppLanguage.URDU) ElectricGreen else ElectricCyan
            )
        }
    }
}

private data class TabItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
