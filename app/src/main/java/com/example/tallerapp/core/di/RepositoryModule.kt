package com.example.tallerapp.core.di

import com.example.tallerapp.feature.vehicle.data.repository.VehicleRepositoryImpl
import com.example.tallerapp.feature.vehicle.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindVehicles(impl: VehicleRepositoryImpl): VehicleRepository
}
