package com.mensis.app.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mensis.app.CycleLog
import com.mensis.app.ui.HomeState
import com.mensis.app.ui.MainViewModel
import com.mensis.app.ui.components.CalendarLegend
import com.mensis.app.ui.components.MensisCard
import com.mensis.app.ui.components.MonthCalendar
import com.mensis.app.ui.components.StatRow
import com.mensis.app.ui.components.formatIt
import com.mensis.app.ui.logging.LogEditorSheet
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(vm: MainViewModel, state: HomeState) {
    val today = LocalDate.now()
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf(today) }
    var editing by remember { mutableStateOf<LocalDate?>(null) }
    var editingLog by remember { mutableStateOf<CycleLog?>(null) }

    val logDates = remember(state.logs) { state.logs.map { it.date }.toHashSet() }
    val periodStarts = remember(state.cycles) { state.cycles.map { it.startDate }.toHashSet() }

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Calendario", style = MaterialTheme.typography.headlineSmall)

        MensisCard {
            MonthCalendar(
                month = month,
                today = today,
                selected = selected,
                phaseOf = { vm.phaseForDate(it, state).first },
                hasLog = { it in logDates },
                onPrev = { month = month.minusMonths(1) },
                onNext = { month = month.plusMonths(1) },
                onSelect = { selected = it }
            )
            CalendarLegend()
        }

        val (phase, cycleDay) = vm.phaseForDate(selected, state)
        val selectedLog = state.logs.firstOrNull { it.date == selected }
        MensisCard {
            Text(selected.formatIt(), style = MaterialTheme.typography.titleLarge)
            Text(
                "$phase · giorno ciclo $cycleDay",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (selectedLog != null) {
                selectedLog.flow?.let { StatRow("Flusso", it) }
                selectedLog.mood?.let { StatRow("Umore", it) }
                selectedLog.mucus?.let { StatRow("Muco", it) }
                selectedLog.temperature?.let { StatRow("Temperatura", "$it °C") }
                selectedLog.pain?.let { StatRow("Dolore", painLabel(it)) }
                selectedLog.symptoms?.let { StatRow("Sintomi", it) }
                if (selectedLog.intercourse == 1) StatRow("Rapporti", "Sì")
                selectedLog.notes?.let { StatRow("Note", it) }
            } else {
                Text(
                    "Nessun dato registrato per questo giorno.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { editing = selected; editingLog = selectedLog }, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedLog != null) "Modifica diario" else "Aggiungi diario")
            }
            Spacer(Modifier.height(6.dp))
            if (selected in periodStarts) {
                OutlinedButton(onClick = { vm.removePeriodStart(selected) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Rimuovi inizio mestruazione")
                }
            } else {
                OutlinedButton(onClick = { vm.markPeriodStart(selected) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Segna inizio mestruazione")
                }
            }
        }
    }

    val editDate = editing
    if (editDate != null) {
        // refresh log from db on open
        LaunchedEffect(editDate) { editingLog = vm.logForDate(editDate) }
        LogEditorSheet(
            date = editDate,
            existing = editingLog,
            onDismiss = { editing = null },
            onSave = { vm.saveLog(it); editing = null }
        )
    }
}

private fun painLabel(p: Int) = listOf("Nessuno", "Lieve", "Medio", "Forte").getOrElse(p) { "—" }
