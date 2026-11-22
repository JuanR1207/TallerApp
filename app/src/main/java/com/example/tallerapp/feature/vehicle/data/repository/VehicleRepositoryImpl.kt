package com.example.tallerapp.feature.vehicle.data.repository

import com.example.tallerapp.feature.vehicle.data.local.VehicleDao
import com.example.tallerapp.feature.vehicle.data.mapper.toDomain
import com.example.tallerapp.feature.vehicle.data.mapper.toDto
import com.example.tallerapp.feature.vehicle.data.mapper.toEntity
import com.example.tallerapp.feature.vehicle.data.remote.FirestoreVehicleDataSource
import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import com.example.tallerapp.feature.vehicle.domain.repository.SyncReport
import com.example.tallerapp.feature.vehicle.domain.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val dao: VehicleDao,
    private val remote: FirestoreVehicleDataSource,
    private val auth: FirebaseAuth
) : VehicleRepository {
    private val syncMutex = Mutex()

    override fun observeVehicles(): Flow<List<Vehicle>> =
        dao.observeVisible().map { rows -> rows.map { it.toDomain() } }

    override suspend fun findById(id: String): Vehicle? =
        dao.find(id)?.takeUnless { it.isDeleted }?.toDomain()

    override suspend fun save(vehicle: Vehicle) {
        // Guardamos primero en Room: se mantiene el modo offline.
        dao.upsert(vehicle.copy(updatedAt = System.currentTimeMillis())
            .toEntity())
    }

    override suspend fun delete(id: String) {
        val current = dao.find(id) ?: return
        // Tombstone: no borrar fisicamente antes de sincronizar.
        dao.upsert(current.copy(
            isDeleted = true, pending = true,
            updatedAt = System.currentTimeMillis()
        ))
    }

    override suspend fun syncNow(): SyncReport = syncMutex.withLock {
        // Si aun no hay sesion, necesita red la primera vez.
        val uid = auth.currentUser?.uid
            ?: auth.signInAnonymously().await().user?.uid
            ?: error("No fue posible iniciar sesion anonima")

        var uploaded = 0
        for (local in dao.getPending()) {
            remote.push(uid, local.toDto())
            dao.markSynced(local.id, local.updatedAt)
            uploaded++
        }

        val cloudItems = remote.fetchFromServer(uid)
        for (dto in cloudItems) {
            val cloud = dto.toEntity()
            val local = dao.find(cloud.id)
            // Nunca sobrescribir una edicion que sigue pendiente.
            if (local?.pending == true) continue
            if (local == null || cloud.updatedAt > local.updatedAt) {
                dao.upsert(cloud)
            }
        }
        SyncReport(uploaded, cloudItems.size)
    }
}
