package com.example.tallerapp.feature.vehicle.domain.model

// Modelo del negocio: un vehiculo que entra al taller.
// No importa nada de Room, Firebase ni Android.
data class Vehicle(
    val id: String,
    val plate: String,        // Placa, ej: ABC123 (carro) o ABC12D (moto)
    val brand: String,        // Marca, ej: Chevrolet
    val ownerName: String,    // Nombre del propietario
    val service: String,      // Servicio que se le hace, ej: cambio de aceite
    val costCop: Long,        // Costo del servicio en pesos colombianos enteros
    val mileage: Int,         // Kilometraje al ingresar
    val updatedAt: Long = System.currentTimeMillis()
)
