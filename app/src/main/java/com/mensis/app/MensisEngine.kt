package com.mensis.app

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class MensisEngine {
    private val defaultCycleLength = 28
    private val defaultPeriodLength = 5
    private val lutealPhaseLength = 14L
    private val tempRiseThreshold = 0.2
    private val baselineLookbackDays = 6
    private val confirmationDays = 3

    fun predict(
        today: LocalDate,
        cycles: List<CycleRecord>,
        logs: List<CycleLog>,
        preferredCycleLength: Int?,
        preferredPeriodLength: Int?
    ): CyclePrediction? {
        val sortedCycles = cycles.sortedBy { it.startDate }
        val currentCycleStart = sortedCycles.lastOrNull { !it.startDate.isAfter(today) }?.startDate
        ?: sortedCycles.lastOrNull()?.startDate
        ?: return null
        val lengths = cycleLengths(sortedCycles)
        val averageCycleLength = when {
            lengths.isNotEmpty() -> round(lengths.average()).toInt()
            preferredCycleLength != null -> preferredCycleLength
            else -> defaultCycleLength
        }.coerceIn(21, 45)
        val averagePeriodLength = (preferredPeriodLength ?: defaultPeriodLength).coerceIn(3, 10)
        val cycleDay = max(1, ChronoUnit.DAYS.between(currentCycleStart, today).toInt() + 1)
        val nextPeriodStart = currentCycleStart.plusDays(averageCycleLength.toLong())
        val ovulationDay = nextPeriodStart.minusDays(lutealPhaseLength)

        // CORREZIONE: La finestra fertile si chiude il giorno dell'ovulazione (6 giorni totali inclusivi)
        val fertileStart = ovulationDay.minusDays(5)
        val fertileEnd = ovulationDay

        val confidence = confidence(lengths)
        return CyclePrediction(
            currentCycleStart = currentCycleStart,
            cycleDay = cycleDay,
            phase = determinePhase(today, currentCycleStart, averageCycleLength, averagePeriodLength, ovulationDay, logs),
                               nextPeriodStart = nextPeriodStart,
                               nextPeriodEnd = nextPeriodStart.plusDays((averagePeriodLength - 1).toLong()),
                               ovulationDay = ovulationDay,
                               fertileStart = fertileStart,
                               fertileEnd = fertileEnd,
                               confidence = confidence,
                               ovulationStatus = detectOvulation(logs, ovulationDay),
                               averageCycleLength = averageCycleLength,
                               averagePeriodLength = averagePeriodLength
        )
    }

    /**
     * Fase (e giorno del ciclo) per una data qualsiasi. Le previsioni si ripetono
     * ciclo-per-ciclo sia in avanti (oltre l'ultima mestruazione) sia all'indietro (prima
     * della prima registrata), così il calendario è colorato anche a ±6 mesi e oltre.
     * Dentro un intervallo realmente registrato si usa la durata effettiva.
     */
    fun phaseForDate(
        date: LocalDate,
        cycles: List<CycleRecord>,
        logs: List<CycleLog>,
        preferredCycleLength: Int?,
        preferredPeriodLength: Int?
    ): Pair<String, Int> {
        val sorted = cycles.sortedBy { it.startDate }
        if (sorted.isEmpty()) return "In attesa dati" to 0

        val lengths = cycleLengths(sorted)
        val averageCycleLength = when {
            lengths.isNotEmpty() -> round(lengths.average()).toInt()
            preferredCycleLength != null -> preferredCycleLength
            else -> defaultCycleLength
        }.coerceIn(21, 45)
        val periodLength = (preferredPeriodLength ?: defaultPeriodLength).coerceIn(3, 10)

        val prev = sorted.lastOrNull { !it.startDate.isAfter(date) }?.startDate
        val next = sorted.firstOrNull { it.startDate.isAfter(date) }?.startDate

        val cycleStart: LocalDate
        val cycleLength: Int
        when {
            // Dentro un intervallo realmente registrato: usa la durata effettiva.
            prev != null && next != null -> {
                cycleStart = prev
                cycleLength = ChronoUnit.DAYS.between(prev, next).toInt().coerceAtLeast(periodLength + 2)
            }
            // Da oggi in avanti (oltre l'ultima mestruazione): proietta ripetendo la media.
            prev != null -> {
                val k = ChronoUnit.DAYS.between(prev, date).toInt() / averageCycleLength
                cycleStart = prev.plusDays(k.toLong() * averageCycleLength)
                cycleLength = averageCycleLength
            }
            // Prima della prima mestruazione registrata: proietta all'indietro.
            else -> {
                val first = sorted.first().startDate
                val daysBefore = ChronoUnit.DAYS.between(date, first).toInt()
                val k = ceil(daysBefore.toDouble() / averageCycleLength).toInt()
                cycleStart = first.minusDays(k.toLong() * averageCycleLength)
                cycleLength = averageCycleLength
            }
        }

        val cycleDay = max(1, ChronoUnit.DAYS.between(cycleStart, date).toInt() + 1)
        val ovulationDay = cycleStart.plusDays((cycleLength - lutealPhaseLength).toLong())
        val phase = determinePhase(date, cycleStart, cycleLength, periodLength, ovulationDay, logs)
        return phase to cycleDay
    }

    fun cycleLengths(cycles: List<CycleRecord>): List<Int> {
        val sorted = cycles.sortedBy { it.startDate }
        if (sorted.size < 2) return emptyList()
            return sorted.zipWithNext()
            .map { (a, b) -> ChronoUnit.DAYS.between(a.startDate, b.startDate).toInt() }
            .filter { it in 20..60 }
    }

    fun pregnancySummary(today: LocalDate, referenceType: String, referenceDate: LocalDate): PregnancySummary {
        val dueDate = when (referenceType) {
            "due_date" -> referenceDate
            "conception" -> referenceDate.plusDays(266)
            else -> referenceDate.plusDays(280)
        }
        val gestationStart = when (referenceType) {
            "due_date" -> referenceDate.minusDays(280)
            "conception" -> referenceDate.minusDays(14)
            else -> referenceDate
        }
        val totalDays = max(0, ChronoUnit.DAYS.between(gestationStart, today).toInt())
        val weeks = totalDays / 7
        val days = totalDays % 7
        val trimester = when {
            weeks < 14 -> "Primo trimestre"
            weeks < 28 -> "Secondo trimestre"
            else -> "Terzo trimestre"
        }
        return PregnancySummary(
            referenceType = referenceType,
            referenceDate = referenceDate,
            weeks = weeks,
            days = days,
            trimester = trimester,
            dueDate = dueDate,
            daysToDueDate = ChronoUnit.DAYS.between(today, dueDate),
                                babySizeHint = sizeHint(weeks)
        )
    }

    private fun sizeHint(weeks: Int): String = when {
        weeks < 6 -> "come un seme di mela"
        weeks < 10 -> "come un chicco d'uva"
        weeks < 14 -> "come un limone"
        weeks < 18 -> "come un avocado"
        weeks < 22 -> "come una banana"
        weeks < 28 -> "come una melanzana"
        weeks < 34 -> "come un melone piccolo"
        weeks < 38 -> "come un ananas"
        else -> "pronto ad arrivare"
    }

    private fun confidence(lengths: List<Int>): String {
        if (lengths.size < 2) return "Media"
            val standardDeviation = standardDeviation(lengths)
            return when {
                standardDeviation <= 2.0 -> "Alta"
                standardDeviation <= 5.0 -> "Media"
                else -> "Bassa"
            }
    }

    private fun detectOvulation(logs: List<CycleLog>, predictedOvulation: LocalDate): String {
        // Conferma via test LH positivo a ridosso dell'ovulazione stimata.
        val lhConfirmed = logs.any {
            it.lhTestResult.equals("Positivo", ignoreCase = true) &&
                abs(ChronoUnit.DAYS.between(it.date, predictedOvulation)) <= 2
        }
        if (lhConfirmed) return "Confermata"
        val withTemp = logs.filter { it.temperature != null }.sortedBy { it.date }
        if (withTemp.size < baselineLookbackDays + confirmationDays) return "Stimata"
            for (i in baselineLookbackDays until withTemp.size) {
                val baseline = withTemp.subList(i - baselineLookbackDays, i).mapNotNull { it.temperature }
                val rise = withTemp.drop(i).take(confirmationDays)
                if (rise.size < confirmationDays) break
                    val baselineAverage = average(baseline)
                    if (rise.all { ((it.temperature ?: 0.0) - baselineAverage) >= tempRiseThreshold }) {
                        val distance = abs(ChronoUnit.DAYS.between(withTemp[i].date, predictedOvulation))
                        if (distance <= 3) return "Confermata"
                    }
            }
            return "Stimata"
    }

    private fun determinePhase(
        date: LocalDate,
        cycleStart: LocalDate,
        cycleLength: Int,
        periodLength: Int,
        ovulationDay: LocalDate,
        logs: List<CycleLog>
    ): String {
        val day = max(1, ChronoUnit.DAYS.between(cycleStart, date).toInt() + 1)
        val ovulationStatus = detectOvulation(logs, ovulationDay)
        return when {
            day <= periodLength -> "Mestruale"
            // CORREZIONE: La fase ovulatoria è circoscritta al picco reale (giorno prima e giorno stesso dell'ovulazione)
            date == ovulationDay || date == ovulationDay.minusDays(1) -> "Ovulatoria"
            date.isBefore(ovulationDay) -> "Follicolare"
            ovulationStatus == "Confermata" || day <= cycleLength -> "Luteale"
            else -> "Follicolare"
        }
    }

    private fun average(nums: List<Number>) = if (nums.isEmpty()) 0.0 else nums.sumOf { it.toDouble() } / nums.size

        private fun standardDeviation(nums: List<Int>): Double {
            if (nums.size < 2) return 0.0
                val average = average(nums)
                return sqrt(nums.sumOf { (it - average).pow(2) } / (nums.size - 1))
        }
}
