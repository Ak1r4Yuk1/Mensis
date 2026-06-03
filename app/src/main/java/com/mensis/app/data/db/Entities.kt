package com.mensis.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "cycles")
data class CycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate? = null
)

@Entity(tableName = "kick_sessions")
data class KickSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startEpochMs: Long,
    val count: Int,
    val durationSeconds: Int
)

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey val date: LocalDate,
    val temperature: Double? = null,
    val flow: String? = null,
    val pain: Int? = null,
    val mucus: String? = null,
    val mood: String? = null,
    val notes: String? = null,
    val symptoms: String? = null,
    val medications: String? = null,
    val intercourse: Int? = null,
    val weight: Double? = null,
    val sleepHours: Double? = null,
    val waterGlasses: Int? = null,
    val activity: String? = null,
    val lhTestResult: String? = null
)
