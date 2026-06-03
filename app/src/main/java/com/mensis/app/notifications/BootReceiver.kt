package com.mensis.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mensis.app.Graph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Dopo un riavvio gli allarmi vengono cancellati: li riprogramma leggendo i dati locali. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Graph.provide(context)
                val settings = Graph.settings.settings.first()
                val cycles = Graph.repository.cyclesOnce()
                val logs = Graph.repository.logsOnce()
                val prediction = Graph.engine.predict(
                    today = LocalDate.now(),
                    cycles = cycles,
                    logs = logs,
                    preferredCycleLength = settings.cycleLength,
                    preferredPeriodLength = settings.periodLength
                )
                val pregnancyActive = settings.pregnancyMode && settings.pregnancyReferenceDate != null
                ReminderScheduler.reschedule(
                    context, prediction, pregnancyActive,
                    settings.remindersEnabled, settings.reminderHour
                )
            } finally {
                pending.finish()
            }
        }
    }
}
