package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    StudentEntity::class,
    TripEventEntity::class,
    OfflineScanQueueEntity::class,
    TicketEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class SafeRideDatabase : RoomDatabase() {

  abstract fun studentDao(): StudentDao
  abstract fun tripEventDao(): TripEventDao
  abstract fun offlineScanDao(): OfflineScanDao
  abstract fun ticketDao(): TicketDao

  companion object {
    @Volatile
    private var INSTANCE: SafeRideDatabase? = null

    fun getInstance(context: Context): SafeRideDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SafeRideDatabase::class.java,
          "saferide_school_tracker.db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    fun createInMemory(context: Context): SafeRideDatabase {
      return Room.inMemoryDatabaseBuilder(
        context.applicationContext,
        SafeRideDatabase::class.java
      )
      .allowMainThreadQueries()
      .build()
    }
  }
}
