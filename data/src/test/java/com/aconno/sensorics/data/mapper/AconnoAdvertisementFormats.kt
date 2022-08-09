package com.aconno.sensorics.data.mapper

object AconnoAdvertisementFormats {

    val ACN_SENSA_VECTOR = """
        {
            "id": "CF00",
            "name": "AcnSensa",
            "icon": "ic_sensa",
            "format": [
                {
                    "name": "Gyroscope X",
                    "start_index_inclusive": 5,
                    "end_index_exclusive": 7,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Gyroscope Y",
                    "start_index_inclusive": 7,
                    "end_index_exclusive": 9,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Gyroscope Z",
                    "start_index_inclusive": 9,
                    "end_index_exclusive": 11,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Accelerometer X",
                    "start_index_inclusive": 11,
                    "end_index_exclusive": 13,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Accelerometer Y",
                    "start_index_inclusive": 13,
                    "end_index_exclusive": 15,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Accelerometer Z",
                    "start_index_inclusive": 15,
                    "end_index_exclusive": 17,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Magnetometer X",
                    "start_index_inclusive": 17,
                    "end_index_exclusive": 19,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Magnetometer Y",
                    "start_index_inclusive": 19,
                    "end_index_exclusive": 21,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Magnetometer Z",
                    "start_index_inclusive": 21,
                    "end_index_exclusive": 23,
                    "reversed": true,
                    "data_type": "SINT16"
                },
                {
                    "name": "Accelerometer Scale Factor",
                    "start_index_inclusive": 23,
                    "end_index_exclusive": 25,
                    "reversed": true,
                    "data_type": "UINT16"
                }
            ],
            "format_required": [
                {
                    "name": "Vendor ID 1",
                    "index": 0,
                    "value": "0x59"
                },
                {
                    "name": "Vendor ID 2",
                    "index": 1,
                    "value": "0x00"
                },
                {
                    "name": "App ID 1",
                    "index": 2,
                    "value": "0x17"
                },
                {
                    "name": "App ID 2",
                    "index": 3,
                    "value": "0xCF"
                },
                {
                    "name": "Advertisement Type",
                    "index": 4,
                    "value": "0x00"
                }
            ]
        }
    """.trimIndent()


    val ACN_SENSA_SCALAR = """
        {
            "id": "CF01",
            "name": "AcnSensa",
            "icon": "ic_sensa",
            "format": [
                {
                    "name": "Temperature",
                    "start_index_inclusive": 5,
                    "end_index_exclusive": 9,
                    "reversed": true,
                    "data_type": "FLOAT"
                },
                {
                    "name": "Humidity",
                    "start_index_inclusive": 9,
                    "end_index_exclusive": 13,
                    "reversed": true,
                    "data_type": "FLOAT"
                },
                {
                    "name": "Pressure",
                    "start_index_inclusive": 13,
                    "end_index_exclusive": 17,
                    "reversed": true,
                    "data_type": "FLOAT"
                },
                {
                    "name": "Light",
                    "start_index_inclusive": 17,
                    "end_index_exclusive": 21,
                    "reversed": true,
                    "data_type": "FLOAT"
                },
                {
                    "name": "Battery Level",
                    "start_index_inclusive": 21,
                    "end_index_exclusive": 22,
                    "reversed": false,
                    "data_type": "BYTE"
                }
            ],
            "format_required": [
                {
                    "name": "Vendor ID 1",
                    "index": 0,
                    "value": "0x59"
                },
                {
                    "name": "Vendor ID 2",
                    "index": 1,
                    "value": "0x00"
                },
                {
                    "name": "App ID 1",
                    "index": 2,
                    "value": "0x17"
                },
                {
                    "name": "App ID 2",
                    "index": 3,
                    "value": "0xCF"
                },
                {
                    "name": "Advertisement Type",
                    "index": 4,
                    "value": "0x01"
                }
            ]
        }
    """.trimIndent()
}