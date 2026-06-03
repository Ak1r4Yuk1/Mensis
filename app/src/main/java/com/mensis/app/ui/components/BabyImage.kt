package com.mensis.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mensis.app.R

/**
 * Illustrazione 2D dello sviluppo del bambino per la settimana corrente.
 *
 * Immagini: ritagli ad alta risoluzione dalla tavola vettoriale "Fetus proposal" (Wikimedia
 * Commons, pubblico dominio). 7 stadi coerenti dallo stesso disegno; la settimana mostra lo
 * stadio più vicino. Nelle primissime settimane (0–5) l'embrione è troppo piccolo: segnaposto.
 */
@Composable
fun BabyImage(week: Int, modifier: Modifier = Modifier) {
    val res = fetusDrawableForWeek(week)
    val shape = RoundedCornerShape(24.dp)
    val border = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

    Box(modifier, contentAlignment = Alignment.Center) {
        if (res == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, border, shape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Egg,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        "Troppo piccolo\nper un'illustrazione",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Crossfade(targetState = res, animationSpec = tween(500), label = "fetus") { r ->
                Image(
                    painter = painterResource(r),
                    contentDescription = "Illustrazione del bambino a $week settimane",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(Color.White)
                        .border(1.dp, border, shape)
                        .padding(6.dp)
                )
            }
        }
    }
}

/** Mappa la settimana sull'illustrazione di stadio più vicina (null nelle primissime settimane). */
private fun fetusDrawableForWeek(week: Int): Int? = when {
    week < 6 -> null
    week < 10 -> R.drawable.fetus_s1   // ~6–9
    week < 13 -> R.drawable.fetus_s2   // ~10–12
    week < 16 -> R.drawable.fetus_s3   // ~13–15
    week < 20 -> R.drawable.fetus_s4   // ~16–19
    week < 26 -> R.drawable.fetus_s5   // ~20–25
    week < 34 -> R.drawable.fetus_s6   // ~26–33
    else -> R.drawable.fetus_s7        // ~34–40
}
