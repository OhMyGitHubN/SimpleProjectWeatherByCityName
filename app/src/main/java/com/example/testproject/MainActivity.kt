package com.example.testproject

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil.setContentView
import com.example.testproject.data.MyWeather
import com.example.testproject.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class MainActivity : AppCompatActivity() {

    private val myViewModel by viewModels<MyViewModel>()

    private lateinit var text: TextView
    private lateinit var progress: ProgressBar
    private lateinit var myText: EditText
    private lateinit var btnGet: Button
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = myViewModel

        text = findViewById(R.id.text)
        progress = findViewById(R.id.progress)
        myText = findViewById(R.id.cityName)
        btnGet = findViewById(R.id.btnGet)
        checkViewsVisibility(false)

        btnGet.setOnClickListener {
            if(myText.text.isNotEmpty()) getRequest("${myText.text}")
            else Toast.makeText(applicationContext, "Please enter the city name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRequest(text: String) = CoroutineScope(Dispatchers.Main).launch {
        try {
            checkViewsVisibility(true)
            val cityId = withContext(Dispatchers.IO) { Network.weatherAPI.getLocationId(text).execute() }
            if (cityId.isSuccessful) {
                val weather =  withContext(Dispatchers.IO) { cityId.body()?.get(0)?.woeid?.let { Network.weatherAPI.getWeather(it).execute() } }
                if(weather != null && weather.isSuccessful) {
                    weather.body()?.let { showAlertDialog(it) }
                } else {
                    Toast.makeText(application, "Something wrong! Please try again later.", Toast.LENGTH_SHORT).show()
                    Log.v("Error", "Weather body ERROR: ${weather?.code()} ${weather?.errorBody()} ${weather?.message()}")
                }
            } else {
                Toast.makeText(application, "Something wrong! Please try again later", Toast.LENGTH_SHORT).show()
                Log.v("Error", "cityId body ERROR: ${cityId.code()} ${cityId.errorBody()} ${cityId.message()}")
            }
            checkViewsVisibility(false)
        } catch (e: Exception) {
            when(e) {
                is UnknownHostException -> Log.d("Exception", "Server is unreachable")
                is SocketTimeoutException -> Log.d("Exception", "No internet connection")
                is IOException -> Log.d("Exception", "IOException")
                is RuntimeException -> Log.d("Exception", "RuntimeException")
            }
            checkViewsVisibility(false)
            Toast.makeText(application, "An error! Please try again later", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun showAlertDialog(body: MyWeather): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setIcon(getWeatherIcon(body.consolidated_weather[0].weather_state_abbr))
            .setTitle("It's ${body.consolidated_weather[0].weather_state_name} in ${body.title.capitalize()}.")
            .setMessage("""
            Min temperature is: ${body.consolidated_weather[0].min_temp}
            The temperature is: ${body.consolidated_weather[0].the_temp}
            Max temperature is: ${body.consolidated_weather[0].max_temp}
            Wind direction is: ${body.consolidated_weather[0].wind_direction_compass}
            Wind speed is: ${body.consolidated_weather[0].wind_speed}
            Air pressure is: ${body.consolidated_weather[0].air_pressure}
            Humidity is: ${body.consolidated_weather[0].humidity}
            Visibility is: ${body.consolidated_weather[0].visibility}""".trimIndent())
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ -> }
        return builder.show()
    }

    private fun checkViewsVisibility(boolean: Boolean) {
        myText.isEnabled = !boolean
        btnGet.isEnabled = !boolean
        text.isVisible = boolean
        progress.isVisible = boolean
    }

    private fun getWeatherIcon(str: String) = when(str) {
        "c" -> R.drawable.clear
        "h" -> R.drawable.hail
        "hc" -> R.drawable.heavy_cloud
        "hr" -> R.drawable.heavy_rain
        "lc" -> R.drawable.light_cloud
        "lr" -> R.drawable.light_rain
        "s" -> R.drawable.showers
        "sl" -> R.drawable.sleet
        "sn" -> R.drawable.snow
        else -> R.drawable.thunderstorm
    }
}
