package com.mensis.app.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mensis.app.Graph
import com.mensis.app.ui.HomeState
import com.mensis.app.ui.components.BarChart
import com.mensis.app.ui.components.MensisCard
import com.mensis.app.ui.components.StatRow
import com.mensis.app.ui.components.StatTile
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun InsightsScreen(state: HomeState) {
    val lengths = Graph.engine.cycleLengths(state.cycles)
    val logs = state.logs

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Insights", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile(
                value = if (lengths.isEmpty()) "—" else "${lengths.average().toInt()}",
                label = "Ciclo medio (gg)",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = if (lengths.size < 2) "—" else "±${stdDev(lengths).toInt()}",
                label = "Variabilità",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile(value = "${state.cycles.size}", label = "Cicli registrati", modifier = Modifier.weight(1f))
            StatTile(value = "${logs.size}", label = "Giorni di diario", modifier = Modifier.weight(1f))
        }

        MensisCard {
            Text("Lunghezza dei cicli", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            BarChart(
                data = lengths.takeLast(8).mapIndexed { i, v -> "C${i + 1}" to v.toFloat() },
                unit = ""
            )
        }

        MensisCard {
            Text("Temperatura basale (ultime rilevazioni)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            val temps = logs.mapNotNull { l -> l.temperature?.let { l.date to it } }.takeLast(10)
            if (temps.isEmpty()) {
                Text("Registra la temperatura per vedere il grafico.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                // offset around 36.0 so bars are visible
                BarChart(
                    data = temps.map { it.first.dayOfMonth.toString() to ((it.second - 35.0).coerceAtLeast(0.0) * 10).toFloat() },
                    unit = ""
                )
                Text(
                    "Valori relativi (base 35°C). Min ${"%.1f".format(temps.minOf { it.second })} · max ${"%.1f".format(temps.maxOf { it.second })}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        MensisCard {
            Text("Umori più frequenti", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            val moods = logs.mapNotNull { it.mood }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
            if (moods.isEmpty()) {
                Text("Nessun umore registrato.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                moods.take(5).forEach { StatRow(it.key, "${it.value} volte") }
            }
        }

        MensisCard {
            Text("Sintomi ricorrenti", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            val symptoms = logs.mapNotNull { it.symptoms }
                .flatMap { it.split(",", ";") }
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .groupingBy { it.lowercase() }.eachCount().entries.sortedByDescending { it.value }
            if (symptoms.isEmpty()) {
                Text("Nessun sintomo registrato.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                symptoms.take(5).forEach { StatRow(it.key.replaceFirstChar { c -> c.uppercase() }, "${it.value}") }
            }
        }
    }
}

private fun stdDev(values: List<Int>): Double {
    if (values.size < 2) return 0.0
    val mean = values.average()
    return sqrt(values.sumOf { (it - mean).pow(2) } / (values.size - 1))
}
