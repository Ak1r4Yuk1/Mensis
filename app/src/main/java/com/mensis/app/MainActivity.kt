package com.mensis.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import com.mensis.app.ui.MainViewModel
import com.mensis.app.ui.MensisRoot
import com.mensis.app.ui.onboarding.OnboardingScreen
import com.mensis.app.ui.onboarding.WelcomeScreen
import com.mensis.app.ui.theme.MensisTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Graph.provide(this)
        lifecycleScope.launch {
            com.mensis.app.data.LegacyImport.runIfNeeded(this@MainActivity, Graph.repository, Graph.settings)
        }
        // Download automatico del modello AI se non è ancora presente (non solo dalla landing).
        runCatching { com.mensis.app.ai.ModelDownloader.ensureDownloading(this) }
        enableEdgeToEdge()
        setContent {
            val vm: MainViewModel = viewModel()
            val state by vm.state.collectAsStateWithLifecycle()

            MensisTheme(themeMode = state.settings.themeMode) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // (Ri)programma i promemoria dei prossimi eventi quando le previsioni cambiano.
                    LaunchedEffect(
                        state.loading, state.prediction, state.pregnancyActive,
                        state.settings.remindersEnabled, state.settings.reminderHour
                    ) {
                        if (!state.loading) {
                            com.mensis.app.notifications.ReminderScheduler.reschedule(
                                Graph.appContext, state.prediction, state.pregnancyActive,
                                state.settings.remindersEnabled, state.settings.reminderHour
                            )
                        }
                    }

                    var locked by remember { mutableStateOf(true) }

                    // Unlock automatically when app lock is disabled.
                    LaunchedEffect(state.loading, state.settings.appLockEnabled) {
                        if (!state.loading && !state.settings.appLockEnabled) locked = false
                    }

                    // Re-lock whenever the app goes to background.
                    val lifecycleOwner = LocalLifecycleOwner.current
                    DisposableEffect(lifecycleOwner, state.settings.appLockEnabled) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_STOP && state.settings.appLockEnabled) {
                                locked = true
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    var welcomeDone by rememberSaveable { mutableStateOf(false) }

                    when {
                        state.loading -> Unit
                        !state.onboardingDone && !welcomeDone -> WelcomeScreen(
                            onDone = { welcomeDone = true }
                        )
                        !state.onboardingDone -> OnboardingScreen(
                            onComplete = { name, cycle, period, last ->
                                vm.completeOnboarding(name, cycle, period, last)
                            },
                        )
                        else -> MensisRoot(
                            vm = vm,
                            state = state,
                            locked = locked,
                            onUnlock = { locked = false }
                        )
                    }
                }
            }
        }
    }
}
