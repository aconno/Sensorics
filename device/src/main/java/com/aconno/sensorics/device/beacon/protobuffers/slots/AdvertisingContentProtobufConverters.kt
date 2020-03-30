package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.SlotsProtobufModel
import com.aconno.sensorics.domain.migrate.hexStringToByteArray
import com.aconno.sensorics.domain.migrate.toCompactHex
import com.google.protobuf.ByteString
import java.lang.IllegalStateException
import java.util.*

interface AdvertisingContentProtobufConverter {

    fun convertFromProtobufModel(protobufModel : SlotsProtobufModel.AdvertisingContent) : Map<String,String>

    fun convertToProtobufModel(parametersMap :  Map<String,String>) : SlotsProtobufModel.AdvertisingContent
}

class DefaultContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        return mutableMapOf<String, String>().apply {
            put(Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA, protobufModel.defaultAdvContentTypeParameters.defaultData.toByteArray().toCompactHex())
        }
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        val defaultData = parametersMap[Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA]?.hexStringToByteArray() ?: byteArrayOf()
        return SlotsProtobufModel.AdvertisingContent.newBuilder()
            .setDefaultAdvContentTypeParameters(
                SlotsProtobufModel.DefaultAdvContentTypeParameters.newBuilder()
                    .setDefaultData(ByteString.copyFrom(defaultData))
            ).build()
    }

}

class UidContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        return mutableMapOf<String, String>().apply {
            put(Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID,protobufModel.uidAdvContentTypeParameters.namespaceId.toByteArray().toCompactHex())
            put(Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID,protobufModel.uidAdvContentTypeParameters.instanceId.toByteArray().toCompactHex())

        }
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        val namespaceId = parametersMap[Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID]?.hexStringToByteArray() ?: byteArrayOf()
        val instanceId = parametersMap[Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID]?.hexStringToByteArray() ?: byteArrayOf()

        return SlotsProtobufModel.AdvertisingContent.newBuilder()
            .setUidAdvContentTypeParameters(
                SlotsProtobufModel.UidAdvContentTypeParameters.newBuilder()
                    .setNamespaceId(ByteString.copyFrom(namespaceId))
                    .setInstanceId(ByteString.copyFrom(instanceId))
            )
            .build()
    }

}



class UrlContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        val urlPrefix = urlPrefixProtobufEnumToString(protobufModel.urlAdvContentTypeParameters.urlPrefix)

        return mutableMapOf<String, String>().apply {
            put(Slot.KEY_ADVERTISING_CONTENT_URL_URL,urlPrefix+protobufModel.urlAdvContentTypeParameters.url)
        }
    }

    private fun urlPrefixProtobufEnumToString(prefix : SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix?) : String {
        return when(prefix) {
            SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTP -> URL_PREFIX_HTTP
            SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTPS -> URL_PREFIX_HTTPS
            SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTP_WWW -> URL_PREFIX_HTTP_WWW
            SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTPS_WWW -> URL_PREFIX_HTTPS_WWW
            null -> ""
            SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.UNRECOGNIZED -> throw IllegalStateException("UrlPrefix must not have value of UNRECOGNIZED")
        }
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        var url = parametersMap[Slot.KEY_ADVERTISING_CONTENT_URL_URL] ?: ""
        val urlPrefix = when {
            url.startsWith(URL_PREFIX_HTTP_WWW) -> {
                url = url.substring(URL_PREFIX_HTTP_WWW.length)
                SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTP_WWW
            }
            url.startsWith(URL_PREFIX_HTTPS_WWW) -> {
                url = url.substring(URL_PREFIX_HTTPS_WWW.length)
                SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTPS_WWW
            }
            url.startsWith(URL_PREFIX_HTTP) -> {
                url = url.substring(URL_PREFIX_HTTP.length)
                SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTP
            }
            url.startsWith(URL_PREFIX_HTTPS) -> {
                url = url.substring(URL_PREFIX_HTTPS.length)
                SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTPS
            }
            else -> null
        }

        return SlotsProtobufModel.AdvertisingContent.newBuilder()
            .setUrlAdvContentTypeParameters(
                SlotsProtobufModel.UrlAdvContentTypeParameters.newBuilder()
                    .setUrlPrefix(urlPrefix)
                    .setUrl(url)
            )
        .build()
    }

    companion object {
        const val URL_PREFIX_HTTP_WWW = "http://www."
        const val URL_PREFIX_HTTPS_WWW = "https://www."
        const val URL_PREFIX_HTTP = "http://"
        const val URL_PREFIX_HTTPS = "https://"
    }

}


//TODO: implement this content converter
class TlmContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        return mutableMapOf<String, String>()
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        return SlotsProtobufModel.AdvertisingContent.newBuilder().setTlmAdvContentTypeParameters(
            SlotsProtobufModel.TlmAdvContentTypeParameters.newBuilder()
        )
            .build()
    }

}



class IBeaconContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        val iBeaconParams = protobufModel.ibeaconAdvContentTypeParameters
        return mutableMapOf<String, String>().apply {
            put(Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID,iBeaconParams.uuid)
            put(Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,iBeaconParams.major.toString())
            put(Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR,iBeaconParams.minor.toString())
        }
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        val uuid: UUID =
            try {
                UUID.fromString(
                    parametersMap[Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID]
                        ?: Slot.DEFAULT_ADVERTISING_CONTENT_IBEACON_UUID
                )
            } catch (ex : IllegalArgumentException) {
                UUID.fromString(Slot.DEFAULT_ADVERTISING_CONTENT_IBEACON_UUID)
            }

        val major: Int = if(parametersMap[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR]?.isNotEmpty() != true) {
            0
        } else {
            parametersMap[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR]?.toInt() ?: 0
        }
        val minor: Int = if(parametersMap[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR]?.isNotEmpty() != true) {
            0
        } else {
            parametersMap[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR]?.toInt() ?: 0
        }

        return SlotsProtobufModel.AdvertisingContent.newBuilder()
            .setIbeaconAdvContentTypeParameters(
                SlotsProtobufModel.IBeaconAdvContentTypeParameters.newBuilder()
                    .setUuid(uuid.toString())
                    .setMajor(major)
                    .setMinor(minor)
            )
            .build()
    }

}

class DeviceInfoContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        return mutableMapOf()
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        return SlotsProtobufModel.AdvertisingContent.newBuilder().build()
    }

}

class EmptyContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        return mutableMapOf()
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        return SlotsProtobufModel.AdvertisingContent.newBuilder().build()
    }

}



class CustomContentProtobufConverter : AdvertisingContentProtobufConverter {
    override fun convertFromProtobufModel(protobufModel: SlotsProtobufModel.AdvertisingContent) : Map<String,String> {
        return mutableMapOf<String, String>().apply {
            put(Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,protobufModel.customAdvContentTypeParameters.customContent.toByteArray().toCompactHex())
        }
    }

    override fun convertToProtobufModel(parametersMap :  Map<String,String>): SlotsProtobufModel.AdvertisingContent {
        val content = parametersMap[Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM]?.hexStringToByteArray()
            ?: byteArrayOf()

        return SlotsProtobufModel.AdvertisingContent.newBuilder()
            .setCustomAdvContentTypeParameters(
                SlotsProtobufModel.CustomAdvContentTypeParameters.newBuilder()
                    .setCustomContent(ByteString.copyFrom(content))
            )
            .build()
    }

}