package com.rhounsaweatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rhounsaweatherapp.db.dao.CityDao
import com.rhounsaweatherapp.db.dao.WeatherDao
import com.rhounsaweatherapp.db.tables.*

/**
 * Created by rhounsa on 17/07/2022.
 */



const val DB_NAME = "weatherApp_db"
const val DB_VERSION = 1

@Database(
    entities = [
        DbCurrent::class,
        DbHourly::class,
        DbDaily::class,
        DbAlert::class,
        DbCity::class,
        DbState::class,
        DbCountry::class,
        DbSavedCity::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
abstract class WeatherAppDb: RoomDatabase() {

    abstract val cityDao: CityDao
    abstract val weatherDao: WeatherDao


    companion object {
        @Volatile
        private var INSTANCE: WeatherAppDb? = null

        fun getInstance(context: Context): WeatherAppDb {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherAppDb::class.java,
                        DB_NAME
                    )
                        .createFromAsset("cities.db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}