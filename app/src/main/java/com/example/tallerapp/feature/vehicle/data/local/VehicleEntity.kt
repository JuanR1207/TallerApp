package com.example.tallerapp.feature.vehicle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// La marca pending evita perder cambios hechos sin conexion.
// isDeleted funciona como tombstone (borrado logico).
@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val plate: String,
    val brand: String,
    val ownerName: String,
    val service: String,
    val costCop: Long,
    val mileage: Int,
    val updatedAt: Long,
    val pending: Boolean = true,
    val isDeleted: Boolean = false
)
