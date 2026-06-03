package com.mensis.app.ui.pregnancy

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mensis.app.BabyDevelopment
import com.mensis.app.PregnancyContent
import com.mensis.app.PregnancyLibraryEntry
import com.mensis.app.ui.HomeState
import com.mensis.app.ui.components.BabyImage
import com.mensis.app.ui.components.MensisCard
import com.mensis.app.ui.components.StatTile
import com.mensis.app.ui.components.formatIt

/* ----------------------------- Dashboard ----------------------------- */

@Composable
fun PregnancyDashboard(
    state: HomeState,
    onOpenGuide: () -> Unit = {},
    onOpenKick: () -> Unit = {},
    onAddWeight: (Double) -> Unit = {}
) {
    val s = state.pregnancy ?: return
    val baby = BabyDevelopment.forWeek(s.weeks)
    var showWeightDialog by remember { mutableStateOf(false) }
    val weightLogs = remember(state.logs) { state.logs.filter { it.weight != null }.sortedBy { it.date } }
    val lastWeight = weightLogs.lastOrNull()?.weight

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("La tua gravidanza", style = MaterialTheme.typography.headlineSmall)

        // Hero — growing baby + size
        MensisCard {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BabyImage(week = s.weeks, modifier = Modifier.size(220.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Settimana ${s.weeks} + ${s.days} giorni",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            if (baby.fruit.isNotBlank()) {
                Text(
                    "Grande come ${baby.fruit}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (baby.lengthCm > 0) Pill("≈ ${"%.1f".format(baby.lengthCm)} cm")
                if (baby.weightG > 0) {
                    Spacer(Modifier.size(8.dp))
                    Pill(if (baby.weightG >= 1000) "≈ ${"%.1f".format(baby.weightG / 1000.0)} kg" else "≈ ${baby.weightG} g")
                }
            }
        }

        // Trimester progress
        MensisCard {
            Text(s.trimester, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { BabyDevelopment.overallProgress(s.weeks) },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Settimana ${s.weeks} di ${BabyDevelopment.MAX_WEEK}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile(value = "${s.daysToDueDate}", label = "Giorni al termine", modifier = Modifier.weight(1f))
            StatTile(value = "${PregnancyContent.monthFromWeeks(s.weeks)}°", label = "Mese di gravidanza", modifier = Modifier.weight(1f))
        }

        MensisCard {
            Text("Data presunta del parto", style = MaterialTheme.typography.titleMedium)
            Text(s.dueDate.formatIt(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        }

        // "Questa settimana" — fixed vertical layout
        MensisCard {
            Text("Questa settimana", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Block("Il bambino", baby.highlight)
            Block("Per te", PregnancyContent.headline(s))
            PregnancyContent.weekChecklist(s).forEach { Block(it.first, it.second) }
        }

        // Peso in gravidanza
        MensisCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Andamento del peso", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (lastWeight != null) "Ultimo: ${"%.1f".format(lastWeight)} kg" else "Nessun peso registrato",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                androidx.compose.material3.TextButton(onClick = { showWeightDialog = true }) { Text("Aggiungi") }
            }
            Spacer(Modifier.height(8.dp))
            com.mensis.app.ui.components.LineChart(
                points = weightLogs.map { it.date.toString() to it.weight!!.toFloat() },
                unit = " kg"
            )
            if (weightLogs.size >= 2 && lastWeight != null) {
                val gain = lastWeight - weightLogs.first().weight!!
                Spacer(Modifier.height(8.dp))
                Text(
                    "Aumento finora: ${"%+.1f".format(gain)} kg",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Aumento totale consigliato (peso pre-gravidanza normale): circa 11,5–16 kg. " +
                    "Varia in base al peso di partenza: fai riferimento a chi ti segue.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        androidx.compose.material3.OutlinedButton(
            onClick = onOpenKick,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Conta i movimenti del bambino") }

        androidx.compose.material3.OutlinedButton(
            onClick = onOpenGuide,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guida del mese") }
    }

    if (showWeightDialog) {
        var w by remember { mutableStateOf(lastWeight?.let { "%.1f".format(it) } ?: "") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Aggiungi peso di oggi") },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = w,
                    onValueChange = { v -> w = v.filter { it.isDigit() || it == '.' || it == ',' }.take(5) },
                    label = { Text("Peso (kg)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    w.replace(',', '.').toDoubleOrNull()?.let { onAddWeight(it) }
                    showWeightDialog = false
                }) { Text("Salva") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showWeightDialog = false }) { Text("Annulla") }
            }
        )
    }
}

/* -------------------- Pregnancy "calendar" = week explorer -------------------- */

@Composable
fun PregnancyCalendarScreen(state: HomeState) {
    val s = state.pregnancy
    val currentWeek = s?.weeks?.coerceIn(0, BabyDevelopment.MAX_WEEK) ?: 0
    var selected by remember { mutableIntStateOf(currentWeek) }
    val baby = BabyDevelopment.forWeek(selected)

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Le tue settimane", style = MaterialTheme.typography.headlineSmall)

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (0..BabyDevelopment.MAX_WEEK).forEach { w ->
                FilterChip(
                    selected = w == selected,
                    onClick = { selected = w },
                    label = { Text("$w" + if (w == currentWeek) "•" else "") }
                )
            }
        }

        MensisCard {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BabyImage(week = selected, modifier = Modifier.size(190.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Settimana $selected" + if (selected == currentWeek) " (ora)" else "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            if (baby.fruit.isNotBlank()) {
                Text(
                    "Grande come ${baby.fruit}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(baby.highlight, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }

        MensisCard {
            Text("Traguardi", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            MILESTONES.forEach { (week, title) ->
                MilestoneRow(week, title, reached = currentWeek >= week)
            }
        }
    }
}

private val MILESTONES = listOf(
    0 to "Inizio della gravidanza (ultima mestruazione)",
    2 to "Concepimento",
    4 to "Impianto nell'utero",
    6 to "Primo battito cardiaco",
    8 to "Fine della fase embrionale",
    10 to "Inizia il periodo fetale",
    12 to "Fine del primo trimestre",
    13 to "Inizio del secondo trimestre",
    20 to "Metà percorso · ecografia morfologica",
    24 to "Soglia di vitalità",
    27 to "Inizio del terzo trimestre",
    37 to "Termine precoce",
    40 to "Data presunta del parto"
)

@Composable
private fun MilestoneRow(week: Int, title: String, reached: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (reached) Icons.Filled.CheckCircle else Icons.Outlined.Schedule,
            contentDescription = null,
            tint = if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.size(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text("Settimana $week", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/* ----------------------------- Monthly guide ----------------------------- */

@Composable
fun PregnancyGuideScreen(state: HomeState) {
    val s = state.pregnancy
    var month by remember { mutableIntStateOf(s?.let { PregnancyContent.monthFromWeeks(it.weeks) } ?: 1) }

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Guida gravidanza", style = MaterialTheme.typography.headlineSmall)

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..9).forEach { m ->
                FilterChip(selected = m == month, onClick = { month = m }, label = { Text("Mese $m") })
            }
        }

        AdviceCard("Panoramica del mese", PregnancyContent.monthOverview(month))
        AdviceCard("Alimentazione", PregnancyContent.monthFoods(month))
        AdviceCard("Visite ed esami", PregnancyContent.monthVisits(month))
        AdviceCard("Benessere", PregnancyContent.monthWellbeing(month))
        AdviceCard("Checklist", PregnancyContent.monthChecklist(month))

        val entries = PregnancyContent.entriesForMonth(month)
        if (entries.isNotEmpty()) {
            Text("Approfondimenti", style = MaterialTheme.typography.titleLarge)
            entries.forEach { ArticleCard(it) }
        }
    }
}

@Composable
private fun AdviceCard(title: String, items: List<Pair<String, String>>) {
    MensisCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        items.forEach { Block(it.first, it.second) }
    }
}

@Composable
private fun ArticleCard(entry: PregnancyLibraryEntry) {
    MensisCard {
        Text(entry.type, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(entry.title, style = MaterialTheme.typography.titleMedium)
        Text(entry.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        entry.sections.forEach { Block(it.first, it.second) }
    }
}

/* ----------------------------- helpers ----------------------------- */

@Composable
private fun Block(title: String, body: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Pill(text: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}
