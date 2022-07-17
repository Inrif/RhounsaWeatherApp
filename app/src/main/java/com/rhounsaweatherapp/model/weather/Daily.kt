package com.rhounsaweatherapp.model.weather

import java.time.ZonedDateTime

/**
 * Created by rhounsa on 17/07/2022.
 */
data class Daily (
    val date: ZonedDateTime,
    val tempMin: Int,
    val tempMax: Int,
    val tempMorn: Int,
    val tempDay: Int,
    val tempEve: Int,
    val tempNight: Int,
    val feelsLikeMorn: Int?,
    val feelsLikeDay: Int?,
    val feelsLikeEve: Int?,
    val feelsLikeNight: Int?,
    val humidity: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDirection: String,
    val pop: Int,
    val rain: Double?,
    val snow: Double?,
    val icon: String,
    val description: String
)