package com.aconno.sensorics.data.repository.resources

object AconnoAdvertisementConfigs {
    val ACN_SENSA_VECTOR = """
    {
        "id" : "CF00",
        "name" : "acnSENSA",
        "usecase_screen_path" : "/sensorics/usecase_screens/acnsensa.html",
        "icon_path" : "/sensorics/icons/ic_sensa.png",
        "format_path" : "/sensorics/formats/CF00.json",
        "device_screen_path" : "/sensorics/device_screens/acnsensa.html"
    }
    """.trimIndent()

    val ACN_BEACON = """
    {
        "id" : "0201",
        "name" : "acnBEACON",
        "usecase_screen_path" : "/sensorics/usecase_screens/acnsensa.html",
        "icon_path" : "/sensorics/icons/ic_sensa.png",
        "format_path" : "/sensorics/formats/0201.json",
        "device_screen_path" : "/sensorics/device_screens/empty.html"
    }
    """.trimIndent()
}