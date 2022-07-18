package com.rhounsaweatherapp.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rhounsaweatherapp.api.Api
import com.rhounsaweatherapp.api.LocationManager
import com.rhounsaweatherapp.db.WeatherAppDb
import com.rhounsaweatherapp.db.dao.CityDao
import com.rhounsaweatherapp.db.dao.WeatherDao
import com.rhounsaweatherapp.repository.Repository
import com.rhounsaweatherapp.ui.views.dataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by rhounsa on 17/07/2022.
 */

@Module
@InstallIn(SingletonComponent::class)
class Modules {
    @Provides
    @Singleton
    fun weatherAppDb(app: Application) = WeatherAppDb.getInstance(app)


    @Provides
    @Singleton
    fun cityDao(db: WeatherAppDb) = db.cityDao

    @Provides
    @Singleton
    fun weatherDao(db: WeatherAppDb) = db.weatherDao

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun weatherApi(moshi: Moshi) = Api(moshi)

    @Provides
    @Singleton
    fun prefs(app: Application) = app.applicationContext.dataStore

    @Provides
    @Singleton
    fun weatherRepo(
        weatherDao: WeatherDao,
        cityDao: CityDao,
        api: Api,
        lm: LocationManager,
        prefs: DataStore<Preferences>
    ) =
        Repository(weatherDao, cityDao, api, lm, prefs)

    @Provides
    @Singleton
    fun locationProvider(app: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(app)


    @Provides
    @Singleton
    fun locationManager(
        fusedLocationClient: FusedLocationProviderClient,
    ) = LocationManager(fusedLocationClient)
}
