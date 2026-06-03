package com.mensis.app.data

import com.mensis.app.CycleLog
import com.mensis.app.CycleRecord
import com.mensis.app.data.db.CycleEntity
import com.mensis.app.data.db.CycleDao
import com.mensis.app.data.db.KickDao
import com.mensis.app.data.db.KickSessionEntity
import com.mensis.app.data.db.LogDao
import com.mensis.app.data.db.LogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/** Sessione di conteggio dei movimenti fetali. */
data class KickSession(
    val id: Long,
    val start: LocalDateTime,
    val count: Int,
    val durationSeconds: Int
)

class MensisRepository(
    private val cycleDao: CycleDao,
    private val logDao: LogDao,
    private val kickDao: KickDao
) {
    val cycles: Flow<List<CycleRecord>> = cycleDao.observeAll().map { list -> list.map { it.toRecord() } }
    val logs: Flow<List<CycleLog>> = logDao.observeAll().map { list -> list.map { it.toLog() } }
    val kicks: Flow<List<KickSession>> = kickDao.observeAll().map { list -> list.map { it.toSession() } }

    suspend fun saveKick(count: Int, durationSeconds: Int) =
        kickDao.insert(KickSessionEntity(startEpochMs = System.currentTimeMillis(), count = count, durationSeconds = durationSeconds))

    suspend fun deleteKick(id: Long) = kickDao.delete(id)

    suspend fun cyclesOnce(): List<CycleRecord> = cycleDao.getAll().map { it.toRecord() }
    suspend fun logsOnce(): List<CycleLog> = logDao.getAll().map { it.toLog() }
    suspend fun logForDate(date: LocalDate): CycleLog? = logDao.getForDate(date)?.toLog()
    suspend fun cycleCount(): Int = cycleDao.count()

    suspend fun addCycle(start: LocalDate, end: LocalDate? = null) {
        if (cycleDao.getAll().none { it.startDate == start }) {
            cycleDao.insert(CycleEntity(startDate = start, endDate = end))
        }
    }

    suspend fun removeCycle(start: LocalDate) = cycleDao.deleteByStartDate(start)

    suspend fun saveLog(log: CycleLog) = logDao.upsert(log.toEntity())

    suspend fun deleteLog(date: LocalDate) = logDao.deleteByDate(date)

    suspend fun clearAll() {
        cycleDao.clear()
        logDao.clear()
        kickDao.clear()
    }
}

private fun CycleEntity.toRecord() = CycleRecord(id = id, startDate = startDate, endDate = endDate)

private fun KickSessionEntity.toSession() = KickSession(
    id = id,
    start = Instant.ofEpochMilli(startEpochMs).atZone(ZoneId.systemDefault()).toLocalDateTime(),
    count = count,
    durationSeconds = durationSeconds
)

private fun LogEntity.toLog() = CycleLog(
    id = null,
    date = date,
    temperature = temperature,
    flow = flow,
    pain = pain,
    mucus = mucus,
    mood = mood,
    notes = notes,
    symptoms = symptoms,
    medications = medications,
    intercourse = intercourse,
    weight = weight,
    sleepHours = sleepHours,
    waterGlasses = waterGlasses,
    activity = activity,
    lhTestResult = lhTestResult
)

private fun CycleLog.toEntity() = LogEntity(
    date = date,
    temperature = temperature,
    flow = flow,
    pain = pain,
    mucus = mucus,
    mood = mood,
    notes = notes,
    symptoms = symptoms,
    medications = medications,
    intercourse = intercourse,
    weight = weight,
    sleepHours = sleepHours,
    waterGlasses = waterGlasses,
    activity = activity,
    lhTestResult = lhTestResult
)
