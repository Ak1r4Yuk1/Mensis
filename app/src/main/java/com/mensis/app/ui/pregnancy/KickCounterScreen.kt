package com.mensis.app.ui.pregnancy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mensis.app.data.KickSession
import com.mensis.app.ui.MainViewModel
import com.mensis.app.ui.components.MensisCard
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

private val itDateTime = DateTimeFormatter.ofPattern("dd/MM 'alle' HH:mm")

@Composable
fun KickCounterScreen(vm: MainViewModel) {
    val sessions by vm.kickSessions.collectAsStateWithLifecycle()

    var count by remember { mutableIntStateOf(0) }
    var startMs by remember { mutableStateOf<Long?>(null) }
    var elapsed by remember { mutableLongStateOf(0L) }

    LaunchedEffectTimer(startMs) { elapsed = it }

    fun reset() {
        count = 0; startMs = null; elapsed = 0L
    }

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Conta i movimenti", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Tocca il cerchio a ogni movimento del bambino. Un metodo comune è contare fino a 10 " +
                "movimenti e annotare quanto tempo serve. In caso di riduzione netta dei movimenti, " +
                "contatta chi ti segue.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        if (startMs == null) startMs = System.currentTimeMillis()
                        count++
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$count",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (startMs == null) "Tocca per iniziare" else formatDuration(elapsed),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { reset() }, modifier = Modifier.weight(1f)) { Text("Azzera") }
            Button(
                onClick = { vm.saveKick(count, elapsed.toInt()); reset() },
                enabled = count > 0,
                modifier = Modifier.weight(1f)
            ) { Text("Salva sessione") }
        }

        if (sessions.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("Sessioni precedenti", style = MaterialTheme.typography.titleMedium)
            sessions.take(20).forEach { s -> SessionRow(s) }
        }
    }
}

@Composable
private fun SessionRow(s: KickSession) {
    MensisCard {
        Text(
            "${s.count} movimenti in ${formatDuration(s.durationSeconds.toLong())}",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            s.start.format(itDateTime),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LaunchedEffectTimer(startMs: Long?, onTick: (Long) -> Unit) {
    androidx.compose.runtime.LaunchedEffect(startMs) {
        if (startMs != null) {
            while (true) {
                onTick((System.currentTimeMillis() - startMs) / 1000)
                delay(1000)
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
