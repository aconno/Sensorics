package com.aconno.sensorics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ResourcesInitializer
import kotlinx.coroutines.*

class SplashViewModel(
    private val resourcesInitializer: ResourcesInitializer
) : ViewModel() {

    val initializationLiveEvent = MutableLiveData<Boolean>()

    fun initApp() {
        GlobalScope.launch(Dispatchers.Main) {

            GlobalScope.async {

                //This will make sure Splash will stay 2000 ms
                val currMillis = System.currentTimeMillis()

                //do Work
                resourcesInitializer.init()

                //This will make sure Splash will stay 2000 ms
                val diff = System.currentTimeMillis() - currMillis - 2000
                if (diff < 0) {
                    //If initializer work took shorter time than 2000ms
                    //Delay coroutine to fill rest
                    delay(-diff)
                }
            }.await()

            initializationLiveEvent.postValue(true)
        }
    }
}
