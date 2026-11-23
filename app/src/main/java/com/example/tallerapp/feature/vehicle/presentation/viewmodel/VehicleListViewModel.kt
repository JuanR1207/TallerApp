package com.example.tallerapp.feature.vehicle.presentation.viewmodel

import com.example.tallerapp.feature.vehicle.presentation.state.VehicleListUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerapp.feature.vehicle.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(VehicleListUiState())
    val ui: StateFlow<VehicleListUiState> = _ui

    init {
        viewModelScope.launch {
            repository.observeVehicles()
                .catch { _ui.value = _ui.value.copy(message = it.message) }
                .collect { vehicles ->
                    _ui.value = _ui.value.copy(vehicles = vehicles)
                }
        }
        sync()
    }

    fun sync() = viewModelScope.launch {
        _ui.value = _ui.value.copy(isSyncing = true, message = null)
        try {
            val report = repository.syncNow()
            _ui.value = _ui.value.copy(
                message = "Sincronizado: ${report.uploaded} enviados"
            )
        } catch (_: Exception) {
            _ui.value = _ui.value.copy(
                message = "Sin conexión: datos locales disponibles"
            )
        } finally {
            _ui.value = _ui.value.copy(isSyncing = false)
        }
    }

    fun delete(id: String) = viewModelScope.launch {
        repository.delete(id)
        sync()
    }
}
