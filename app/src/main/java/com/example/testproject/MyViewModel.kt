package com.example.testproject

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.testproject.data.MyWeather
import com.example.testproject.data.Weather
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

//data class Result<out T>(val success: T? = null, val error: Throwable? = null)

class MyViewModel(application: Application) : AndroidViewModel(application) {


    val myText: MutableLiveData<String> = MutableLiveData("")
    val cityId: MutableLiveData<Int> = MutableLiveData()
//    val myCityId: LiveData<Int> = Transformations.distinctUntilChanged(cityId)
    val myCityId: LiveData<Int> = cityId.distinctUntilChanged()
    val weatherData: MutableLiveData<MyWeather> = MutableLiveData()
//    val myWeather: LiveData<MyWeather> = Transformations.distinctUntilChanged(weatherData)
    val myWeather: LiveData<MyWeather> = weatherData.distinctUntilChanged()

    fun getCityId(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // Первый Get запрос для получения cityID по cityName
            val response =
                withContext(Dispatchers.IO) { Network.weatherAPI.getLocationId(text).execute() }
            if (response.isSuccessful) {
                cityId.value = response.body()?.get(0)?.woeid
            } else {
                Toast.makeText(getApplication(), "Something wrong! Please try again later", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(getApplication(), "Something wrong! Please try again later.", Toast.LENGTH_SHORT).show()
                Log.v("Error", "Weather body ERROR: ${response.code()} ${response.errorBody()} ${response.message()}")
            }
        }
    }
}