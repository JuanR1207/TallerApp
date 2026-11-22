package com.example.tallerapp.feature.vehicle.domain.usecase

import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import com.example.tallerapp.feature.vehicle.domain.repository.VehicleRepository
import javax.inject.Inject

class SaveVehicleUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    // Placa colombiana: 3 letras + 3 numeros (carro) o 3 letras + 2 numeros + 1 letra (moto).
    private val plateRegex = Regex("^[A-Z]{3}[0-9]{2}[0-9A-Z]$")

    suspend operator fun invoke(vehicle: Vehicle) {
        require(plateRegex.matches(vehicle.plate)) { "Placa invalida (ej: ABC123 o ABC12D)" }
        require(vehicle.brand.isNotBlank()) { "Marca obligatoria" }
        require(vehicle.brand.length <= 40) { "Marca: maximo 40 caracteres" }
        require(vehicle.ownerName.isNotBlank()) { "Propietario obligatorio" }
        require(vehicle.ownerName.length <= 80) { "Propietario: maximo 80 caracteres" }
        require(vehicle.service.isNotBlank()) { "Describe el servicio" }
        require(vehicle.service.length <= 500) { "Servicio: maximo 500 caracteres" }
        require(vehicle.costCop > 0) { "El costo debe ser positivo" }
        require(vehicle.mileage >= 0) { "Kilometraje invalido" }
        repository.save(vehicle)
    }
}
