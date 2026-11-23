package com.example.tallerapp.feature.vehicle.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/** Unica clase que conoce las rutas de la coleccion de Firestore. */
class FirestoreVehicleDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private fun vehicles(uid: String) = db.collection("users")
        .document(uid).collection("vehicles")

    suspend fun push(uid: String, dto: VehicleDto) {
        // El ID del documento es el ID estable del vehiculo local.
        val data = mapOf(
            "id" to dto.id,
            "plate" to dto.plate,
            "brand" to dto.brand,
            "ownerName" to dto.ownerName,
            "service" to dto.service,
            "costCop" to dto.costCop,
            "mileage" to dto.mileage,
            "updatedAt" to dto.updatedAt,
            "isDeleted" to dto.isDeleted
        )
        vehicles(uid).document(dto.id).set(data).await()
    }

    suspend fun fetchFromServer(uid: String): List<VehicleDto> =
        vehicles(uid).get(Source.SERVER).await().documents.mapNotNull { doc ->
            val id = doc.getString("id") ?: return@mapNotNull null
            VehicleDto(
                id = id,
                plate = doc.getString("plate").orEmpty(),
                brand = doc.getString("brand").orEmpty(),
                ownerName = doc.getString("ownerName").orEmpty(),
                service = doc.getString("service").orEmpty(),
                costCop = doc.getLong("costCop") ?: 0L,
                mileage = (doc.getLong("mileage") ?: 0).toInt(),
                updatedAt = doc.getLong("updatedAt") ?: 0L,
                isDeleted = doc.getBoolean("isDeleted") ?: false
            )
        }
}
