package com.rhounsaweatherapp.api

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rhounsa on 17/07/2022.
 */


interface ApiService {
    @GET("onecall")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appId: String = "f218129264ba2288384cd29b3839d9f3"
    ): Result.Success
}

class Api(moshi: Moshi) {
    private val retrofit:ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }

    suspend fun getForecast(lat: Double, lon: Double): Result {
        return try {
            retrofit.getForecast(lat, lon)
        } catch (e: Exception) {
            Result.Failure(e.toString())
        }
    }
}
