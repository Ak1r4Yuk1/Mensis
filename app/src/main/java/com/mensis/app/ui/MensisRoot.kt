package com.mensis.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.mensis.app.academy.AcademyContent
import com.mensis.app.academy.AcademyMode
import com.mensis.app.academy.AcademyScreen
import com.mensis.app.academy.ContentDetailScreen
import com.mensis.app.ai.ChatScreen
import com.mensis.app.ui.calendar.CalendarScreen
import com.mensis.app.ui.dashboard.DashboardScreen
import com.mensis.app.ui.insights.InsightsScreen
import com.mensis.app.ui.lock.LockScreen
import com.mensis.app.ui.pregnancy.KickCounterScreen
import com.mensis.app.ui.pregnancy.PregnancyCalendarScreen
import com.mensis.app.ui.pregnancy.PregnancyDashboard
import com.mensis.app.ui.pregnancy.PregnancyGuideScreen
import com.mensis.app.ui.settings.SettingsScreen

private enum class Tab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    CALENDAR("Calendario", Icons.Filled.CalendarMonth),
    ACADEMY("Academy", Icons.Filled.School),
    SETTINGS("Impostazioni", Icons.Filled.Settings)
}

@Composable
fun MensisRoot(
    vm: MainViewModel,
    state: HomeState,
    locked: Boolean,
    onUnlock: () -> Unit
) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var detail by remember { mutableStateOf<AcademyContent?>(null) }
    var overlay by remember { mutableStateOf<String?>(null) }
    val pregnancy = state.pregnancyActive

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                if (detail == null && overlay == null) {
                    FloatingActionButton(onClick = { overlay = "chat" }) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Assistente AI")
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach { t ->
                        val icon = when {
                            t == Tab.HOME && pregnancy -> Icons.Filled.Favorite
                            else -> t.icon
                        }
                        val label = when {
                            t == Tab.HOME && pregnancy -> "Gravidanza"
                            t == Tab.CALENDAR && pregnancy -> "Settimane"
                            else -> t.label
                        }
                        NavigationBarItem(
                            selected = tab == t,
                            onClick = { tab = t },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())
            ) {
                when (tab) {
                    Tab.HOME -> if (pregnancy) {
                        PregnancyDashboard(
                            state,
                            onOpenGuide = { overlay = "guide" },
                            onOpenKick = { overlay = "kick" },
                            onAddWeight = { vm.addWeight(it) }
                        )
                    } else {
                        DashboardScreen(
                            state = state,
                            onMarkPeriodToday = { vm.markPeriodStart(java.time.LocalDate.now()) },
                            onLogToday = { tab = Tab.CALENDAR },
                            onOpenInsights = { overlay = "insights" },
                            onQuickLog = { transform -> vm.updateTodayLog(transform) }
                        )
                    }
                    Tab.CALENDAR -> if (pregnancy) PregnancyCalendarScreen(state) else CalendarScreen(vm, state)
                    Tab.ACADEMY -> AcademyScreen(
                        mode = if (pregnancy) AcademyMode.PREGNANCY else AcademyMode.CYCLE,
                        currentPhase = if (pregnancy) null else state.prediction?.phase,
                        onOpen = { detail = it }
                    )
                    Tab.SETTINGS -> SettingsScreen(vm, state)
                }
            }
        }

        // Sub-screens (with back)
        detail?.let { c ->
            BackHandler { detail = null }
            ContentDetailScreen(c, onBack = { detail = null })
        }
        if (detail == null && overlay == "chat") {
            BackHandler { overlay = null }
            ChatScreen(state, onBack = { overlay = null })
        } else if (detail == null && overlay != null) {
            BackHandler { overlay = null }
            OverlayScreen(
                title = when (overlay) {
                    "guide" -> "Guida gravidanza"
                    "kick" -> "Conta movimenti"
                    else -> "Statistiche"
                },
                onBack = { overlay = null }
            ) {
                when (overlay) {
                    "guide" -> PregnancyGuideScreen(state)
                    "kick" -> KickCounterScreen(vm)
                    else -> InsightsScreen(state)
                }
            }
        }

        if (locked && state.settings.appLockEnabled) {
            LockScreen(
                storedPin = state.settings.appLockPin,
                biometricEnabled = state.settings.biometricUnlockEnabled,
                onUnlock = onUnlock
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverlayScreen(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            content()
        }
    }
}
