package com.mensis.app.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mensis.app.Graph
import com.mensis.app.data.Exporter
import com.mensis.app.data.PdfReport
import com.mensis.app.data.ThemeMode
import com.mensis.app.ui.HomeState
import com.mensis.app.ui.MainViewModel
import com.mensis.app.ui.components.DateField
import com.mensis.app.ui.components.MensisCard
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun SettingsScreen(vm: MainViewModel, state: HomeState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val s = state.settings

    var editingProfile by remember { mutableStateOf(false) }
    var settingPin by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    var settingPregnancy by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                runCatching {
                    val json = Exporter.buildJson(state.settings, state.cycles, state.logs)
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                }.onSuccess { Toast.makeText(context, "Esportazione completata", Toast.LENGTH_SHORT).show() }
                    .onFailure { Toast.makeText(context, "Errore nell'esportazione", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        PdfReport.write(
                            os, state.settings, state.cycles, state.logs,
                            state.prediction, state.pregnancy,
                            Graph.engine.cycleLengths(state.cycles)
                        )
                    }
                }.onSuccess { Toast.makeText(context, "PDF per il medico esportato", Toast.LENGTH_SHORT).show() }
                    .onFailure { Toast.makeText(context, "Errore nell'esportazione PDF", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    fun copyJsonForAi() {
        val json = Exporter.buildJson(state.settings, state.cycles, state.logs)
        val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm.setPrimaryClip(android.content.ClipData.newPlainText("Mensis dati", json))
        Toast.makeText(context, "JSON copiato negli appunti", Toast.LENGTH_SHORT).show()
    }

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Impostazioni", style = MaterialTheme.typography.headlineSmall)

        // Profile
        MensisCard {
            Text("Profilo", style = MaterialTheme.typography.titleMedium)
            Text(if (s.userName.isBlank()) "Senza nome" else s.userName, style = MaterialTheme.typography.bodyLarge)
            Text("Ciclo ${s.cycleLength} gg · mestruazione ${s.periodLength} gg", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { editingProfile = true }) { Text("Modifica profilo") }
        }

        // Theme
        MensisCard {
            Text("Tema", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip("Sistema", s.themeMode == ThemeMode.SYSTEM) { vm.setTheme(ThemeMode.SYSTEM) }
                ThemeChip("Chiaro", s.themeMode == ThemeMode.LIGHT) { vm.setTheme(ThemeMode.LIGHT) }
                ThemeChip("Scuro", s.themeMode == ThemeMode.DARK) { vm.setTheme(ThemeMode.DARK) }
            }
        }

        // Pregnancy
        MensisCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Modalità gravidanza", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (s.pregnancyMode) "Attiva" else "Disattivata",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = s.pregnancyMode,
                    onCheckedChange = { checked ->
                        if (checked) settingPregnancy = true else vm.setPregnancy(false)
                    }
                )
            }
        }

        // App lock
        MensisCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Blocco app", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (s.appLockEnabled) "Protetta da PIN" else "Disattivato",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = s.appLockEnabled,
                    onCheckedChange = { checked ->
                        if (checked) settingPin = true else vm.setAppLock(false, null)
                    }
                )
            }
            if (s.appLockEnabled) {
                val canBiometric = remember {
                    BiometricManager.from(context).canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                    ) == BiometricManager.BIOMETRIC_SUCCESS
                }
                if (canBiometric) {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sblocco con biometria", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        Switch(checked = s.biometricUnlockEnabled, onCheckedChange = { vm.setBiometric(it) })
                    }
                }
            }
        }

        // Reminders
        MensisCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Promemoria", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (s.remindersEnabled) "Avvisi su mestruazione, finestra fertile e ovulazione"
                        else "Disattivati",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = s.remindersEnabled,
                    onCheckedChange = { vm.setReminders(it, s.reminderHour) }
                )
            }
            if (s.remindersEnabled) {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Orario dell'avviso", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = { vm.setReminders(true, (s.reminderHour + 23) % 24) }) {
                        Icon(Icons.Filled.Remove, contentDescription = "Ora precedente")
                    }
                    Text(
                        "%02d:00".format(s.reminderHour),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { vm.setReminders(true, (s.reminderHour + 1) % 24) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Ora successiva")
                    }
                }
            }
        }

        // Privacy / data
        MensisCard {
            Text("Dati e privacy", style = MaterialTheme.typography.titleMedium)
            Text(
                "Tutti i tuoi dati restano in locale su questo dispositivo. Nessun cloud, nessun account, nessuna connessione.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { pdfLauncher.launch("mensis-medico-${LocalDate.now()}.pdf") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Esporta PDF per il medico") }
            Spacer(Modifier.height(6.dp))
            OutlinedButton(onClick = { copyJsonForAi() }, modifier = Modifier.fillMaxWidth()) {
                Text("Copia JSON per l'AI")
            }
            Spacer(Modifier.height(6.dp))
            OutlinedButton(
                onClick = { exportLauncher.launch("mensis-backup-${LocalDate.now()}.json") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Esporta dati (JSON)") }
            Spacer(Modifier.height(6.dp))
            OutlinedButton(onClick = { confirmClear = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Cancella tutti i dati")
            }
        }

        MensisCard {
            Text("Avviso legale", style = MaterialTheme.typography.titleMedium)
            Text(
                "Mensis usa un algoritmo statistico-matematico basato sui tuoi dati. Le previsioni sono stime e possono variare: l'app non è un dispositivo medico né un metodo contraccettivo. Per decisioni di salute o contraccezione consulta sempre un professionista.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        MensisCard {
            Text("Crediti immagini", style = MaterialTheme.typography.titleMedium)
            Text(
                "Le illustrazioni dello sviluppo del bambino sono ritagli dalla tavola \"Fetus proposal\" " +
                    "di Wikimedia Commons, in pubblico dominio.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Fonte: commons.wikimedia.org · Pubblico dominio",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            "Mensis 2.0 · tracker locale del ciclo",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (editingProfile) {
        ProfileDialog(
            name = s.userName, cycle = s.cycleLength, period = s.periodLength,
            onDismiss = { editingProfile = false },
            onSave = { n, c, p -> vm.setProfile(n, c, p); editingProfile = false }
        )
    }

    if (settingPin) {
        PinDialog(
            onDismiss = { settingPin = false },
            onConfirm = { pin -> vm.setAppLock(true, pin); settingPin = false }
        )
    }

    if (settingPregnancy) {
        PregnancySetupDialog(
            onDismiss = { settingPregnancy = false },
            onConfirm = { type, date -> vm.setPregnancy(true, type, date); settingPregnancy = false }
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Cancellare tutti i dati?") },
            text = { Text("Questa azione elimina cicli, diario e impostazioni. Non è reversibile.") },
            confirmButton = {
                TextButton(onClick = { vm.clearAllData(); confirmClear = false }) { Text("Cancella") }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Annulla") } }
        )
    }
}

@Composable
private fun ThemeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
private fun ProfileDialog(name: String, cycle: Int, period: Int, onDismiss: () -> Unit, onSave: (String, Int, Int) -> Unit) {
    var n by remember { mutableStateOf(name) }
    var c by remember { mutableStateOf(cycle.toString()) }
    var p by remember { mutableStateOf(period.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifica profilo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(n, { n = it }, label = { Text("Nome") }, singleLine = true)
                OutlinedTextField(c, { c = it.filter(Char::isDigit).take(2) }, label = { Text("Durata ciclo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(p, { p = it.filter(Char::isDigit).take(2) }, label = { Text("Durata mestruazione") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(n.trim(), c.toIntOrNull()?.coerceIn(21, 45) ?: 28, p.toIntOrNull()?.coerceIn(3, 10) ?: 5)
            }) { Text("Salva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}

@Composable
private fun PinDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Imposta un PIN") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(pin, { pin = it.filter(Char::isDigit).take(6) }, label = { Text("PIN (4-6 cifre)") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), singleLine = true)
                OutlinedTextField(confirm, { confirm = it.filter(Char::isDigit).take(6) }, label = { Text("Conferma PIN") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    pin.length < 4 -> error = "Almeno 4 cifre"
                    pin != confirm -> error = "I PIN non coincidono"
                    else -> onConfirm(pin)
                }
            }) { Text("Attiva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}

@Composable
private fun PregnancySetupDialog(onDismiss: () -> Unit, onConfirm: (String, LocalDate) -> Unit) {
    var type by remember { mutableStateOf("last_period") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modalità gravidanza") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Calcola in base a:", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(type == "last_period", { type = "last_period" }, label = { Text("Ultima mestruazione") })
                    FilterChip(type == "conception", { type = "conception" }, label = { Text("Concepimento") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(type == "due_date", { type = "due_date" }, label = { Text("Data presunta parto") })
                }
                DateField(label = "Data", value = date, onChange = { date = it })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(type, date) }) { Text("Attiva") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}
