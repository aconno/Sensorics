class Slot {
    constructor() {
        this.frameType = FrameType.EMPTY
        this.frame = "{}"
        this.advertisingInterval = 1000
        this.rssi1m = -55
        this.radioTx = -55
        this.triggerEnabled = true
        this.triggerType = TriggerType.DOUBLE_TAP
    }
}

//let uidToRaw = function(map){
//
//    let whatever = new Array();
//    whatever.push(
//          0x02,
//          0x01,
//          0x06,
//          0x03,
//          0x03,
//          0xAA,
//          0xFE,
//          0x24,
//          0x16,
//          0xAA,
//          0xFE,
//          0x00, 0x10, // TODO TODO TODO
//          map.get(keyString),
//    );
//};

var FrameType = {
    EMPTY: 1,
    UID: 2,
    URL: 3,
    IBEACON: 4,
    CUSTOM: 5
};

var TriggerType = {
    DOUBLE_TAP: 1,
    TRIPLE_TAP: 2,
};

//function parseHexString(str) {
//    var result = [];
//    while (str.length >= 8) {
//        result.push(parseInt(str.substring(0, 8), 16));
//
//        str = str.substring(8, str.length);
//    }
//
//    return result;
//}
//
//function createHexString(arr) {
//    var result = "";
//    var z;
//
//    for (var i = 0; i < arr.length; i++) {
//        var str = arr[i].toString(16);
//
//        z = 8 - str.length + 1;
//        str = Array(z).join("0") + str;
//
//        result += str;
//    }
//
//    return result;
//}