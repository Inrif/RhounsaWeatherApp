package com.rhounsaweatherapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhounsaweatherapp.model.cities.Country
import com.rhounsaweatherapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

/**
 * Created by rhounsa on 18/07/2022.
 */

@ExperimentalCoroutinesApi
@HiltViewModel
class CityViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>>
        get() = _countries

    val countryIndex = repo.countryId.combine(countries) { id, countries ->
        countries.withIndex().firstOrNull { it.value.id == id }?.index ?: 0
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, 0)

    fun selectCountry(index: Int) {
        viewModelScope.launch {
            repo.setCurrentCountry(countries.value[index].id)
        }
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _countries.value = repo.getAllCountries()
            }
        }
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query

    fun setQuery(name: String) {
        viewModelScope.launch {
            _query.emit(name)
        }
    }

    private val cities = repo.citiesForCountry

    val filteredCities = cities.combine(query) { cities, query ->
        val q = query.lowercase(Locale.ROOT).trim()
        cities.filter { city ->
            city.name.lowercase(Locale.ROOT).contains(q)
        }
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, emptyList())

    fun addCity(id: Long) = repo.addCity(id)

    fun removeCity(id: Long) = repo.removeCity(id)
}