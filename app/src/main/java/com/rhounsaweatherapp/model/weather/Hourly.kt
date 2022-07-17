package com.rhounsaweatherapp.model.weather

import java.time.ZonedDateTime

/**
 * Created by rhounsa on 17/07/2022.
 */
data class Hourly (
    val time: ZonedDateTime,
    val temp: Int,
    val feelsLike: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDirection: String,
    val pop: Int,
    val rain: Double?,
    val snow: Double?,
    val icon: String,
    val description: String,
    val alpha: Float
)