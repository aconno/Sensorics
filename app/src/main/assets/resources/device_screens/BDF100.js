const status = document.getElementById("status");
const image = document.getElementById("image");
const led1Checkbox = document.getElementById("led1");
const led2Checkbox = document.getElementById("led2");
const led3Checkbox = document.getElementById("led3");

const buttonServiceUuid = '00118000-4455-6677-8899-aabbccddeeff';
const buttonCharacteristicUuid = '00118001-4455-6677-8899-aabbccddeeff';
const ledCharacteristicUuid = '00118002-4455-6677-8899-aabbccddeeff';


function onSensorReadings(json_values){
    JSON.parse(json_values, function (key, value) {
    });
}

function onStatusReading(text) {
    text = text.toLocaleUpperCase();

    console.log(text);
    if (text.localeCompare("CONNECTED") === 0 || text.localeCompare("VERBUNDEN") === 0) {
        setConnected(true);
    } else if (text.localeCompare("SERVICES DISCOVERED") === 0 || text.localeCompare("DIENSTE ENTDECKT") === 0) {
    } else if (text.localeCompare("MTU CHANGED") === 0) {
        enableNotifications();
        setTimeout(function(){ readData(); }, 500);
    } else if (text.localeCompare("DEVICE NOT FOUND") === 0 || text.localeCompare("GERÄT NICHT GEFUNDEN") === 0) {
        setConnected(false);
    } else if (text.localeCompare("DISCONNECTED") === 0 || text.localeCompare("GETRENNT") === 0) {
        setConnected(false);
    } else if (text.localeCompare("ERROR") === 0 || text.localeCompare("ERROR") === 0) {
    } else {
        console.log(text);
    }
}

function setConnected(isConnected) {
    status.innerHTML = isConnected ? "CONNECTED" : "NOT CONNECTED";
}

function enableNotifications() {
    app.enableNotifications(
        buttonCharacteristicUuid,
        buttonServiceUuid,
        true
    );
}

function readData() {
    app.readCharacteristic(buttonServiceUuid, buttonCharacteristicUuid);
    setTimeout(function(){ app.readCharacteristic(buttonServiceUuid, ledCharacteristicUuid); }, 500);
}

function writeData() {
    let data = ((led1Checkbox.checked ? 0x010000 : 0x000000) | (led2Checkbox.checked ? 0x000100 : 0x000000) | (led3Checkbox.checked ? 0x000001 : 0x000000)).toString(16).padStart(6, '0');
    app.writeCharacteristicHexString(
        buttonServiceUuid,
        ledCharacteristicUuid,
        data
    );
}

function onCharRead(charUuid, hexData) {
    console.log(hexData);

    if (charUuid === buttonCharacteristicUuid || charUuid === "null") {
        image.src = `./bdf100_assets/${parseInt(hexData, 16).toString(2).padStart(1, '0')}.png`;
    } else if (charUuid === ledCharacteristicUuid) {
        led1Checkbox.checked = hexData.substring(0, 2) === "01";
        led2Checkbox.checked = hexData.substring(2, 4) === "01";
        led3Checkbox.checked = hexData.substring(4, 6) === "01";
    } else {
        console.log(charUuid);
        console.log(hexData);
    }
}