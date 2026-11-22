package com.example.tallerapp.core.di

import android.content.Context
import androidx.room.Room
import com.example.tallerapp.core.database.AppDatabase
import com.example.tallerapp.feature.vehicle.data.local.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java,
            "taller.db").build()

    @Provides
    fun vehicleDao(database: AppDatabase): VehicleDao = database.vehicleDao()
}
