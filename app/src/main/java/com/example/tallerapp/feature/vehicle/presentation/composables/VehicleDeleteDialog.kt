package com.example.tallerapp.feature.vehicle.presentation.composables

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

/** Modal solo visual. La operacion real se decide en la Screen. */
@Composable
fun VehicleDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar vehículo") },
        text = { Text("Se ocultará localmente y se eliminará al sincronizar.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
