package com.mensis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val itLocale = Locale.forLanguageTag("it-IT")

@Composable
fun MonthCalendar(
    month: YearMonth,
    today: LocalDate,
    selected: LocalDate?,
    phaseOf: (LocalDate) -> String,
    hasLog: (LocalDate) -> Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrev) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mese precedente")
            }
            Text(
                month.month.getDisplayName(TextStyle.FULL, itLocale).replaceFirstChar { it.uppercase() } + " " + month.year,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mese successivo")
            }
        }

        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom").forEach {
                Text(
                    it,
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        val firstDay = month.atDay(1)
        val leadingBlanks = (firstDay.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
        val daysInMonth = month.lengthOfMonth()
        val cells = buildList<LocalDate?> {
            repeat(leadingBlanks) { add(null) }
            for (d in 1..daysInMonth) add(month.atDay(d))
        }
        cells.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp), contentAlignment = Alignment.Center) {
                        if (date != null) {
                            DayCell(
                                date = date,
                                isToday = date == today,
                                isSelected = date == selected,
                                phase = phaseOf(date),
                                hasLog = hasLog(date),
                                onClick = { onSelect(date) }
                            )
                        }
                    }
                }
                if (week.size < 7) repeat(7 - week.size) { Box(Modifier.weight(1f)) {} }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    phase: String,
    hasLog: Boolean,
    onClick: () -> Unit
) {
    val fill = phaseColor(phase)
    val border = if (isSelected) MaterialTheme.colorScheme.primary
    else if (isToday) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(fill)
            .border(if (isSelected || isToday) 2.dp else 0.dp, border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (hasLog) {
                Box(
                    Modifier.padding(top = 1.dp).size(4.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        // Marcatore dell'ovulazione
        if (phase == "Ovulatoria") {
            Icon(
                Icons.Filled.Adjust,
                contentDescription = "Ovulazione",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.TopEnd).padding(2.dp).size(10.dp)
            )
        }
    }
}
