package com.example.tallerapp.feature.vehicle.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    // Room emite nuevos resultados cuando cambia la tabla.
    @Query("SELECT * FROM vehicles WHERE isDeleted = 0 " +
           "ORDER BY updatedAt DESC")
    fun observeVisible(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    suspend fun find(id: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE pending = 1")
    suspend fun getPending(): List<VehicleEntity>

    @Upsert
    suspend fun upsert(vehicle: VehicleEntity)

    // Solo marca sincronizada una version que no haya cambiado.
    @Query("UPDATE vehicles SET pending = 0 " +
           "WHERE id = :id AND updatedAt = :version")
    suspend fun markSynced(id: String, version: Long)
}
