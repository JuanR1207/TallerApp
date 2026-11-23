package com.example.tallerapp.feature.vehicle.presentation.viewmodel

import com.example.tallerapp.feature.vehicle.presentation.state.VehicleFormUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerapp.feature.vehicle.domain.model.Vehicle
import com.example.tallerapp.feature.vehicle.domain.repository.VehicleRepository
import com.example.tallerapp.feature.vehicle.domain.usecase.SaveVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val repository: VehicleRepository,
    private val saveVehicle: SaveVehicleUseCase
) : ViewModel() {
    private val _ui = MutableStateFlow(VehicleFormUiState())
    val ui: StateFlow<VehicleFormUiState> = _ui
    private var editingId: String? = null
    private var loaded = false

    fun load(id: String?) {
        if (loaded) return
        loaded = true
        editingId = id
        if (id == null) return
        viewModelScope.launch {
            val vehicle = repository.findById(id) ?: return@launch
            _ui.value = VehicleFormUiState(
                plate = vehicle.plate,
                brand = vehicle.brand,
                ownerName = vehicle.ownerName,
                service = vehicle.service,
                cost = vehicle.costCop.toString(),
                mileage = vehicle.mileage.toString()
            )
        }
    }

    fun updatePlate(v: String) { _ui.value = _ui.value.copy(plate = v) }
    fun updateBrand(v: String) { _ui.value = _ui.value.copy(brand = v) }
    fun updateOwnerName(v: String) {
        _ui.value = _ui.value.copy(ownerName = v)
    }
    fun updateService(v: String) { _ui.value = _ui.value.copy(service = v) }
    fun updateCost(v: String) { _ui.value = _ui.value.copy(cost = v) }
    fun updateMileage(v: String) {
        _ui.value = _ui.value.copy(mileage = v)
    }

    fun save() = viewModelScope.launch {
        val current = _ui.value
        val cost = current.cost.toLongOrNull()
        val mileage = current.mileage.toIntOrNull()
        if (cost == null || mileage == null) {
            _ui.value = current.copy(error = "Costo y kilometraje deben ser numéricos")
            return@launch
        }
        _ui.value = current.copy(saving = true, error = null)
        try {
            saveVehicle(Vehicle(
                id = editingId ?: UUID.randomUUID().toString(),
                // La placa se guarda en mayusculas y sin espacios ni guiones.
                plate = current.plate.trim().uppercase()
                    .replace(" ", "").replace("-", ""),
                brand = current.brand.trim(),
                ownerName = current.ownerName.trim(),
                service = current.service.trim(),
                costCop = cost,
                mileage = mileage
            ))
            // Guardado local exitoso incluso si no hay Internet.
            _ui.value = _ui.value.copy(saved = true, saving = false)
            // El usuario usa Sincronizar en la lista cuando necesite subir cambios.
            // Para automatizar reintentos en produccion, usar WorkManager.
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(
                saving = false, error = e.message ?: "Error de guardado"
            )
        }
    }
}
