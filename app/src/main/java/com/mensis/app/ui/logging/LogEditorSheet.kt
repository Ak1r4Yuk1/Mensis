package com.mensis.app.ui.logging

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mensis.app.CycleLog
import com.mensis.app.ui.components.formatIt
import java.time.LocalDate

private val FLOW = listOf("Nessuno", "Leggero", "Medio", "Abbondante")
private val MUCUS = listOf("—", "Secco", "Cremoso", "Acquoso", "Filante")
private val MOOD = listOf("—", "Serena", "Felice", "Stanca", "Irritata", "Triste", "Ansiosa")
private val PAIN = listOf("Nessuno", "Lieve", "Medio", "Forte")
private val LH = listOf("—", "Negativo", "Positivo")

private fun clean(value: String, empties: Set<String>): String? =
    value.trim().ifBlank { null }?.takeUnless { it in empties }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogEditorSheet(
    date: LocalDate,
    existing: CycleLog?,
    onDismiss: () -> Unit,
    onSave: (CycleLog) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var temperature by remember { mutableStateOf(existing?.temperature?.toString() ?: "") }
    var flow by remember { mutableStateOf(existing?.flow ?: "Nessuno") }
    var pain by remember { mutableStateOf(existing?.pain ?: 0) }
    var mucus by remember { mutableStateOf(existing?.mucus ?: "—") }
    var lh by remember { mutableStateOf(existing?.lhTestResult ?: "—") }
    var mood by remember { mutableStateOf(existing?.mood ?: "—") }
    var symptoms by remember { mutableStateOf(existing?.symptoms ?: "") }
    var medications by remember { mutableStateOf(existing?.medications ?: "") }
    var intercourse by remember { mutableStateOf((existing?.intercourse ?: 0) == 1) }
    var weight by remember { mutableStateOf(existing?.weight?.toString() ?: "") }
    var sleep by remember { mutableStateOf(existing?.sleepHours?.toString() ?: "") }
    var water by remember { mutableStateOf(existing?.waterGlasses?.toString() ?: "") }
    var activity by remember { mutableStateOf(existing?.activity ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Diario del ${date.formatIt()}", style = MaterialTheme.typography.titleLarge)

            ChipGroup("Flusso", FLOW, flow) { flow = it }
            ChipGroup("Dolore", PAIN, PAIN[pain.coerceIn(0, 3)]) { pain = PAIN.indexOf(it).coerceAtLeast(0) }
            ChipGroup("Muco cervicale", MUCUS, mucus) { mucus = it }
            ChipGroup("Test ovulazione (LH)", LH, lh) { lh = it }
            ChipGroup("Umore", MOOD, mood) { mood = it }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Rapporti", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Switch(checked = intercourse, onCheckedChange = { intercourse = it })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumField("Temp. °C", temperature, Modifier.weight(1f)) { temperature = it }
                NumField("Peso kg", weight, Modifier.weight(1f)) { weight = it }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumField("Sonno h", sleep, Modifier.weight(1f)) { sleep = it }
                NumField("Acqua (bicch.)", water, Modifier.weight(1f), integer = true) { water = it }
            }

            TextField2("Sintomi", symptoms) { symptoms = it }
            TextField2("Farmaci / integratori", medications) { medications = it }
            TextField2("Attività", activity) { activity = it }
            TextField2("Note", notes) { notes = it }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    onSave(
                        CycleLog(
                            id = existing?.id,
                            date = date,
                            temperature = temperature.replace(',', '.').toDoubleOrNull(),
                            flow = clean(flow, setOf("Nessuno")),
                            pain = pain.takeIf { it > 0 },
                            mucus = clean(mucus, setOf("—")),
                            mood = clean(mood, setOf("—")),
                            notes = clean(notes, emptySet()),
                            symptoms = clean(symptoms, emptySet()),
                            medications = clean(medications, emptySet()),
                            intercourse = if (intercourse) 1 else 0,
                            weight = weight.replace(',', '.').toDoubleOrNull(),
                            sleepHours = sleep.replace(',', '.').toDoubleOrNull(),
                            waterGlasses = water.toIntOrNull(),
                            activity = clean(activity, emptySet()),
                            lhTestResult = clean(lh, setOf("—"))
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Salva") }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Annulla") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipGroup(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { opt ->
                FilterChip(
                    selected = opt == selected,
                    onClick = { onSelect(opt) },
                    label = { Text(opt) }
                )
            }
        }
    }
}

@Composable
private fun NumField(label: String, value: String, modifier: Modifier = Modifier, integer: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            val filtered = if (integer) input.filter(Char::isDigit)
            else input.filter { it.isDigit() || it == '.' || it == ',' }
            onChange(filtered)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (integer) KeyboardType.Number else KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
private fun TextField2(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}
