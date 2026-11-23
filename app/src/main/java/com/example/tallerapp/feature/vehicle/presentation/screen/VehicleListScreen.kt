package com.example.tallerapp.feature.vehicle.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tallerapp.feature.vehicle.presentation.composables.VehicleDeleteDialog
import com.example.tallerapp.feature.vehicle.presentation.section.VehicleListSection
import com.example.tallerapp.feature.vehicle.presentation.viewmodel.VehicleListViewModel

/** Pantalla contenedora: conecta ViewModel, secciones y navegacion. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    vm: VehicleListViewModel,
    onCreate: () -> Unit,
    onEdit: (String) -> Unit
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taller - Vehículos") },
                actions = { TextButton(onClick = { vm.sync() }) { Text("Sincronizar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) { Text("+") }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (ui.isSyncing) LinearProgressIndicator(Modifier.fillMaxWidth())
            ui.message?.let { Text(it, Modifier.padding(12.dp)) }
            VehicleListSection(
                vehicles = ui.vehicles,
                onEdit = onEdit,
                onAskDelete = { pendingDeleteId = it }
            )
        }
    }
    if (pendingDeleteId != null) VehicleDeleteDialog(
        onConfirm = {
            pendingDeleteId?.let(vm::delete)
            pendingDeleteId = null
        },
        onDismiss = { pendingDeleteId = null }
    )
}
