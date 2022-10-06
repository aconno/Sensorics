const buttonDown = document.getElementById("button_down");
const buttonQuickDown = document.getElementById("button_quick_down");
const buttonUp = document.getElementById("button_up");
const buttonQuickUp = document.getElementById("button_quick_up");

const moveServiceUuid = '00118000-4455-6677-8899-aabbccddeeff';
const moveCharacteristicUuid = '00118001-4455-6677-8899-aabbccddeeff';

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
        setConnected(true);
    } else if (text.localeCompare("MTU CHANGED") === 0) {
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
    if(isConnected){
        buttonUp.removeAttribute("disabled");
        buttonQuickUp.removeAttribute("disabled");
        buttonDown.removeAttribute("disabled");
        buttonQuickDown.removeAttribute("disabled");
    } else {
        buttonUp.setAttribute("disabled", true);
        buttonQuickUp.setAttribute("disabled", true);
        buttonDown.setAttribute("disabled", true);
        buttonQuickDown.setAttribute("disabled", true);
    }
}

function onDownKeyPressed() {
    //send -1
    app.writeCharacteristicHexString(
        moveServiceUuid,
        moveCharacteristicUuid,
        "FF"
    );
}

function onDownKeyReleased() {
    //send 0
    app.writeCharacteristicHexString(
        moveServiceUuid,
        moveCharacteristicUuid,
        "00"
    );
}

function onUpKeyPressed() {
    //send 1
    app.writeCharacteristicHexString(
        moveServiceUuid,
        moveCharacteristicUuid,
        "01"
    );
}

function onUpKeyReleased() {
    //send 0
    app.writeCharacteristicHexString(
        moveServiceUuid,
        moveCharacteristicUuid,
        "00"
    );
}