package com.example.project

/**
 * This data class is for formatting the weather info that comes back from the OpenWeatherMap API
 */
data class JsonWeather(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val timeZone: Int,
    val id: Int,
    val name: String,
    val cod: Int
){
    data class Coord(val lat: Double, val lon: Double)
    data class Weather(val id: Int, val main: String, val description: String, val icon: String)
    data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double,
                    val pressure: Int, val humidity: Int, val sea_level: Int, val grd_level: Int)
    data class Wind(val speed: Double, val deg: Int, val gust: Double)
    data class Rain(val h: Double)
    data class Clouds(val all: Int)
    data class Sys(val type: Int, val id: Int, val country: String, val sunrise: Int, val sunset: Int)
}
