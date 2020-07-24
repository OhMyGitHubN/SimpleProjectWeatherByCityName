package com.example.testproject

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.observe
import com.example.testproject.data.MyWeather
import com.example.testproject.databinding.ActivityMainBinding
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
            btnClicked()
        }

        myViewModel.message.observe(this) {
            it.getContentIfNotHandled()?.let { showToast(it) }
        }
    }

    private fun btnClicked() {
        if(myText.text.isNotEmpty()) {
            try {
                checkViewsVisibility(true)
                myViewModel.getCityId("${myText.text}")
                myViewModel.myCityId.observe(this) {
                    myViewModel.getWeather(it)
                    myViewModel.myCityId.removeObservers(this)
                }
                myViewModel.myWeather.observe(this) {
                    showAlertDialog(it)
                    myViewModel.myWeather.removeObservers(this)
                }
            } catch (e: Exception) {
                when(e) {
                    is UnknownHostException -> Log.d("Exception", "Server is unreachable")
                    is SocketTimeoutException -> Log.d("Exception", "No internet connection")
                    is IOException -> Log.d("Exception", "IOException")
                    is RuntimeException -> Log.d("Exception", "RuntimeException")
                }
                showToast( "An error! Please try again later")
                e.printStackTrace()
            } finally {
                checkViewsVisibility(false)
            }
        }
        else showToast( "Please enter the city name")
    }

    override fun onDestroy() {
        super.onDestroy()

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
