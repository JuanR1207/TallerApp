package com.example.tallerapp.feature.vehicle.data.remote

/** Representacion del documento Firestore; no contiene anotaciones Room. */
data class VehicleDto(
    val id: String,
    val plate: String,
    val brand: String,
    val ownerName: String,
    val service: String,
    val costCop: Long,
    val mileage: Int,
    val updatedAt: Long,
    val isDeleted: Boolean
)
