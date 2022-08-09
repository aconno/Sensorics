let totalToWrite = 0;
let leftToWrite = 0;

const MAC_ADDRESS_KEY = "macAddress";
const RSSI_KEY = "rssi";
const TIMESTAMP_KEY = "timestamp";
const RSSI_AT_1M_KEY = "RSSI at 1m";
const RSSI_STANDARD_DEVIATION_KEY = "RSSI standard deviation";


const protobufServiceUuid = "00111000-4455-6677-8899-aabbccddeeff";
const protobufCharacteristicReadUuid = "00111001-4455-6677-8899-aabbccddeeff";
const protobufCharacteristicWriteUuid = "00111002-4455-6677-8899-aabbccddeeff";
const deviceInformationServiceUuid = "0000180A-0000-1000-8000-00805f9b34fb";
const manufacturerNameStringCharacteristicUuid = "00002a29-0000-1000-8000-00805f9b34fb";
const modelNumberStringCharacteristicUuid = "00002a24-0000-1000-8000-00805f9b34fb";
const hardwareRevisionStringCharacteristicUuid = "00002a27-0000-1000-8000-00805f9b34fb";
const firmwareRevisionStringCharacteristicUuid = "00002a26-0000-1000-8000-00805f9b34fb";
const softwareRevisionStringCharacteristicUuid = "00002a28-0000-1000-8000-00805f9b34fb";

function onCharWritten(charUuid) {
    leftToWrite--;

    if (leftToWrite === 0) {
        setWritingText(`${totalToWrite}/${totalToWrite} written!`)
        setFormsVisible(true);
    } else {
        setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`)
    }
}

function onSensorReadings(json_values) {
    JSON.parse(json_values, (key, value) => {
        if (key === MAC_ADDRESS_KEY) {
            window.set_mac_address(value);
        }
    });
}

function hexToString(hexstring) {
    return hexstring.match(/\w{2}/g).map(function (a) {
        return String.fromCharCode(parseInt(a, 16));
    }).join("");
}

function hexToBase64(hexstring) {
    return btoa(hexToString(hexstring));
}

function onCharRead(charUuid, hexData) {
    console.log(hexData);

    if (charUuid === protobufCharacteristicReadUuid) {
        if (hexData.substring(0, 4) !== "5555") {
            alert("Invalid preamble!");
            return;
        }

        if (hexData.substring(4, 8) !== "0000") {
            alert("Invalid version!");
            return;
        }

        let length = parseInt(hexData.substring(8, 10), 16) * 2 + (parseInt(hexData.substring(10, 12)) << 16);
        console.log(length);

        if (length < 0 || length > (hexData.length - 12)) {
            alert(`Invalid data length {hexData.length} {length}!`);
            return;
        }

        let trimmedData = hexData.substring(12, 12 + length);

        console.log(trimmedData);

        window.set_data(hexToBase64(hexData.substring(12, 12 + length)));
    } else if (charUuid === manufacturerNameStringCharacteristicUuid) {
        window.update_device_information_service_data({
            "manufacturer": hexToString(hexData)
        });
    } else if (charUuid === manufacturerNameStringCharacteristicUuid) {
        window.update_device_information_service_data({
            "model": hexToString(hexData)
        });
    } else if (charUuid === modelNumberStringCharacteristicUuid) {
        window.update_device_information_service_data({
            "manufacturer": hexToString(hexData)
        });
    } else if (charUuid === hardwareRevisionStringCharacteristicUuid) {
        window.update_device_information_service_data({
            "hardware_version": hexToString(hexData)
        });
    } else if (charUuid === firmwareRevisionStringCharacteristicUuid) {
        window.update_device_information_service_data({
            "firmware_version": hexToString(hexData)
        });
    } else if (charUuid === softwareRevisionStringCharacteristicUuid) {
        window.update_device_information_service_data({
            "software_version": hexToString(hexData)
        });
    }
}

function setFormsVisible(visible) {
    let formsDiv = document.getElementById("root");

    if (visible) {
        formsDiv.style.display = "block";
    } else {
        formsDiv.style.display = "none";
    }
}

function setConnected(connected) {
    let formsDiv = document.getElementById("root");
    let noFormsDiv = document.getElementById("noForms");

    if (connected) {
        formsDiv.style.display = "block";
        noFormsDiv.style.display = "none";
    } else {
        formsDiv.style.display = "none";
        noFormsDiv.style.display = "block";
    }
}

function setTextValue(elementId, value) {
    document.getElementById(elementId).innerHTML = value
}

function setWritingText(text) {
    document.getElementById("writingText").innerText = text;
}

function read_data() {
    app.readCharacteristic(protobufServiceUuid, protobufCharacteristicReadUuid);
    app.readCharacteristic(deviceInformationServiceUuid, manufacturerNameStringCharacteristicUuid);
    app.readCharacteristic(deviceInformationServiceUuid, modelNumberStringCharacteristicUuid);
    app.readCharacteristic(deviceInformationServiceUuid, hardwareRevisionStringCharacteristicUuid);
    app.readCharacteristic(deviceInformationServiceUuid, firmwareRevisionStringCharacteristicUuid);
    app.readCharacteristic(deviceInformationServiceUuid, softwareRevisionStringCharacteristicUuid);
}

function onStatusReading(text) {
    text = text.toLocaleUpperCase();

    console.log(text);
    if (text.localeCompare("CONNECTED") === 0 || text.localeCompare("VERBUNDEN") === 0) {
        setConnected(true);
        setWritingText("");
        read_data();
    } else if (text.localeCompare("SERVICES DISCOVERED") === 0 || text.localeCompare("DIENSTE ENTDECKT") === 0) {
        setConnected(true);
        setWritingText("");
        console.log("Requesting!")
    } else if (text.localeCompare("MTU CHANGED") === 0) {
        setTimeout(function () {
            read_data();
        }, 500);
    } else if (text.localeCompare("DEVICE NOT FOUND") === 0 || text.localeCompare("GERÄT NICHT GEFUNDEN") === 0) {
        setConnected(false);
        setWritingText("");
    } else if (text.localeCompare("DISCONNECTED") === 0 || text.localeCompare("GETRENNT") === 0) {
        setConnected(false);
        setWritingText("");
    } else if (text.localeCompare("ERROR") === 0 || text.localeCompare("ERROR") === 0) {
        setWritingText("Error")
        setFormsVisible(true);
    } else {
        console.log(text)
    }
}

window.write_data = function (data_bytes) {
    const data = Array.from(data_bytes).map(value => ('0' + value.toString(16)).slice(-2)).join('');

    totalToWrite = 1;
    leftToWrite = totalToWrite;
    setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`);

    const byte_count = parseInt(data.length) / 2;
    console.log(byte_count);
    console.log(byte_count.toString(16));
    console.log(byte_count.toString(16).padStart(4, '0'));

    setFormsVisible(false);

    let packet = "5555" + "0000" + byte_count.toString(16).padStart(4, '0').substring(2, 4) + byte_count.toString(16).padStart(4, '0').substring(0, 2) + data;
    console.log(packet);
    app.writeCharacteristicHexString(protobufServiceUuid, protobufCharacteristicWriteUuid, packet);
}