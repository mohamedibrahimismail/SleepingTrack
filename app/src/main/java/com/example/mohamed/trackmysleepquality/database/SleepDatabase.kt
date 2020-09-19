package com.example.mohamed.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {


    abstract val sleepDatabaseDao: SleepDatabaseDao


    companion object {

        @Volatile
        private var INSTANCE: SleepDatabase? = null


        fun getInstance(context: Context): SleepDatabase {

            synchronized(this) {

                var instance = INSTANCE
                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                                                       .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}