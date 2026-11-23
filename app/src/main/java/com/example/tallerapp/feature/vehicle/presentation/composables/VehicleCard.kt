package com.example.tallerapp.feature.vehicle.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import java.text.NumberFormat
import java.util.Locale

/** Componente visual reutilizable: recibe datos y emite eventos. */
@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onEdit: () -> Unit,
    onAskDelete: () -> Unit
) {
    val money = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    }
    val numbers = remember {
        NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"))
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(vehicle.plate, style = MaterialTheme.typography.titleMedium)
            Text("${vehicle.brand} | Propietario: ${vehicle.ownerName}")
            Text("Servicio: ${vehicle.service}")
            Text("${money.format(vehicle.costCop)} | ${numbers.format(vehicle.mileage)} km")
            Row {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onAskDelete) { Text("Eliminar") }
            }
        }
    }
}
