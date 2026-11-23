package com.example.tallerapp.feature.vehicle.domain.usecase

import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import com.example.tallerapp.feature.vehicle.domain.repository.SyncReport
import com.example.tallerapp.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/** Repositorio falso: solo guarda en una lista para poder revisar. */
private class FakeVehicleRepository : VehicleRepository {
    val saved = mutableListOf<Vehicle>()
    override fun observeVehicles(): Flow<List<Vehicle>> = flowOf(saved)
    override suspend fun findById(id: String) = saved.find { it.id == id }
    override suspend fun save(vehicle: Vehicle) { saved += vehicle }
    override suspend fun delete(id: String) { saved.removeAll { it.id == id } }
    override suspend fun syncNow() = SyncReport(0, 0)
}

class SaveVehicleUseCaseTest {

    private val valid = Vehicle(
        id = "1", plate = "ABC123", brand = "Chevrolet",
        ownerName = "Juan Perez", service = "Cambio de aceite",
        costCop = 180_000, mileage = 45_000
    )

    @Test
    fun guarda_vehiculo_valido() = runBlocking {
        val repo = FakeVehicleRepository()
        SaveVehicleUseCase(repo)(valid)
        assertEquals(1, repo.saved.size)
    }

    @Test
    fun acepta_placa_de_moto() = runBlocking {
        val repo = FakeVehicleRepository()
        SaveVehicleUseCase(repo)(valid.copy(plate = "XYZ45F"))
        assertEquals(1, repo.saved.size)
    }

    @Test
    fun rechaza_placa_invalida() {
        val repo = FakeVehicleRepository()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { SaveVehicleUseCase(repo)(valid.copy(plate = "12ABC")) }
        }
        assertEquals(0, repo.saved.size)
    }

    @Test
    fun rechaza_costo_cero() {
        val repo = FakeVehicleRepository()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { SaveVehicleUseCase(repo)(valid.copy(costCop = 0)) }
        }
    }

    @Test
    fun rechaza_kilometraje_negativo() {
        val repo = FakeVehicleRepository()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { SaveVehicleUseCase(repo)(valid.copy(mileage = -1)) }
        }
    }

    @Test
    fun rechaza_marca_vacia() {
        val repo = FakeVehicleRepository()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { SaveVehicleUseCase(repo)(valid.copy(brand = " ")) }
        }
    }
}
