package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aconno.sensorics.SingleLiveEvent
import com.aconno.sensorics.domain.ResourcesInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val resourcesInitializer: ResourcesInitializer
) : ViewModel() {

    val initializationLiveEvent = SingleLiveEvent<Boolean>()

    fun initApp() {
        viewModelScope.launch(Dispatchers.IO) {
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
            initializationLiveEvent.postValue(true)
        }
    }
}
