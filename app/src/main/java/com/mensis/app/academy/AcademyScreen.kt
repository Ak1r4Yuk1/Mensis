package com.mensis.app.academy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mensis.app.ui.components.MensisCard

@Composable
fun AcademyScreen(
    mode: AcademyMode,
    currentPhase: String?,
    onOpen: (AcademyContent) -> Unit
) {
    val categories = remember(mode) { listOf("Tutti") + AcademyCatalog.categories(mode) }
    var category by remember(mode) { mutableStateOf("Tutti") }
    val featured = remember(mode, currentPhase) { AcademyCatalog.featured(mode, currentPhase) }
    val list = remember(mode, category) {
        AcademyCatalog.forMode(mode).filter { category == "Tutti" || it.category == category }
    }

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Academy", style = MaterialTheme.typography.headlineSmall)

        if (currentPhase != null && mode == AcademyMode.CYCLE) {
            Text(
                "In evidenza per la fase $currentPhase",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                featured.take(6).forEach { c ->
                    FeaturedCard(c, onClick = { onOpen(c) })
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(selected = cat == category, onClick = { category = cat }, label = { Text(cat) })
            }
        }

        list.forEach { c -> ContentRow(c, onClick = { onOpen(c) }) }
    }
}

@Composable
private fun FeaturedCard(content: AcademyContent, onClick: () -> Unit) {
    Column(
        Modifier.width(220.dp).clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(bottom = 10.dp)
    ) {
        Thumbnail(content, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
        Spacer(Modifier.height(8.dp))
        Text(content.title, Modifier.padding(horizontal = 10.dp), style = MaterialTheme.typography.titleSmall, maxLines = 2)
        Text(
            badge(content), Modifier.padding(horizontal = 10.dp),
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ContentRow(content: AcademyContent, onClick: () -> Unit) {
    MensisCard(Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Thumbnail(content, Modifier.size(110.dp, 64.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(content.category.uppercase() + " · " + badge(content), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(content.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(content.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
        }
    }
}

@Composable
private fun Thumbnail(content: AcademyContent, modifier: Modifier) {
    Box(modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
        val url = content.thumbnailUrl
        if (url != null) {
            AsyncImage(model = url, contentDescription = content.title, modifier = Modifier.fillMaxSize())
            Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
        } else {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun badge(c: AcademyContent): String =
    if (c.type == ContentType.VIDEO) "Video · ${c.durationMinutes} min" else "Articolo · ${c.durationMinutes} min"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(content: AcademyContent, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(content.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (content.type == ContentType.VIDEO && content.videoId != null) {
                YouTubePlayer(content.videoId, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
                Text(content.title, style = MaterialTheme.typography.headlineSmall)
                Text(content.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val ctx = androidx.compose.ui.platform.LocalContext.current
                androidx.compose.material3.TextButton(onClick = {
                    runCatching {
                        ctx.startActivity(
                            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com/watch?v=${content.videoId}"))
                        )
                    }
                }) { Text("Il video non parte? Aprilo su YouTube") }
            } else if (content.bodyMarkdown != null) {
                MarkdownText(
                    markdown = content.bodyMarkdown,
                    textColor = MaterialTheme.colorScheme.onSurface.toArgb(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
