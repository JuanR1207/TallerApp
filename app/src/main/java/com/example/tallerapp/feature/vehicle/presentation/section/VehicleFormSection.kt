package com.example.tallerapp.feature.vehicle.presentation.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tallerapp.feature.vehicle.presentation.composables.VehicleFormField
import com.example.tallerapp.feature.vehicle.presentation.state.VehicleFormUiState

/** Seccion de formulario: solo recibe UiState y callbacks. */
@Composable
fun VehicleFormSection(
    ui: VehicleFormUiState,
    onPlate: (String) -> Unit,
    onBrand: (String) -> Unit,
    onOwnerName: (String) -> Unit,
    onService: (String) -> Unit,
    onCost: (String) -> Unit,
    onMileage: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VehicleFormField("Placa (ej: ABC123)", ui.plate, onPlate,
            capitalization = KeyboardCapitalization.Characters)
        VehicleFormField("Marca", ui.brand, onBrand)
        VehicleFormField("Propietario", ui.ownerName, onOwnerName)
        VehicleFormField("Servicio realizado", ui.service, onService)
        VehicleFormField("Costo COP", ui.cost, onCost, KeyboardType.Number)
        VehicleFormField("Kilometraje", ui.mileage, onMileage, KeyboardType.Number)
        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = onSave,
            enabled = !ui.saving,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (ui.saving) "Guardando..." else "Guardar") }
    }
}
