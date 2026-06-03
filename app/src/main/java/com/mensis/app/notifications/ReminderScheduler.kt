package com.mensis.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mensis.app.CyclePrediction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Programma i promemoria sui prossimi eventi del ciclo (mestruazione, finestra fertile,
 * ovulazione) a partire dalle previsioni dell'engine. Usa allarmi inesatti (nessun permesso
 * speciale): per un avviso giornaliero un piccolo scarto è accettabile. Va richiamato a ogni
 * avvio e quando i dati cambiano: cancella e riprograramma per restare allineato.
 */
object ReminderScheduler {

    private const val REQ_BASE = 7000

    private data class Event(val id: Int, val date: LocalDate, val title: String, val text: String)

    fun reschedule(
        context: Context,
        prediction: CyclePrediction?,
        pregnancyActive: Boolean,
        enabled: Boolean,
        hour: Int
    ) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return

        // Cancella sempre i 4 promemoria precedenti.
        for (id in 0..3) am.cancel(pendingIntent(context, id, null))

        // Disattivati, in gravidanza o senza previsione: solo cancellazione.
        if (!enabled || pregnancyActive || prediction == null) return

        val notifyTime = LocalTime.of(hour.coerceIn(0, 23), 0)
        val now = LocalDateTime.now()
        val events = listOf(
            Event(0, prediction.nextPeriodStart.minusDays(1), "Mestruazione in arrivo",
                "Domani potrebbe iniziare la mestruazione. Tieni pronto l'occorrente."),
            Event(1, prediction.nextPeriodStart, "Mestruazione prevista oggi",
                "Secondo le tue previsioni oggi dovrebbe iniziare la mestruazione."),
            Event(2, prediction.fertileStart, "Inizia la finestra fertile",
                "Da oggi inizia la tua finestra fertile."),
            Event(3, prediction.ovulationDay, "Ovulazione prevista oggi",
                "Oggi è il giorno stimato dell'ovulazione.")
        )

        for (ev in events) {
            val triggerAt = LocalDateTime.of(ev.date, notifyTime)
            if (triggerAt.isAfter(now)) {
                val millis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent(context, ev.id, ev))
            }
        }
    }

    private fun pendingIntent(context: Context, id: Int, event: Event?): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        if (event != null) {
            intent.putExtra(ReminderReceiver.EXTRA_ID, event.id)
            intent.putExtra(ReminderReceiver.EXTRA_TITLE, event.title)
            intent.putExtra(ReminderReceiver.EXTRA_TEXT, event.text)
        }
        return PendingIntent.getBroadcast(
            context, REQ_BASE + id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
