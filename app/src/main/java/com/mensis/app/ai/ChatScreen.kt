package com.mensis.app.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mensis.app.BabyDevelopment
import com.mensis.app.Graph
import com.mensis.app.ui.HomeState
import kotlinx.coroutines.launch

private data class ChatMsg(val fromUser: Boolean, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(state: HomeState, onBack: () -> Unit) {
    val llm = Graph.llm
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMsg>() }
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val systemInstruction = remember(state) { buildSystemInstruction(state) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var dl by remember { mutableStateOf(ModelDownloader.status(context)) }
    LaunchedEffect(Unit) {
        while (dl.state != ModelDownloader.State.DONE) {
            dl = ModelDownloader.status(context)
            kotlinx.coroutines.delay(1000)
        }
    }
    val modelReady = dl.state == ModelDownloader.State.DONE

    // Monta il modello quando la chat è aperta, lo smonta alla chiusura → app reattiva.
    DisposableEffect(modelReady) {
        if (modelReady) llm.ensureLoaded()
        onDispose { llm.release() }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assistente Mensis") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            if (!modelReady) {
                ModelDownloadGate(
                    dl = dl,
                    onStart = {
                        ModelDownloader.ensureDownloading(context)
                        dl = ModelDownloader.status(context)
                    }
                )
                return@Column
            }

            Spacer(Modifier.height(12.dp))

            if (messages.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Ciao! Sono il tuo assistente locale. Chiedimi del tuo ciclo, dei sintomi o di come stai. (Non sono un medico.)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(messages) { _, m -> Bubble(m) }
                }
            }

            if (busy) {
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.padding(end = 10.dp))
                    Text("Sto pensando…", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Scrivi un messaggio…") },
                    enabled = !busy
                )
                IconButton(
                    onClick = {
                        val q = input.trim()
                        if (q.isEmpty() || busy) return@IconButton
                        messages.add(ChatMsg(true, q))
                        input = ""
                        busy = true
                        scope.launch {
                            val answer = runCatching { llm.ask(systemInstruction, q) }
                                .getOrElse { "Mi dispiace, si è verificato un errore: ${it.message}" }
                            messages.add(ChatMsg(false, answer.ifBlank { "(nessuna risposta)" }))
                            busy = false
                        }
                    },
                    enabled = !busy
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Invia", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun Bubble(m: ChatMsg) {
    val bg = if (m.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (m.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.fromUser) Arrangement.Start else Arrangement.End) {
        Box(
            Modifier.widthIn(max = 300.dp).clip(RoundedCornerShape(16.dp)).background(bg).padding(12.dp)
        ) {
            Text(m.text, style = MaterialTheme.typography.bodyMedium, color = fg)
        }
    }
}

@Composable
private fun ModelDownloadGate(dl: ModelDownloader.Status, onStart: () -> Unit) {
    val mb = 1024.0 * 1024.0
    Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (dl.state) {
                ModelDownloader.State.RUNNING, ModelDownloader.State.PENDING, ModelDownloader.State.PAUSED -> {
                    Text(
                        "Sto scaricando l'assistente AI…",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                    if (dl.totalBytes > 0 && dl.downloadedBytes > 0) {
                        LinearProgressIndicator(progress = { dl.fraction }, modifier = Modifier.fillMaxWidth())
                        Text(
                            "${"%.0f".format(dl.downloadedBytes / mb)} / ${"%.0f".format(dl.totalBytes / mb)} MB",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                    Text(
                        "Il download prosegue anche in background. La chat sarà pronta al termine.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                else -> {
                    Text(
                        "L'assistente AI funziona interamente sul tuo telefono. " +
                            "Serve scaricare una volta il modello (≈ 1,5 GB).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 16.dp))
                    Button(onClick = onStart) { Text("Scarica l'assistente AI") }
                }
            }
        }
    }
}

private val itDate = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun buildSystemInstruction(state: HomeState): String {
    val sb = StringBuilder()
    sb.append("Sei l'assistente dell'app italiana Mensis per il ciclo mestruale e la gravidanza.\n\n")
    sb.append("REGOLE (rispettale sempre):\n")
    sb.append("- Rispondi SEMPRE in italiano, in modo empatico, chiaro e conciso (massimo 4-5 frasi).\n")
    sb.append("- Usa SOLO i dati elencati in 'DATI UTENTE'. NON inventare date, numeri, durate o sintomi non presenti. ")
    sb.append("Se un'informazione non è nei dati, dillo onestamente o dai un consiglio generale senza inventare valori.\n")
    sb.append("- Non sei un medico: per diagnosi, terapie, farmaci o contraccezione invita a consultare un professionista sanitario.\n")
    sb.append("- Usa solo unità metriche con i termini italiani: gradi Celsius (°C), chilogrammi (kg), centimetri (cm), millilitri (ml), settimane, giorni. Niente unità anglosassoni.\n")
    sb.append("- Scrivi le date nel formato gg/mm/aaaa.\n\n")
    sb.append("DATI UTENTE (usa solo questi):\n")

    val s = state.settings
    sb.append("- Nome: ${s.userName.ifBlank { "non indicato" }}\n")
    val preg = state.pregnancy
    if (state.pregnancyActive && preg != null) {
        val baby = BabyDevelopment.forWeek(preg.weeks)
        sb.append("- Modalità: gravidanza\n")
        sb.append("- Età gestazionale: ${preg.weeks} settimane e ${preg.days} giorni (${preg.trimester.lowercase()})\n")
        sb.append("- Data presunta del parto: ${preg.dueDate.format(itDate)}\n")
        sb.append("- Dimensione del bambino: grande come ${baby.fruit} (circa ${"%.1f".format(baby.lengthCm)} cm")
        if (baby.weightG > 0) sb.append(", ${baby.weightG} g")
        sb.append(")\n")
    } else {
        sb.append("- Modalità: ciclo mestruale\n")
        val p = state.prediction
        if (p != null) {
            sb.append("- Giorno del ciclo: ${p.cycleDay}\n")
            sb.append("- Fase attuale: ${p.phase.lowercase()}\n")
            sb.append("- Durata media del ciclo: ${p.averageCycleLength} giorni\n")
            sb.append("- Durata media della mestruazione: ${p.averagePeriodLength} giorni\n")
            sb.append("- Prossima mestruazione prevista: ${p.nextPeriodStart.format(itDate)}\n")
            sb.append("- Ovulazione ${p.ovulationStatus.lowercase()}: ${p.ovulationDay.format(itDate)}\n")
            sb.append("- Finestra fertile: ${p.fertileStart.format(itDate)} – ${p.fertileEnd.format(itDate)}\n")
        } else {
            sb.append("- Dati del ciclo non ancora sufficienti per una previsione.\n")
        }
        val recentSymptoms = state.logs.takeLast(10).mapNotNull { it.symptoms }.flatMap { it.split(",", ";") }
            .map { it.trim() }.filter { it.isNotBlank() }.distinct().take(6)
        if (recentSymptoms.isNotEmpty()) sb.append("- Sintomi recenti annotati: ${recentSymptoms.joinToString(", ")}\n")
    }
    return sb.toString()
}
