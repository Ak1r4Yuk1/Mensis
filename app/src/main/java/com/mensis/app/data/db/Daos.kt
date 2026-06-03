package com.mensis.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycles ORDER BY startDate ASC")
    fun observeAll(): Flow<List<CycleEntity>>

    @Query("SELECT * FROM cycles ORDER BY startDate ASC")
    suspend fun getAll(): List<CycleEntity>

    @Query("SELECT COUNT(*) FROM cycles")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cycle: CycleEntity)

    @Query("DELETE FROM cycles WHERE startDate = :date")
    suspend fun deleteByStartDate(date: LocalDate)

    @Query("DELETE FROM cycles")
    suspend fun clear()
}

@Dao
interface KickDao {
    @Query("SELECT * FROM kick_sessions ORDER BY startEpochMs DESC")
    fun observeAll(): Flow<List<KickSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: KickSessionEntity)

    @Query("DELETE FROM kick_sessions WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM kick_sessions")
    suspend fun clear()
}

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY date ASC")
    fun observeAll(): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs ORDER BY date ASC")
    suspend fun getAll(): List<LogEntity>

    @Query("SELECT * FROM logs WHERE date = :date LIMIT 1")
    suspend fun getForDate(date: LocalDate): LogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: LogEntity)

    @Query("DELETE FROM logs WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)

    @Query("DELETE FROM logs")
    suspend fun clear()
}
