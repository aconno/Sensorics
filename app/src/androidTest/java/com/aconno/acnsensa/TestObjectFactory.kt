package com.aconno.acnsensa

import com.aconno.acnsensa.model.GooglePublishModel

class ObjectFactory {

    companion object {

        fun makeGooglePublishModel(): GooglePublishModel {

            return GooglePublishModel(
                0L,
                "Test",
                "TestProjectid",
                "Region",
                "DeviceRegistry",
                "Device",
                "PrivateKey",
                false,
                "Secs",
                45000L,
                0L
            )
        }
    }

}