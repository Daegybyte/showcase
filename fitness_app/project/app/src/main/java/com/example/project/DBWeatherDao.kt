package com.example.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DBWeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbWeather: DBWeather)

    @Query("DELETE FROM weather_table")
    suspend fun deleteAll()

    @Query("SELECT AVG(`temp`) FROM weather_table")
    fun getAveTemp(): Flow<Double>
}