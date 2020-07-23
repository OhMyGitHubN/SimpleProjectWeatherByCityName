package com.example.testproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testproject.data.MyWeather

class MyViewModel(application: Application) : AndroidViewModel(application) {


    val myText: MutableLiveData<String> = MutableLiveData("")
    val myCityId: MutableLiveData<Int> = MutableLiveData()
    val myWeather: LiveData<MyWeather> = MutableLiveData()

}