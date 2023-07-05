package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This data class is for formatting the weather info for our database
 */
@Entity(tableName = "weather_table")
data class DBWeather(
    val id: Int,
    val name: String,
    //Coord
    val lat: Double,
    val lon: Double,
    //Weather
    val weatherMain: String,
    val description: String,
    val icon: String,
    //Main
    val temp: Double,
    //Sys
    val country: String,
){
    @PrimaryKey(autoGenerate = true) var key : Int = 0
}
