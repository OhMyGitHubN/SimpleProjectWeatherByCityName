package com.example.testproject

import com.example.testproject.data.Location
import com.example.testproject.data.MyWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherAPI {
    @GET("/api/location/search/")
    fun getLocationId(@Query("query") title: String): Call<List<Location?>?>

    @GET("/api/location/{id}")
    fun getWeather(@Path("id") id: Int): Call<MyWeather?>
}