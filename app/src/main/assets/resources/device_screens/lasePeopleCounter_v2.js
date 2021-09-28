let counter = 0;

document.addEventListener('DOMContentLoaded', function () {
    var elems     = document.querySelectorAll('select');
    var instances = M.FormSelect.init(elems);
});

const PEOPLE_ENTERED_KEY = "PeopleEntered";
const PEOPLE_EXITED_KEY  = "PeopleExited";
const PEOPLE_IN_ROOM_KEY = "PeopleInRoom";

// "uuid": "00110021-1111-1111-1111-111111111111"

function createOdometer(elementId) {
    return new Odometer({
        el: document.querySelector(elementId),
        value: 0,
        format: 'd', // Change how digit groups are formatted, and how many digits are shown after the decimal point
        duration: 500, // Change how long the javascript expects the CSS animation to take
        theme: 'car', // Specify the theme (if you have more than one theme css file on the page)
        animation: 'count' // Count is a simpler animation method which just increments the value
    });
}

odPeopleEntered = createOdometer('#odometerPeopleEntered');
odPeopleExited  = createOdometer('#odometerPeopleExited');
odPeopleInRoom  = createOdometer('#odometerPeopleInRoom');

function onSensorReadings(json_values) {
    console.log(json_values);

    JSON.parse(json_values, function (key, value) {
        console.log("KEY" + key + ", value: " + value);
        if (key === PEOPLE_ENTERED_KEY) {
            let entered = parseInt(value);
            odPeopleEntered.update(entered);
            document.getElementById("peopleEntered").value = entered;
        } else if (key === PEOPLE_EXITED_KEY) {
            let exited = parseInt(value);
            odPeopleExited.update(exited);
            document.getElementById("peopleExited").value = exited;
        } else if (key === PEOPLE_IN_ROOM_KEY) {
            odPeopleInRoom.update(parseInt(value));
        }
    });
}

let totalToWrite = 0;
let leftToWrite  = 0;

function onCharWritten(charUuid) {
    leftToWrite--;

    if (leftToWrite === 0) {
        setWritingText(`${totalToWrite}/${totalToWrite} written!`)
        setFormsVisible(true);
    } else {
        setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`)
    }
}

function wifiPicked(ssid) {
    console.log(ssid)
    document.getElementById("wifiSsid").value = ssid;
    M.updateTextFields();
}

function setConnected(connected) {
    let formsDiv   = document.getElementById("forms");
    let noFormsDiv = document.getElementById("noForms");

    if (connected) {
        formsDiv.style.display   = "block";
        noFormsDiv.style.display = "none";
    } else {
        formsDiv.style.display   = "none";
        noFormsDiv.style.display = "block";
    }
}

function setFormsVisible(visible) {
    let formsDiv = document.getElementById("forms");

    if (visible) {
        formsDiv.style.display = "block";
    } else {
        formsDiv.style.display = "none";
    }
}

function setWritingText(text) {
    document.getElementById("writingText").innerText = text;
}

function onStatusReading(text) {
    if (text.localeCompare("CONNECTED") === 0 || text.localeCompare("VERBUNDEN") === 0) {
        setConnected(true);
        setWritingText("");
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
        console.log("Reading text is empty")
    }
}

function setTextValue(elementId, value) {
    document.getElementById(elementId).innerHTML = value
}

function isInt(n) {
    return n % 1 === 0;
}

function makeItAnInt(it) {
    let doorHeight = it.value;

    if (!isInt(doorHeight) || doorHeight.toString().includes(".")) {
        it.value = Math.floor(doorHeight);
    }

    if (doorHeight < 0) {
        it.value = Math.floor(220);
    }
}

let mqttServiceUuid       = "00110020-1111-1111-1111-111111111111";
let mqttSchemaUuid        = "00110021-1111-1111-1111-111111111111";
let mqttClientIdUuid      = "00110022-1111-1111-1111-111111111111";
let mqttUsernameUuid      = "00110023-1111-1111-1111-111111111111";
let mqttPasswordUuid      = "00110024-1111-1111-1111-111111111111";
let mqttBrokerUuid        = "00110025-1111-1111-1111-111111111111";
let mqttAutoReconnectUuid = "00110026-1111-1111-1111-111111111111";
let mqttPortUuid          = "00110027-1111-1111-1111-111111111111";
let mqttTopicUuid         = "00110028-1111-1111-1111-111111111111";
let mqttQosUuid           = "00110029-1111-1111-1111-111111111111";

let wifiServiceUuid  = "00110070-1111-1111-1111-111111111111";
let wifiSsidUuid     = "00110071-1111-1111-1111-111111111111";
let wifiPasswordUuid = "00110072-1111-1111-1111-111111111111";

let peopleCountServiceUuid               = "00110050-1111-1111-1111-111111111111";
let peopleCountEnteredUuid               = "00110051-1111-1111-1111-111111111111";
let peopleCountExitedUuid                = "00110052-1111-1111-1111-111111111111";
let sensorConfigurationInvertDirectionId = "00110053-1111-1111-1111-111111111111";
let sensorConfigurationDoorHeightId      = "00110054-1111-1111-1111-111111111111";

function writeSensorConfiguration() {
    let doorHeight      = document.getElementById("sensorConfigurationDoorHeight").value;
    let invertDirection = document.getElementById("sensorConfigurationInvertDirection").checked;


    if (doorHeight.length == 0) {
        alert("Please fill out all necessary fields!");
        return;
    }

    doorHeight = Math.floor(parseFloat(doorHeight));
    if (isNaN(doorHeight)) {
        doorHeight = 220;
    }

    console.log("doorHeight: " + doorHeight.toString());
    console.log("invertDirection: " + invertDirection.toString());

    totalToWrite = 1; // Change to 2 after door height
    leftToWrite  = totalToWrite;
    setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`);
    setFormsVisible(false);

    // app.writeCharacteristicRawUnsignedInt32(sensorConfigurationServiceId, sensorConfigurationDoorHeightId, doorHeight);
    app.writeCharacteristicRawUnsignedInt8(sensorConfigurationServiceId, sensorConfigurationInvertDirectionId, invertDirection ? 1 : 0);
}

