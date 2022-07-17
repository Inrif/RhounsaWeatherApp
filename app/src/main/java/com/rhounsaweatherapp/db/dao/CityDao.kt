package com.rhounsaweatherapp.db.dao

import androidx.room.*
import com.rhounsaweatherapp.db.tables.Coord
import com.rhounsaweatherapp.db.tables.DbCityName
import com.rhounsaweatherapp.db.tables.DbSavedCity
import com.rhounsaweatherapp.db.tables.DbSavedCityName
import com.rhounsaweatherapp.model.cities.City
import com.rhounsaweatherapp.model.cities.Country
import kotlinx.coroutines.flow.Flow

/**
 * Created by rhounsa on 17/07/2022.
 */
@Dao
interface CityDao {@Query(
    """
        SELECT cities.id, cities.name AS city, states.name AS state
        FROM cities
        LEFT JOIN states ON cities.stateId == states.id
        WHERE cities.countryId = :countryId
        ORDER BY cities.name COLLATE NOCASE
    """
)
suspend fun getCitiesForCountry(countryId: Long): List<DbCityName>

    @Query(
        """
        SELECT * FROM countries
        ORDER BY name COLLATE NOCASE
    """
    )
    suspend fun getAllCountries(): List<Country>

    @Query(
        """
        SELECT saved_cities.id, cities.name AS city, states.name AS state, countries.name AS country, selected
        FROM saved_cities
        LEFT JOIN cities ON saved_cities.id == cities.id
        LEFT JOIN states ON cities.stateId == states.id
        LEFT JOIN countries ON cities.countryId == countries.id
        WHERE saved_cities.id != 0
        ORDER BY cities.name COLLATE NOCASE
    """
    )
    fun getSavedCities(): Flow<List<DbSavedCityName>>

    @Query("SELECT id FROM saved_cities ORDER BY id ASC")
    fun getSavedCityIdsFlow(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(city: DbSavedCity)

    @Query("SELECT * FROM saved_cities WHERE selected")
    suspend fun getSelectedCity(): DbSavedCity

    @Query(
        """
        SELECT saved_cities.id, name 
        FROM saved_cities 
        JOIN cities ON saved_cities.id == cities.id
        WHERE selected"""
    )
    fun getSelectedCityFlow(): Flow<City?>

    @Transaction
    suspend fun selectCity(city: DbSavedCity) {
        insert(getSelectedCity().copy(selected = false))
        insert(city.copy(selected = true))
    }

    @Query("SELECT lat, lon from cities where id = :cityId")
    suspend fun getCoordinates(cityId: Long): Coord

    @Query("SELECT * FROM saved_cities WHERE id == :id")
    suspend fun getSavedCity(id: Long): DbSavedCity

    @Query("SELECT * FROM saved_cities WHERE id = :id")
    suspend fun isCitySaved(id: Long): DbSavedCity?

    @Delete
    suspend fun delete(savedCity: DbSavedCity)

    @Query("SELECT * FROM saved_cities LIMIT 1")
    suspend fun getAnySavedCity(): DbSavedCity

    @Transaction
    suspend fun removeCity(id: Long) {
        isCitySaved(id)?.let { oldCity ->
            if (oldCity.selected) {
                selectCity(getAnySavedCity())
            }
            delete(oldCity.copy(selected = false))
        }
    }

}