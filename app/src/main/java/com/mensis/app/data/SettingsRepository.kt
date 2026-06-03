package com.mensis.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class Settings(
    val userName: String = "",
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val lastPeriodStart: LocalDate? = null,
    val onboardingDone: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val pregnancyMode: Boolean = false,
    val pregnancyReferenceType: String = "last_period",
    val pregnancyReferenceDate: LocalDate? = null,
    val appLockEnabled: Boolean = false,
    val appLockPin: String? = null,
    val biometricUnlockEnabled: Boolean = false,
    val remindersEnabled: Boolean = true,
    val reminderHour: Int = 9
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mensis_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val userName = stringPreferencesKey("userName")
        val cycleLength = intPreferencesKey("cycleLength")
        val periodLength = intPreferencesKey("periodLength")
        val lastPeriodStart = stringPreferencesKey("lastPeriodStart")
        val onboardingDone = booleanPreferencesKey("onboardingDone")
        val themeMode = stringPreferencesKey("themeMode")
        val pregnancyMode = booleanPreferencesKey("pregnancyMode")
        val pregnancyReferenceType = stringPreferencesKey("pregnancyReferenceType")
        val pregnancyReferenceDate = stringPreferencesKey("pregnancyReferenceDate")
        val appLockEnabled = booleanPreferencesKey("appLockEnabled")
        val appLockPin = stringPreferencesKey("appLockPin")
        val biometricUnlockEnabled = booleanPreferencesKey("biometricUnlockEnabled")
        val remindersEnabled = booleanPreferencesKey("remindersEnabled")
        val reminderHour = intPreferencesKey("reminderHour")
    }

    val settings: Flow<Settings> = context.dataStore.data.map { p ->
        Settings(
            userName = p[Keys.userName] ?: "",
            cycleLength = (p[Keys.cycleLength] ?: 28).coerceIn(21, 45),
            periodLength = (p[Keys.periodLength] ?: 5).coerceIn(3, 10),
            lastPeriodStart = p[Keys.lastPeriodStart]?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            onboardingDone = p[Keys.onboardingDone] ?: false,
            themeMode = runCatching { ThemeMode.valueOf(p[Keys.themeMode] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM),
            pregnancyMode = p[Keys.pregnancyMode] ?: false,
            pregnancyReferenceType = p[Keys.pregnancyReferenceType] ?: "last_period",
            pregnancyReferenceDate = p[Keys.pregnancyReferenceDate]?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            appLockEnabled = p[Keys.appLockEnabled] ?: false,
            appLockPin = p[Keys.appLockPin],
            biometricUnlockEnabled = p[Keys.biometricUnlockEnabled] ?: false,
            remindersEnabled = p[Keys.remindersEnabled] ?: true,
            reminderHour = (p[Keys.reminderHour] ?: 9).coerceIn(0, 23)
        )
    }

    suspend fun completeOnboarding(name: String, cycleLength: Int, periodLength: Int, lastPeriodStart: LocalDate) {
        context.dataStore.edit { p ->
            p[Keys.userName] = name
            p[Keys.cycleLength] = cycleLength.coerceIn(21, 45)
            p[Keys.periodLength] = periodLength.coerceIn(3, 10)
            p[Keys.lastPeriodStart] = lastPeriodStart.toString()
            p[Keys.onboardingDone] = true
        }
    }

    suspend fun setProfile(name: String, cycleLength: Int, periodLength: Int) {
        context.dataStore.edit { p ->
            p[Keys.userName] = name
            p[Keys.cycleLength] = cycleLength.coerceIn(21, 45)
            p[Keys.periodLength] = periodLength.coerceIn(3, 10)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun setPregnancy(enabled: Boolean, referenceType: String?, referenceDate: LocalDate?) {
        context.dataStore.edit { p ->
            p[Keys.pregnancyMode] = enabled
            if (referenceType != null) p[Keys.pregnancyReferenceType] = referenceType
            if (referenceDate != null) p[Keys.pregnancyReferenceDate] = referenceDate.toString()
        }
    }

    suspend fun setAppLock(enabled: Boolean, pin: String?) {
        context.dataStore.edit { p ->
            p[Keys.appLockEnabled] = enabled
            if (pin != null) p[Keys.appLockPin] = pin
            if (!enabled) {
                p.remove(Keys.appLockPin)
                p[Keys.biometricUnlockEnabled] = false
            }
        }
    }

    suspend fun setBiometricUnlock(enabled: Boolean) {
        context.dataStore.edit { it[Keys.biometricUnlockEnabled] = enabled }
    }

    suspend fun setReminders(enabled: Boolean, hour: Int) {
        context.dataStore.edit { p ->
            p[Keys.remindersEnabled] = enabled
            p[Keys.reminderHour] = hour.coerceIn(0, 23)
        }
    }

    suspend fun putRaw(key: String, value: String) {
        context.dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
