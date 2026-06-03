package com.mensis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Circular ring that fills to [progress] (0..1). Children are centered inside. */
@Composable
fun PhaseRing(
    progress: Float,
    ringColor: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxWidth().size(200.dp)) {
            val stroke = 22.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        content()
    }
}

/** Simple line chart (e.g. andamento del peso). [points] = label→valore in ordine cronologico. */
@Composable
fun LineChart(
    points: List<Pair<String, Float>>,
    unit: String,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (points.size < 2) {
        Text("Servono almeno due rilevazioni per il grafico.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val values = points.map { it.second }
    val minV = values.min()
    val maxV = values.max()
    val range = (maxV - minV).coerceAtLeast(0.1f)
    val dotColor = lineColor
    Column(modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(150.dp)) {
            val n = points.size
            val stepX = if (n > 1) size.width / (n - 1) else size.width
            val padTop = 14f
            val padBottom = 14f
            val usableH = size.height - padTop - padBottom
            fun yFor(v: Float) = padTop + usableH * (1f - (v - minV) / range)

            val path = Path()
            points.forEachIndexed { i, (_, v) ->
                val x = i * stepX
                val y = yFor(v)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color = lineColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
            points.forEachIndexed { i, (_, v) ->
                drawCircle(dotColor, radius = 5f, center = Offset(i * stepX, yFor(v)))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("min ${"%.1f".format(minV)}$unit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("max ${"%.1f".format(maxV)}$unit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Simple vertical bar chart. */
@Composable
fun BarChart(
    data: List<Pair<String, Float>>,
    unit: String,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        Text("Dati insufficienti", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val maxValue = (data.maxOf { it.second }).coerceAtLeast(1f)
    Row(
        modifier = modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    if (value > 0f) "${value.toInt()}$unit" else "-",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Canvas(
                    Modifier.fillMaxWidth().padding(horizontal = 2.dp).height(110.dp)
                ) {
                    val h = size.height * (value / maxValue).coerceIn(0f, 1f)
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(0f, size.height - h),
                        size = Size(size.width, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
