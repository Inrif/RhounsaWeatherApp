package com.rhounsaweatherapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhounsaweatherapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by rhounsa on 18/07/2022.
 */

@ExperimentalCoroutinesApi
@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {
    fun refresh() {
        viewModelScope.launch {
            repo.refreshSelectedCity()
        }
    }

    fun setCurrentCity(cityId: Long) {
        viewModelScope.launch {
            repo.setCurrentCity(cityId)
        }
    }

    fun onPermissionGranted() {
        repo.onPermissionGranted()
    }

    fun removeCurrentCity() {
        repo.removeCurrentCity()
    }

    val currentCityName = repo.currentCity.map { it?.name ?: "" }

    val cities = repo.savedCities
    val state = repo.refreshState

    val currentForecast = repo.currentForecast
    val hourlyForecast = repo.hourlyForecast
    val dailyForecast = repo.dailyForecast
    val alerts = repo.alerts

}
