const BLACK = '#000000';
var isChecked = 0;
var colorPicked = BLACK;
var ledOn = false;

function onSensorReadings(json_values){
    JSON.parse(json_values, function (key, value) {
        if( key == "MovementDetected")
        {
             var movement_value = document.getElementById("movement_value");
             movement_value.innerHTML = parseInt(value) == 1 ? 'DETECTED' : 'NOT DETECTED';
        }
    });
}

function onStatusReading(text) {
    if (text.localeCompare("CONNECTED") == 0 || text.localeCompare("VERBUNDEN") == 0) {
        document.getElementById("speaker").setAttribute("disabled", false);
        document.getElementById("close").disabled = false;
        document.getElementById("primary_color").disabled = false;
        document.getElementById("status").innerHTML = text;
    }
    else if (text != "") {
        document.getElementById("speaker").setAttribute("disabled", true);
        document.getElementById("close").disabled = true;
        document.getElementById("primary_color").disabled = true;
        document.getElementById("status").innerHTML = text;
    } else {
        console.log("Reading text is empty")
    }
}

function turnOnBuzzer() {
    if (isChecked == 0) {
        app.writeCharacteristicRawString(
            'cc520000-9adb-4c37-bc48-376f5fee8851',
            'cc520001-9adb-4c37-bc48-376f5fee8851',
            '\xFF\xFF\xFF\xFF\xFF'
        )
        isChecked = 1;
        document.getElementById("speaker").src = "../icons/speaker_on.png";
    }

    else {
        app.writeCharacteristicRawString(
            'cc520000-9adb-4c37-bc48-376f5fee8851',
            'cc520001-9adb-4c37-bc48-376f5fee8851',
            '\x00\x00\x00\x00\x00'
        )
        isChecked = 0;
        document.getElementById("speaker").src = "../icons/speaker_off.png";
    }
}

function changeColor(value) {
    color = value;
    console.log(value);
}

function switchLED() {
    ledOn = !ledOn;
    let value = null;
    let text = '';
    if (ledOn) {
        value = color;
        text = 'TURN LED OFF';
    } else {
        value = BLACK;
        text = 'TURN LED ON';
    }

    if (value != BLACK) {
        document.querySelector('input[type="color"]').value = value;
    }

    document.getElementById('close').value = text;
    app.writeCharacteristicRawString(
        'cc520000-9adb-4c37-bc48-376f5fee8851',
        'cc520002-9adb-4c37-bc48-376f5fee8851',
        String.fromCharCode(parseInt(color.substring(1, 3), 16)) + String.fromCharCode(parseInt(color.substring(3, 5), 16)) + String.fromCharCode(parseInt(color.substring(5, 7), 16)) + (ledOn ? '\xFF\xFF\xFF\xFF' : '\x00\x00\x00\x00'
    )
}