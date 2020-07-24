package com.example.testproject

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.testproject.data.MyWeather
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage

    private val cityId: MutableLiveData<Int> = MutableLiveData()
    val city: LiveData<Int>
        get() = cityId
    val myCityId: LiveData<Int> = city.distinctUntilChanged()
    private val weatherData: MutableLiveData<MyWeather> = MutableLiveData()
    val weather: LiveData<MyWeather>
        get() = weatherData
    val myWeather: LiveData<MyWeather> = weather.distinctUntilChanged()

    fun getCityId(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // Первый Get запрос для получения cityID по cityName
            val response =
                withContext(Dispatchers.IO) { Network.weatherAPI.getLocationId(text).execute() }
            if (response.isSuccessful) {
                cityId.value = response.body()?.get(0)?.woeid
            } else {
                statusMessage.value = Event("GET CityId is NOT success")
                Log.v("Error", "cityId body ERROR: ${response.code()} ${response.errorBody()} ${response.message()}")
            }
        }
    }

    fun getWeather(cityId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            // Второй Get запрос для получения погоды по cityID
            val response =
                withContext(Dispatchers.IO) { Network.weatherAPI.getWeather(cityId).execute() }
            if (response.isSuccessful) {
                weatherData.value = response.body()
            } else {
                statusMessage.value = Event("GET Weather is NOT success")
                Log.v("Error", "Weather body ERROR: ${response.code()} ${response.errorBody()} ${response.message()}")
            }
        }
    }
}