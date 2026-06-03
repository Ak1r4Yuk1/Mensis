package com.mensis.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mensis.app.CycleLog
import com.mensis.app.CyclePrediction
import com.mensis.app.CycleRecord
import com.mensis.app.Graph
import com.mensis.app.PregnancySummary
import com.mensis.app.data.Settings
import com.mensis.app.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeState(
    val loading: Boolean = true,
    val settings: Settings = Settings(),
    val cycles: List<CycleRecord> = emptyList(),
    val logs: List<CycleLog> = emptyList(),
    val prediction: CyclePrediction? = null,
    val pregnancy: PregnancySummary? = null
) {
    val onboardingDone: Boolean get() = settings.onboardingDone
    val pregnancyActive: Boolean get() = settings.pregnancyMode && pregnancy != null
}

class MainViewModel : ViewModel() {

    private val repo = Graph.repository
    private val settingsRepo = Graph.settings
    private val engine = Graph.engine

    val state: StateFlow<HomeState> =
        combine(settingsRepo.settings, repo.cycles, repo.logs) { settings, cycles, logs ->
            val today = LocalDate.now()
            val prediction = engine.predict(
                today = today,
                cycles = cycles,
                logs = logs,
                preferredCycleLength = settings.cycleLength,
                preferredPeriodLength = settings.periodLength
            )
            val pregnancy = if (settings.pregnancyMode && settings.pregnancyReferenceDate != null) {
                engine.pregnancySummary(today, settings.pregnancyReferenceType, settings.pregnancyReferenceDate)
            } else null
            HomeState(false, settings, cycles, logs, prediction, pregnancy)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    val kickSessions: StateFlow<List<com.mensis.app.data.KickSession>> =
        repo.kicks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun saveKick(count: Int, durationSeconds: Int) =
        viewModelScope.launch { repo.saveKick(count, durationSeconds) }

    fun deleteKick(id: Long) = viewModelScope.launch { repo.deleteKick(id) }

    /** Applica una modifica al diario di oggi preservando gli altri campi. */
    fun updateTodayLog(transform: (CycleLog) -> CycleLog) = viewModelScope.launch {
        val today = LocalDate.now()
        val base = repo.logForDate(today) ?: CycleLog(
            id = null, date = today, temperature = null, flow = null, pain = null, mucus = null,
            mood = null, notes = null, symptoms = null, medications = null, intercourse = null,
            weight = null, sleepHours = null, waterGlasses = null, activity = null, lhTestResult = null
        )
        repo.saveLog(transform(base))
    }

    fun addWeight(weight: Double) = updateTodayLog { it.copy(weight = weight) }

    fun setReminders(enabled: Boolean, hour: Int) =
        viewModelScope.launch { settingsRepo.setReminders(enabled, hour) }

    fun phaseForDate(date: LocalDate, s: HomeState): Pair<String, Int> =
        engine.phaseForDate(date, s.cycles, s.logs, s.settings.cycleLength, s.settings.periodLength)

    // --- Actions ---
    fun completeOnboarding(name: String, cycleLength: Int, periodLength: Int, lastPeriod: LocalDate) =
        viewModelScope.launch {
            settingsRepo.completeOnboarding(name, cycleLength, periodLength, lastPeriod)
            repo.addCycle(lastPeriod)
        }

    fun setProfile(name: String, cycleLength: Int, periodLength: Int) =
        viewModelScope.launch { settingsRepo.setProfile(name, cycleLength, periodLength) }

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { settingsRepo.setThemeMode(mode) }

    fun markPeriodStart(date: LocalDate) = viewModelScope.launch { repo.addCycle(date) }

    fun removePeriodStart(date: LocalDate) = viewModelScope.launch { repo.removeCycle(date) }

    fun saveLog(log: CycleLog) = viewModelScope.launch { repo.saveLog(log) }

    suspend fun logForDate(date: LocalDate): CycleLog? = repo.logForDate(date)

    fun setPregnancy(enabled: Boolean, referenceType: String? = null, referenceDate: LocalDate? = null) =
        viewModelScope.launch { settingsRepo.setPregnancy(enabled, referenceType, referenceDate) }

    fun setAppLock(enabled: Boolean, pin: String?) =
        viewModelScope.launch { settingsRepo.setAppLock(enabled, pin) }

    fun setBiometric(enabled: Boolean) = viewModelScope.launch { settingsRepo.setBiometricUnlock(enabled) }

    fun clearAllData() = viewModelScope.launch {
        repo.clearAll()
        settingsRepo.clearAll()
    }
}
