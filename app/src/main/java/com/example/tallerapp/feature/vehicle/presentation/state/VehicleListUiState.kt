package com.example.tallerapp.feature.vehicle.presentation.state

import com.example.tallerapp.feature.vehicle.domain.model.Vehicle

/** Datos que VehicleListScreen necesita renderizar. */
data class VehicleListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isSyncing: Boolean = false,
    val message: String? = null
)
