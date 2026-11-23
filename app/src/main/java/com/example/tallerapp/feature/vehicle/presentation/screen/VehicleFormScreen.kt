package com.example.tallerapp.feature.vehicle.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tallerapp.feature.vehicle.presentation.section.VehicleFormSection
import com.example.tallerapp.feature.vehicle.presentation.viewmodel.VehicleFormViewModel

/** La Screen orquesta estado, estructura Scaffold y salida del formulario. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleFormScreen(
    vm: VehicleFormViewModel,
    vehicleId: String?,
    onDone: () -> Unit
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    LaunchedEffect(vehicleId) { vm.load(vehicleId) }
    LaunchedEffect(ui.saved) { if (ui.saved) onDone() }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(if (vehicleId == null) "Nuevo ingreso" else "Editar vehículo")
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            VehicleFormSection(
                ui = ui,
                onPlate = vm::updatePlate,
                onBrand = vm::updateBrand,
                onOwnerName = vm::updateOwnerName,
                onService = vm::updateService,
                onCost = vm::updateCost,
                onMileage = vm::updateMileage,
                onSave = vm::save
            )
        }
    }
}
