const status = document.getElementById("status");
const image = document.getElementById("image");

const buttonServiceUuid = '00118000-4455-6677-8899-aabbccddeeff';
const buttonCharacteristicUuid = '00118001-4455-6677-8899-aabbccddeeff';

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
}

function onCharRead(charUuid, hexData) {
    console.log(hexData);

    if (charUuid === buttonCharacteristicUuid || charUuid === "null") {
        let code = 0b000;
        code |= hexData.substring(0, 2) === "01" ? 0b001 : 0b000;
        code |= hexData.substring(2, 4) === "01" ? 0b100 : 0b000;
        code |= hexData.substring(4, 6) === "01" ? 0b010 : 0b000;

        const stringCode = code.toString(2).padStart(3, '0');

        image.src = `./bdf200_assets/${stringCode}.png`;
    } else {
        console.log(charUuid);
        console.log(hexData);
    }
}