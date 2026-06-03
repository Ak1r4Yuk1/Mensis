package com.mensis.app

import java.time.LocalDate

data class CycleRecord(val id: Long, val startDate: LocalDate, val endDate: LocalDate?)

data class CycleLog(
    val id: Long?,
    val date: LocalDate,
    val temperature: Double?,
    val flow: String?,
    val pain: Int?,
    val mucus: String?,
    val mood: String?,
    val notes: String?,
    val symptoms: String?,
    val medications: String?,
    val intercourse: Int?,
    val weight: Double?,
    val sleepHours: Double?,
    val waterGlasses: Int?,
    val activity: String?,
    val lhTestResult: String? = null
)

data class CyclePrediction(
    val currentCycleStart: LocalDate,
    val cycleDay: Int,
    val phase: String,
    val nextPeriodStart: LocalDate,
    val nextPeriodEnd: LocalDate,
    val ovulationDay: LocalDate,
    val fertileStart: LocalDate,
    val fertileEnd: LocalDate,
    val confidence: String,
    val ovulationStatus: String,
    val averageCycleLength: Int,
    val averagePeriodLength: Int
)

data class PregnancySummary(
    val referenceType: String,
    val referenceDate: LocalDate,
    val weeks: Int,
    val days: Int,
    val trimester: String,
    val dueDate: LocalDate,
    val daysToDueDate: Long,
    val babySizeHint: String
)

data class PregnancyLibraryEntry(
    val id: String,
    val month: Int,
    val type: String,
    val title: String,
    val subtitle: String,
    val source: String,
    val sourceUrl: String,
    val youtubeId: String? = null,
    val sections: List<Pair<String, String>>
)
