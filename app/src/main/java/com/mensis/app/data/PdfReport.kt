package com.mensis.app.data

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.mensis.app.CycleLog
import com.mensis.app.CyclePrediction
import com.mensis.app.CycleRecord
import com.mensis.app.PregnancySummary
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Genera un PDF riepilogativo "per il medico" curato: intestazione colorata, sezioni con
 * titolo e filetto, righe etichetta/valore allineate, elenchi puntati e piè di pagina con
 * numero pagina. Usa [PdfDocument] della piattaforma — nessuna dipendenza esterna.
 */
object PdfReport {

    private val itDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private const val ACCENT = 0xFFE11D48.toInt()
    private const val INK = 0xFF0F172A.toInt()
    private const val SOFT = 0xFF64748B.toInt()
    private const val RULE = 0xFFE2E8F0.toInt()
    private const val WHITE = 0xFFFFFFFF.toInt()

    fun write(
        out: OutputStream,
        settings: Settings,
        cycles: List<CycleRecord>,
        logs: List<CycleLog>,
        prediction: CyclePrediction?,
        pregnancy: PregnancySummary?,
        cycleLengths: List<Int>
    ) {
        val pageW = 595
        val pageH = 842
        val left = 48f
        val right = pageW - 48f
        val contentW = right - left
        val footerY = pageH - 44f

        val pTitle = paint(WHITE, 21f, bold = true)
        val pBandSub = paint(WHITE, 10.5f).also { it.alpha = 220 }
        val pH = paint(ACCENT, 13f, bold = true)
        val pLabel = paint(SOFT, 10.5f)
        val pVal = paint(INK, 10.5f)
        val pBody = paint(INK, 10.5f)
        val pSmall = paint(SOFT, 8.5f)
        val pRule = Paint().apply { color = RULE; strokeWidth = 1f; isAntiAlias = true }
        val pBand = Paint().apply { color = ACCENT; isAntiAlias = true }

        val doc = PdfDocument()
        var pageNum = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create())
        var canvas = page.canvas
        var y = 0f

        fun truncate(text: String, p: Paint, maxW: Float): String {
            if (p.measureText(text) <= maxW) return text
            var t = text
            while (t.isNotEmpty() && p.measureText("$t…") > maxW) t = t.dropLast(1)
            return "$t…"
        }

        fun drawFooter() {
            canvas.drawLine(left, footerY, right, footerY, pRule)
            canvas.drawText(
                "Mensis · documento informativo, non sostituisce un parere medico",
                left, footerY + 14f, pSmall
            )
            val pg = "Pagina $pageNum"
            canvas.drawText(pg, right - pSmall.measureText(pg), footerY + 14f, pSmall)
        }

        fun drawHeader(first: Boolean) {
            if (first) {
                canvas.drawRect(0f, 0f, pageW.toFloat(), 92f, pBand)
                canvas.drawText("Mensis — Riepilogo per il medico", left, 50f, pTitle)
                canvas.drawText("Generato il ${LocalDate.now().format(itDate)}", left, 72f, pBandSub)
                y = 122f
            } else {
                canvas.drawRect(0f, 0f, pageW.toFloat(), 6f, pBand)
                y = 56f
            }
        }

