package com.mensis.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mensis.app.ui.components.DateField
import com.mensis.app.ui.components.MensisCard
import java.time.LocalDate

@Composable
fun OnboardingScreen(
    onComplete: (name: String, cycleLength: Int, periodLength: Int, lastPeriod: LocalDate) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cycle by remember { mutableStateOf("28") }
    var period by remember { mutableStateOf("5") }
    var lastPeriod by remember { mutableStateOf(LocalDate.now()) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Mensis", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            "Un diario elegante e privato per ciclo, fertilità e gestazione.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MensisCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(0.dp))
                Text(
                    "  Tutto resta in locale sul dispositivo. Nessun cloud, nessun account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        MensisCard {
            Text("Avviso importante", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                "Mensis usa un algoritmo statistico-matematico basato sui tuoi dati. Le previsioni sono stime e possono variare: l'app non è un dispositivo medico né un metodo contraccettivo. Per decisioni di salute o contraccezione consulta sempre un professionista.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        MensisCard {
            Text("Configura il tuo profilo", style = MaterialTheme.typography.titleLarge)
            Text(
                "Potrai cambiare tutto in qualsiasi momento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome o soprannome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = cycle,
                onValueChange = { cycle = it.filter(Char::isDigit).take(2) },
                label = { Text("Durata media ciclo (giorni)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = period,
                onValueChange = { period = it.filter(Char::isDigit).take(2) },
                label = { Text("Durata media mestruazione (giorni)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            DateField(
                label = "Ultima mestruazione",
                value = lastPeriod,
                onChange = { lastPeriod = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    onComplete(
                        name.trim(),
                        cycle.toIntOrNull()?.coerceIn(21, 45) ?: 28,
                        period.toIntOrNull()?.coerceIn(3, 10) ?: 5,
                        lastPeriod
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Inizia il tracking") }
        }
    }
}
