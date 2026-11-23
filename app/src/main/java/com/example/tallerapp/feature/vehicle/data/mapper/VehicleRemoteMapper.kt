package com.example.tallerapp.feature.vehicle.data.mapper

import com.example.tallerapp.feature.vehicle.data.local.VehicleEntity
import com.example.tallerapp.feature.vehicle.data.remote.VehicleDto

// Transformamos los registros entre la BD local y la representacion remota.
// pending NO viaja a Firestore; isDeleted SI.
fun VehicleEntity.toDto() = VehicleDto(
    id, plate, brand, ownerName, service, costCop, mileage, updatedAt, isDeleted
)

fun VehicleDto.toEntity() = VehicleEntity(
    id, plate, brand, ownerName, service, costCop, mileage, updatedAt,
    pending = false, isDeleted = isDeleted
)
