package com.example.tallerapp.feature.vehicle.presentation.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import com.example.tallerapp.feature.vehicle.presentation.composables.VehicleCard

/** Bloque funcional de lista: no conoce Room, Firestore ni ViewModel. */
@Composable
fun VehicleListSection(
    vehicles: List<Vehicle>,
    onEdit: (String) -> Unit,
    onAskDelete: (String) -> Unit
) {
    if (vehicles.isEmpty()) {
        Text("No hay vehículos en el taller. Pulsa + para registrar uno.",
            Modifier.padding(24.dp))
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(vehicles, key = { it.id }) { vehicle ->
            VehicleCard(vehicle,
                onEdit = { onEdit(vehicle.id) },
                onAskDelete = { onAskDelete(vehicle.id) }
            )
        }
    }
}