        fun newPage() {
            drawFooter()
            doc.finishPage(page)
            pageNum++
            page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create())
            canvas = page.canvas
            drawHeader(false)
        }

        fun ensure(space: Float) { if (y + space > footerY - 10f) newPage() }

        fun section(title: String) {
            y += 14f
            ensure(pH.textSize + 16f)
            canvas.drawText(title, left, y, pH)
            y += 6f
            canvas.drawLine(left, y, right, y, pRule)
            y += 16f
        }

        fun kv(label: String, value: String) {
            ensure(pVal.textSize + 6f)
            canvas.drawText(label, left, y, pLabel)
            val vx = left + 175f
            canvas.drawText(truncate(value, pVal, right - vx), vx, y, pVal)
            y += pVal.textSize + 6f
        }

        fun bullet(text: String) {
            ensure(pBody.textSize + 4f)
            canvas.drawCircle(left + 2f, y - 3f, 1.7f, pVal)
            canvas.drawText(truncate(text, pBody, contentW - 12f), left + 12f, y, pBody)
            y += pBody.textSize + 4f
        }

        fun note(text: String) {
            ensure(pBody.textSize + 4f)
            canvas.drawText(truncate(text, pBody, contentW), left, y, pBody)
            y += pBody.textSize + 4f
        }

        drawHeader(true)

        section("Profilo")
        kv("Nome", settings.userName.ifBlank { "non indicato" })
        kv("Durata media ciclo", "${settings.cycleLength} giorni")
        kv("Durata media mestruazione", "${settings.periodLength} giorni")

        if (pregnancy != null) {
            section("Gravidanza")
            kv("Età gestazionale", "${pregnancy.weeks} settimane e ${pregnancy.days} giorni")
            kv("Trimestre", pregnancy.trimester)
            kv("Data presunta del parto", pregnancy.dueDate.format(itDate))
            kv("Giorni al termine", "${pregnancy.daysToDueDate}")
        } else if (prediction != null) {
            section("Quadro del ciclo (stime statistiche)")
            kv("Giorno del ciclo", "${prediction.cycleDay}")
            kv("Fase attuale", prediction.phase)
            kv("Durata media ciclo (calcolata)", "${prediction.averageCycleLength} giorni")
            kv("Durata media mestruazione", "${prediction.averagePeriodLength} giorni")
            kv("Prossima mestruazione", prediction.nextPeriodStart.format(itDate))
            kv("Ovulazione (${prediction.ovulationStatus.lowercase()})", prediction.ovulationDay.format(itDate))
            kv("Finestra fertile", "${prediction.fertileStart.format(itDate)} – ${prediction.fertileEnd.format(itDate)}")
            kv("Affidabilità della stima", prediction.confidence)
        } else {
            section("Quadro del ciclo")
            note("Dati insufficienti per una previsione.")
        }

        section("Storico cicli")
        if (cycles.isEmpty()) {
            note("Nessun ciclo registrato.")
        } else {
            if (cycleLengths.isNotEmpty()) {
                kv("Lunghezze recenti (gg)", cycleLengths.takeLast(12).joinToString(", "))
                y += 4f
            }
            cycles.sortedByDescending { it.startDate }.take(24).forEach { c ->
                bullet(
                    "Mestruazione dal ${c.startDate.format(itDate)}" +
                        (c.endDate?.let { " al ${it.format(itDate)}" } ?: "")
                )
            }
        }

        section("Diario recente")
        val recent = logs.sortedByDescending { it.date }.take(30)
        if (recent.isEmpty()) {
            note("Nessuna annotazione.")
        } else {
            recent.forEach { l ->
                val parts = buildList {
                    l.flow?.let { add("flusso $it") }
                    l.pain?.let { add("dolore $it/3") }
                    l.mucus?.let { add("muco $it") }
                    l.temperature?.let { add("T $it °C") }
                    l.lhTestResult?.let { add("LH $it") }
                    l.mood?.let { add("umore $it") }
                    l.weight?.let { add("peso $it kg") }
                    l.symptoms?.takeIf { it.isNotBlank() }?.let { add("sintomi: $it") }
                    if (l.intercourse == 1) add("rapporti")
                }
                bullet("${l.date.format(itDate)} — ${parts.joinToString(", ").ifBlank { "—" }}")
            }
        }

        drawFooter()
        doc.finishPage(page)
        doc.writeTo(out)
        doc.close()
    }

    private fun paint(color: Int, size: Float, bold: Boolean = false) = Paint().apply {
        this.color = color
        textSize = size
        isFakeBoldText = bold
        isAntiAlias = true
    }
}
