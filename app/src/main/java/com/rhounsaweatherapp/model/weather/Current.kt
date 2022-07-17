package com.rhounsaweatherapp.model.weather

import java.time.ZonedDateTime

/**
 * Created by rhounsa on 17/07/2022.
 */
data class Current (
    val lastUpdated: ZonedDateTime,
    val sunrise: ZonedDateTime,
    val sunset: ZonedDateTime,
    val temp: Int,
    val feelsLike: Int,
    val pressure: Double,
    val humidity: Int,
    val dewPoint: Int,
    val clouds: Int,
    val uvi: Int,
    val visibility: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDirection: String,
    val icon: String,
    val description: String
)