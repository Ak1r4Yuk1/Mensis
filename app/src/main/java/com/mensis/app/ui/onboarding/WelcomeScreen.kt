package com.mensis.app.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mensis.app.ai.ModelDownloader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Slide(val icon: ImageVector, val title: String, val body: String)

@Composable
fun WelcomeScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val slides = remember {
        listOf(
            Slide(
                Icons.Filled.Lock,
                "Benvenuta in Mensis",
                "Il tuo diario del ciclo, della fertilità e della gravidanza. Tutto resta in locale sul " +
                    "dispositivo: nessun cloud, nessun account, massima privacy."
            ),
            Slide(
                Icons.Filled.Favorite,
                "Previsioni e Academy",
                "Calendario con le fasi del ciclo, previsioni di mestruazione e finestra fertile, e una " +
                    "raccolta di articoli e video su ciclo e gravidanza, sempre con te."
            ),
            Slide(
                Icons.Filled.AutoAwesome,
                "Assistente AI e promemoria",
                "Un assistente AI che funziona tutto sul telefono (in download ora). Attiva le notifiche per " +
                    "ricevere promemoria sui prossimi eventi: mestruazione, finestra fertile, ovulazione."
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Avvia automaticamente il download del modello AI alla prima apertura.
    LaunchedEffect(Unit) { runCatching { ModelDownloader.ensureDownloading(context) } }

    // Stato del download, aggiornato in tempo reale per la terza slide.
    var dl by remember { mutableStateOf(ModelDownloader.status(context)) }
    LaunchedEffect(Unit) {
        while (dl.state != ModelDownloader.State.DONE) {
            dl = ModelDownloader.status(context)
            delay(1000)
        }
    }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* esito ignorato: si prosegue comunque */ }

    fun requestNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val isLast = pagerState.currentPage == slides.lastIndex

    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = onDone) { Text("Salta") }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            val s = slides[page]
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier.size(120.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(s.icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
                }
                Spacer(Modifier.height(28.dp))
                Text(
                    s.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    s.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (page == slides.lastIndex) {
                    Spacer(Modifier.height(20.dp))
                    DownloadStatus(dl)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { requestNotifications() }) {
                        Icon(Icons.Outlined.Notifications, null)
                        Spacer(Modifier.size(8.dp))
                        Text("Attiva le notifiche")
                    }
                }
            }
        }

        // Indicatore a pallini
        Row(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(slides.size) { i ->
                val selected = i == pagerState.currentPage
                Box(
                    Modifier.padding(horizontal = 4.dp).size(if (selected) 10.dp else 8.dp).clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        Button(
            onClick = {
                if (isLast) {
                    requestNotifications()
                    onDone()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLast) "Inizia con Mensis" else "Avanti")
            if (!isLast) {
                Spacer(Modifier.size(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DownloadStatus(status: ModelDownloader.Status) {
    val mb = 1024.0 * 1024.0
    when (status.state) {
        ModelDownloader.State.DONE ->
            Text(
                "Assistente AI pronto ✓",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        ModelDownloader.State.FAILED ->
            Text(
                "Download dell'assistente non riuscito: riprova più tardi dalla chat.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        else -> {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (status.totalBytes > 0 && status.downloadedBytes > 0) {
                    LinearProgressIndicator(
                        progress = { status.fraction },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Scaricamento assistente AI… ${"%.0f".format(status.downloadedBytes / mb)} / " +
                            "${"%.0f".format(status.totalBytes / mb)} MB",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Avvio del download dell'assistente AI…",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
