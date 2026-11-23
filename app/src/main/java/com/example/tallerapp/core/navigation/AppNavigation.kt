package com.example.tallerapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.tallerapp.feature.vehicle.presentation.screen.VehicleFormScreen
import com.example.tallerapp.feature.vehicle.presentation.screen.VehicleListScreen
import com.example.tallerapp.feature.vehicle.presentation.viewmodel.VehicleFormViewModel
import com.example.tallerapp.feature.vehicle.presentation.viewmodel.VehicleListViewModel
import kotlinx.serialization.Serializable

// Dos destinos: lista y formulario.
// El formulario sirve para crear (id nulo) y editar (id existente).
@Serializable data object VehicleListRoute : NavKey
@Serializable data class VehicleFormRoute(val id: String? = null) : NavKey

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(VehicleListRoute)
    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<VehicleListRoute> {
                val vm: VehicleListViewModel = hiltViewModel()
                VehicleListScreen(vm,
                    onCreate = { backStack.add(VehicleFormRoute()) },
                    onEdit = { backStack.add(VehicleFormRoute(it)) }
                )
            }
            entry<VehicleFormRoute> { route ->
                val vm: VehicleFormViewModel = hiltViewModel()
                VehicleFormScreen(vm, route.id,
                    onDone = { backStack.removeLastOrNull() })
            }
        }
    )
}
