package com.example.tallerapp.feature.vehicle.domain.repository

import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

data class SyncReport(val uploaded: Int, val downloaded: Int)

// Contrato: QUE operaciones necesita el taller, no COMO se hacen.
interface VehicleRepository {
    fun observeVehicles(): Flow<List<Vehicle>>
    suspend fun findById(id: String): Vehicle?
    suspend fun save(vehicle: Vehicle)
    suspend fun delete(id: String)
    // Sincronizacion explicitamente solicitada desde la UI.
    suspend fun syncNow(): SyncReport
}
