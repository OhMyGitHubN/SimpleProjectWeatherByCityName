package com.example.testproject.data


data class MyWeather(
    val consolidated_weather: List<Weather>,
    val time: String,
    val sun_rise: String,
    val sun_set: String,
    val timezone_name: String,
    val parent: Parent,
    val sources: List<Source>,
    val title: String,
    val location_type: String,
    val woeid: String,
    val latt_long: String,
    val timezone: String
)