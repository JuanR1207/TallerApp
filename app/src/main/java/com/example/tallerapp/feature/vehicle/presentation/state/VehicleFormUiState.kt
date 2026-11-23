package com.example.tallerapp.feature.vehicle.presentation.state

/** Un unico estado para que Compose pinte el formulario. */
data class VehicleFormUiState(
    val plate: String = "",
    val brand: String = "",
    val ownerName: String = "",
    val service: String = "",
    val cost: String = "",
    val mileage: String = "0",
    val saving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)
