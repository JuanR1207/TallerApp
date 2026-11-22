package com.example.tallerapp.feature.vehicle.data.mapper

import com.example.tallerapp.feature.vehicle.data.local.VehicleEntity
import com.example.tallerapp.feature.vehicle.domain.model.Vehicle

// Los modelos del dominio nunca exponen detalles de Room.
fun VehicleEntity.toDomain() = Vehicle(
    id, plate, brand, ownerName, service, costCop, mileage, updatedAt
)

fun Vehicle.toEntity() = VehicleEntity(
    id = id, plate = plate, brand = brand, ownerName = ownerName,
    service = service, costCop = costCop, mileage = mileage,
    updatedAt = updatedAt, pending = true, isDeleted = false
)
