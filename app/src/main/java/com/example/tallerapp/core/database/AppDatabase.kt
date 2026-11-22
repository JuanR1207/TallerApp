package com.example.tallerapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tallerapp.feature.vehicle.data.local.VehicleDao
import com.example.tallerapp.feature.vehicle.data.local.VehicleEntity

@Database(entities = [VehicleEntity::class], version = 1,
          exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
}
