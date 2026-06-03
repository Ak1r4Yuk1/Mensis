package com.mensis.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.mensis.app.CycleLog
import java.time.LocalDate

/**
 * One-time best-effort migration from the legacy hand-rolled SQLite DB ("mensis.db")
 * into the new Room DB + DataStore. Runs only when the new DB is still empty and the
 * legacy file exists. The legacy file is left untouched.
 */
object LegacyImport {

    suspend fun runIfNeeded(
        context: Context,
        repo: MensisRepository,
        settings: SettingsRepository
    ) {
        val legacyFile = context.getDatabasePath("mensis.db")
        if (!legacyFile.exists()) return
        if (repo.cycleCount() > 0) return

        val db = runCatching {
            SQLiteDatabase.openDatabase(legacyFile.path, null, SQLiteDatabase.OPEN_READONLY)
        }.getOrNull() ?: return

        db.use { legacy ->
            // Cycles
            runCatching {
                legacy.rawQuery("SELECT start_date, end_date FROM cycles ORDER BY start_date ASC", null).use { c ->
                    while (c.moveToNext()) {
                        val start = c.getString(0)?.let { LocalDate.parse(it) } ?: continue
                        val end = if (c.isNull(1)) null else c.getString(1)?.let { LocalDate.parse(it) }
                        repo.addCycle(start, end)
                    }
                }
            }

            // Logs
            runCatching {
                legacy.rawQuery(
                    "SELECT date,temperature,flow,pain,mucus,mood,notes,symptoms,medications,intercourse,weight,sleep_hours,water_glasses,activity FROM logs",
                    null
                ).use { c ->
                    while (c.moveToNext()) {
                        val date = c.getString(0)?.let { LocalDate.parse(it) } ?: continue
                        repo.saveLog(
                            CycleLog(
                                id = null,
                                date = date,
                                temperature = c.dbl(1),
                                flow = c.str(2),
                                pain = c.int(3),
                                mucus = c.str(4),
                                mood = c.str(5),
                                notes = c.str(6),
                                symptoms = c.str(7),
                                medications = c.str(8),
                                intercourse = c.int(9),
                                weight = c.dbl(10),
                                sleepHours = c.dbl(11),
                                waterGlasses = c.int(12),
                                activity = c.str(13)
                            )
                        )
                    }
                }
            }

            // Settings
            runCatching {
                val map = mutableMapOf<String, String>()
                legacy.rawQuery("SELECT key, value FROM settings", null).use { c ->
                    while (c.moveToNext()) map[c.getString(0)] = c.getString(1)
                }
                val name = map["userName"] ?: ""
                val cycle = map["cycleLength"]?.toIntOrNull() ?: 28
                val period = map["periodLength"]?.toIntOrNull() ?: 5
                if (map["onboardingDone"] == "true") {
                    val last = map["lastPeriodStart"]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                        ?: LocalDate.now()
                    settings.completeOnboarding(name, cycle, period, last)
                } else {
                    settings.setProfile(name, cycle, period)
                }
                settings.setThemeMode(if (map["themeMode"] == "dark") ThemeMode.DARK else ThemeMode.LIGHT)
                if (map["pregnancyMode"] == "true") {
                    settings.setPregnancy(
                        true,
                        map["pregnancyReferenceType"],
                        map["pregnancyReferenceDate"]?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    )
                }
                if (map["appLockEnabled"] == "true") {
                    settings.setAppLock(true, map["appLockPin"])
                    if (map["biometricUnlockEnabled"] == "true") settings.setBiometricUnlock(true)
                }
            }
        }
    }

    private fun android.database.Cursor.str(i: Int) = if (isNull(i)) null else getString(i)
    private fun android.database.Cursor.int(i: Int) = if (isNull(i)) null else getInt(i)
    private fun android.database.Cursor.dbl(i: Int) = if (isNull(i)) null else getDouble(i)
}