function writePeopleCount() {
    let peopleEntered = document.getElementById("peopleEntered").value;
    let peopleExited  = document.getElementById("peopleExited").value;

    if (peopleEntered.length == 0 || peopleExited.length == 0) {
        alert("Please fill out all necessary fields!");
        return;
    }

    peopleEntered = parseInt(peopleEntered);
    peopleExited  = parseInt(peopleExited);

    if (isNaN(peopleEntered)) {
        peopleEntered = 0;
    }
    if (isNaN(peopleExited)) {
        peopleExited = 0;
    }

    console.log("peopleEntered: " + peopleEntered.toString());
    console.log("peopleExited: " + peopleExited.toString());

    totalToWrite = 2;
    leftToWrite  = totalToWrite;
    setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`);

    setFormsVisible(false);

    app.writeCharacteristicRawUnsignedInt32(peopleCountServiceUuid, peopleCountEnteredUuid, peopleEntered);
    app.writeCharacteristicRawUnsignedInt32(peopleCountServiceUuid, peopleCountExitedUuid, peopleExited);
}

function resetPeopleCount() {
    console.log("Resetting people count...")
    let peopleEntered = 0;
    let peopleExited  = 0;

    totalToWrite = 2;
    leftToWrite  = totalToWrite;
    setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`);

    setFormsVisible(false);

    app.writeCharacteristicRawUnsignedInt32(peopleCountServiceUuid, peopleCountEnteredUuid, peopleEntered);
    app.writeCharacteristicRawUnsignedInt32(peopleCountServiceUuid, peopleCountExitedUuid, peopleExited);
}

function writeWifiConfig() {
    let wifiSsid     = document.getElementById("wifiSsid").value;
    let wifiPassword = document.getElementById("wifiPassword").value;

    console.log("wifiSsid: " + wifiSsid.toString());
    console.log("wifiPassword: " + wifiPassword.toString());

    totalToWrite = 2;
    leftToWrite  = totalToWrite;
    setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`);

    setFormsVisible(false);

    app.writeCharacteristicRawString(wifiServiceUuid, wifiSsidUuid, wifiSsid);
    app.writeCharacteristicRawString(wifiServiceUuid, wifiPasswordUuid, wifiPassword);
}

function writeMqttConfig() {
    let mqttSchema        = parseInt(document.getElementById("mqttSchema").value);
    let mqttClientId      = document.getElementById("mqttClientId").value.trim();
    let mqttUsername      = document.getElementById("mqttUsername").value.trim();
    let mqttPassword      = document.getElementById("mqttPassword").value;
    let mqttBroker        = document.getElementById("mqttBroker").value.trim();
    let mqttAutoReconnect = document.getElementById("mqttAutoReconnect").checked;
    let mqttPort          = document.getElementById("mqttPort").value.trim();
    let mqttTopic         = document.getElementById("mqttTopic").value;
    let mqttQos           = parseInt(document.querySelector('input[name="mqttQos"]:checked').value);

    if (mqttClientId.length == 0 || mqttBroker.length == 0 || mqttPort.length == 0 || mqttTopic.length == 0) {
        alert("Please fill out all necessary fields!");
        return;
    }

    mqttPort = parseInt(mqttPort);
    if (isNaN(mqttPort)) {
        mqttPort = 0;
    }

    console.log("mqttSchema: " + mqttSchema.toString());
    console.log("mqttClientId: " + mqttClientId.toString());
    console.log("mqttUsername: " + mqttUsername.toString());
    console.log("mqttPassword: " + mqttPassword.toString());
    console.log("mqttBroker: " + mqttBroker.toString());
    console.log("mqttAutoReconnect: " + mqttAutoReconnect.toString());
    console.log("mqttPort: " + mqttPort.toString());
    console.log("mqttTopic: " + mqttTopic.toString());
    console.log("mqttQos: " + mqttQos.toString());

    totalToWrite = 9;
    leftToWrite  = totalToWrite;
    setWritingText(`${totalToWrite - leftToWrite}/${totalToWrite} written...`);
    setFormsVisible(false);

    app.writeCharacteristicRawUnsignedInt8(mqttServiceUuid, mqttSchemaUuid, mqttSchema);
    app.writeCharacteristicRawString(mqttServiceUuid, mqttClientIdUuid, mqttClientId);
    app.writeCharacteristicRawString(mqttServiceUuid, mqttUsernameUuid, mqttUsername);
    app.writeCharacteristicRawString(mqttServiceUuid, mqttPasswordUuid, mqttPassword);
    app.writeCharacteristicRawString(mqttServiceUuid, mqttBrokerUuid, mqttBroker);
    app.writeCharacteristicRawUnsignedInt8(mqttServiceUuid, mqttAutoReconnectUuid, mqttAutoReconnect ? 1 : 0);
    app.writeCharacteristicRawUnsignedInt16(mqttServiceUuid, mqttPortUuid, mqttPort);
    app.writeCharacteristicRawString(mqttServiceUuid, mqttTopicUuid, mqttTopic);
    app.writeCharacteristicRawUnsignedInt8(mqttServiceUuid, mqttQosUuid, mqttQos);
}

function scanWifi() {
    app.scanForWifi()
}