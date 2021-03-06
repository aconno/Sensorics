class Slot {
    constructor() {
        this.type = FrameType.DEFAULT
        this.frame = {}
        this.name = "Name"
        this.active = true
        this.advertisingMode = true
        this.packetCount = 0
        this.supportedtxPower = []
        this.txPower = 0
        this.readOnly = false
        this.addInterval = 0
    }
}

var FrameType = {
    UID: "UID",
    URL: "URL",
    TLM: "TLM",
    IBEACON: "I_BEACON",
    DEVICE_INFO: "DEVICE_INFO",
    DEFAULT: "DEFAULT",
    EMPTY: "EMPTY",
    CUSTOM: "CUSTOM"
};

const KEY_ADVERTISING_CONTENT_IBEACON_UUID =
    "ADVERTISING_CONTENT_IBEACON_UUID"
const KEY_ADVERTISING_CONTENT_IBEACON_MAJOR =
    "ADVERTISING_CONTENT_IBEACON_MAJOR"
const KEY_ADVERTISING_CONTENT_IBEACON_MINOR =
    "ADVERTISING_CONTENT_IBEACON_MINOR"
const KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID =
    "ADVERTISING_CONTENT_UID_NAMESPACE_ID"
const KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID =
    "ADVERTISING_CONTENT_UID_INSTANCE_ID"
const KEY_ADVERTISING_CONTENT_URL_URL =
    "ADVERTISING_CONTENT_URL_URL"
const KEY_ADVERTISING_CONTENT_DEFAULT_DATA =
     "ADVERTISING_CONTENT_DEFAULT_DATA"
const KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM =
    "ADVERTISING_CONTENT_CUSTOM_CUSTOM"
const KEY_ADVERTISING_CONTENT_CUSTOM_IS_HEX_MODE_ON =
    "ADVERTISING_CONTENT_CUSTOM_IS_HEX_MODE_ON"