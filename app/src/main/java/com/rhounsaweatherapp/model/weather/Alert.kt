package com.rhounsaweatherapp.model.weather

import java.time.ZonedDateTime

/**
 * Created by rhounsa on 17/07/2022.
 */

data class Alert (
    val senderName: String,
    val event: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val description: String
)