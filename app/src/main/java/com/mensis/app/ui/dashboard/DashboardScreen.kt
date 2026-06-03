package com.mensis.app.ui.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mensis.app.CycleLog
import com.mensis.app.CyclePrediction
import com.mensis.app.ui.HomeState
import com.mensis.app.ui.components.MensisCard
import com.mensis.app.ui.components.PhaseRing
import com.mensis.app.ui.components.StatTile
import com.mensis.app.ui.components.formatIt
import com.mensis.app.ui.components.phaseColor
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun DashboardScreen(
    state: HomeState,
    onMarkPeriodToday: () -> Unit,
    onLogToday: () -> Unit,
    onOpenInsights: () -> Unit = {},
    onQuickLog: ((CycleLog) -> CycleLog) -> Unit = {}
) {
    val today = LocalDate.now()
    val prediction = state.prediction
    val todayLog = state.logs.firstOrNull { it.date == today }
    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val name = state.settings.userName
        Text(
            if (name.isBlank()) "Ciao 👋" else "Ciao, $name 👋",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (prediction == null) {
            MensisCard {
                Text("In attesa di dati", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Segna l'inizio della tua mestruazione per iniziare a vedere previsioni e fasi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = onMarkPeriodToday, modifier = Modifier.fillMaxWidth()) {
                    Text("Segna mestruazione di oggi")
                }
            }
            return@Column
        }

        // Phase ring
        MensisCard {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val progress = prediction.cycleDay.toFloat() / prediction.averageCycleLength.toFloat()
                PhaseRing(progress = progress, ringColor = phaseColor(prediction.phase)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Giorno ${prediction.cycleDay}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            prediction.phase,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Affidabilità: ${prediction.confidence}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick logging shortcuts (phase-aware)
        MensisCard {
            Text("Registra rapido", style = MaterialTheme.typography.titleMedium)
            Text(
                "Fase ${prediction.phase}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (prediction.phase) {
                    "Mestruale" -> listOf("Scarso", "Medio", "Abbondante").forEach { f ->
                        QuickChip("Flusso: $f", todayLog?.flow == f) { onQuickLog { it.copy(flow = f) } }
                    }
                    "Follicolare" -> listOf("Secco", "Cremoso", "Acquoso", "Filante").forEach { m ->
                        QuickChip("Muco: $m", todayLog?.mucus == m) { onQuickLog { it.copy(mucus = m) } }
                    }
                    "Ovulatoria" -> {
                        listOf("Positivo", "Negativo").forEach { r ->
                            QuickChip("Test LH: $r", todayLog?.lhTestResult == r) { onQuickLog { it.copy(lhTestResult = r) } }
                        }
                        QuickChip("Muco filante", todayLog?.mucus == "Filante") { onQuickLog { it.copy(mucus = "Filante") } }
                    }
                    "Luteale" -> listOf("Bene", "Così così", "Giù").forEach { mo ->
                        QuickChip("Umore: $mo", todayLog?.mood == mo) { onQuickLog { it.copy(mood = mo) } }
                    }
                }
                val hadIntercourse = todayLog?.intercourse == 1
                QuickChip("Rapporto", hadIntercourse) {
                    onQuickLog { it.copy(intercourse = if (hadIntercourse) 0 else 1) }
                }
            }
        }

        // Key stats
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile(
                value = daysLabel(today, prediction.nextPeriodStart),
                label = "Prossima mestruazione",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = daysLabel(today, prediction.ovulationDay),
                label = "Ovulazione (${prediction.ovulationStatus})",
                modifier = Modifier.weight(1f)
            )
        }

        MensisCard {
            Text("Finestra fertile", style = MaterialTheme.typography.titleMedium)
            Text(
                "${prediction.fertileStart.formatIt()} → ${prediction.fertileEnd.formatIt()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                fertileSubtitle(today, prediction),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Upcoming timeline
        MensisCard {
            Text("Prossimi momenti", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            TimelineRow("Mestruazione", prediction.nextPeriodStart, today)
            TimelineRow("Ovulazione", prediction.ovulationDay, today)
            TimelineRow("Inizio finestra fertile", prediction.fertileStart, today)
            Text(
                "Ciclo medio ${prediction.averageCycleLength} gg · mestruazione ${prediction.averagePeriodLength} gg",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onMarkPeriodToday, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("  Mestruazione")
            }
            OutlinedButton(onClick = onLogToday, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Text("  Diario di oggi")
            }
        }

        OutlinedButton(onClick = onOpenInsights, modifier = Modifier.fillMaxWidth()) {
            Text("Statistiche e andamento")
        }
    }
}

@Composable
private fun QuickChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
private fun TimelineRow(title: String, target: LocalDate, today: LocalDate) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(target.formatIt(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            daysLabel(today, target),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun daysLabel(today: LocalDate, target: LocalDate): String {
    val days = ChronoUnit.DAYS.between(today, target)
    return when {
        days == 0L -> "Oggi"
        days == 1L -> "Domani"
        days > 1L -> "tra $days gg"
        days == -1L -> "ieri"
        else -> "${-days} gg fa"
    }
}

private fun fertileSubtitle(today: LocalDate, p: CyclePrediction): String = when {
    !today.isBefore(p.fertileStart) && !today.isAfter(p.fertileEnd) -> "Sei nella finestra fertile."
    today.isBefore(p.fertileStart) -> "La finestra fertile si avvicina."
    else -> "Finestra fertile passata per questo ciclo."
}
