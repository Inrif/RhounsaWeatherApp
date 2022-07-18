package com.rhounsaweatherapp.repository

import android.util.Log
import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.rhounsaweatherapp.R
import com.rhounsaweatherapp.api.Api
import com.rhounsaweatherapp.api.LocationManager
import com.rhounsaweatherapp.api.asDatabaseModel
import com.rhounsaweatherapp.db.dao.CityDao
import com.rhounsaweatherapp.db.dao.WeatherDao
import com.rhounsaweatherapp.db.tables.DbSavedCity
import com.rhounsaweatherapp.db.tables.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by rhounsa on 17/07/2022.
 */


sealed class RefreshState {
    object Loading : RefreshState()
    object Loaded : RefreshState()
    object PermissionError : RefreshState()
    data class Error(@StringRes val message: Int) : RefreshState()
}

const val USER_LOCATION_CITY_ID = 0L
const val USA_COUNTRY_CODE = 241L
val SAVED_COUNTRY_ID = longPreferencesKey("saved_country_id")

class Repository(
    private val weatherDb: WeatherDao,
    private val cityDb: CityDao,
    private val api: Api,
    private val lm: LocationManager,
    private val prefs: DataStore<Preferences>
) {

    private fun removeUserLocation() {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDb.clearForecast(USER_LOCATION_CITY_ID)
        }
    }

    fun onPermissionGranted() {
        getWeather(USER_LOCATION_CITY_ID)
    }

    val currentForecast =
        weatherDb.getCurrentForecast().map {
            it?.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, null)
    val hourlyForecast =
        weatherDb.getHourlyForecast().map {
            it.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())
    val dailyForecast =
        weatherDb.getDailyForecast().map {
            it.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())
    val alerts = weatherDb.getAlerts().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())
    val savedCities = cityDb.getSavedCities().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())
    val currentCity =
        cityDb.getSelectedCityFlow().stateIn(
            CoroutineScope(Dispatchers.IO),
            SharingStarted.Eagerly, null)

    init {
        refreshSelectedCity()
    }

    fun refreshSelectedCity() {
        CoroutineScope(Dispatchers.IO).launch {
            getWeather(cityDb.getSelectedCity().id)
        }
    }

    private fun getWeather(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val (lat, lon) = if (cityId == USER_LOCATION_CITY_ID) {
                if (!lm.hasPermission()) {
                    Log.d("getWeather", "Skip refresh $cityId: location permission not granted")
                    _refreshState.emit(RefreshState.PermissionError)
                    removeUserLocation()
                    return@launch
                }
                lm.getLocation() ?: run {
                    Log.d("getWeather", "Skip refresh $cityId: user location unavailable")
                    _refreshState.emit(RefreshState.Error(R.string.location_unavailable_message))
                    return@launch
                }
            } else {
                cityDb.getCoordinates(cityId)
            }
            val lastRefresh = weatherDb.lastUpdated(cityId)
            if (lastRefresh != null && (System.currentTimeMillis() - lastRefresh * 1000 < 600000)) {
                Log.d("getWeather", "Skip refresh $cityId: refreshed < ten minutes ago")
                _refreshState.emit(RefreshState.Loaded)
                return@launch
            }
            Log.d("getWeather", "Refreshing $cityId")
            _refreshState.emit(RefreshState.Loading)
            api.getForecast(lat, lon).run {
                when (this) {
                    is com.rhounsaweatherapp.api.Result.Failure -> {
                        Log.e("getWeather", "Fail $cityId: $error")
                        _refreshState.emit(RefreshState.Error(R.string.network_error_message))
                    }
                    is com.rhounsaweatherapp.api.Result.Success -> {
                        Log.d("getWeather", "Success $cityId")
                        this.run {
                            weatherDb.saveForecast(
                                cityId,
                                current.asDatabaseModel(cityId, timezone),
                                hourly.asDatabaseModel(cityId, timezone),
                                daily.asDatabaseModel(cityId, timezone),
                                alerts.asDatabaseModel(cityId, timezone)
                            )
                            _refreshState.emit(RefreshState.Loaded)
                        }
                    }
                }
            }
        }
    }

    fun setCurrentCity(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(cityDb.getSavedCity(cityId))
            getWeather(cityId)
        }
    }

    //------------------------------------------------------------------------------------------

    fun addCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(DbSavedCity(id))
            getWeather(id)
        }
    }

    fun removeCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.removeCity(id)
            weatherDb.clearForecast(id)
        }
    }

    //------------------------------------------------------------------------------------------

    val countryId = prefs.data.map { it[SAVED_COUNTRY_ID] ?: USA_COUNTRY_CODE }
        .stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, USA_COUNTRY_CODE)

    fun setCurrentCountry(newCountryId: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            prefs.edit {
                it[SAVED_COUNTRY_ID] = newCountryId
            }
        }
    }

    private val savedCityIds =
        cityDb.getSavedCityIdsFlow().stateIn(
            CoroutineScope(Dispatchers.IO),
            SharingStarted.Eagerly, emptyList())

    val citiesForCountry = countryId.combine(savedCityIds) { countryId, savedCitiesIds ->
        cityDb.getCitiesForCountry(countryId).asDomainModel(savedCitiesIds)
    }

    suspend fun getAllCountries() = cityDb.getAllCountries()

    fun removeCurrentCity() {
        CoroutineScope(Dispatchers.IO).launch {
            removeCity(cityDb.getSelectedCity().id)
        }
    }

    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loading)
    val refreshState: StateFlow<RefreshState> get() = _refreshState
}
