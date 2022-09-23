const pulsesSpan = document.getElementById("pulses");
const image = document.getElementById("image");

function onSensorReadings(json_values){
    JSON.parse(json_values, function (key, value) {
        if (key === "Pulses") {
            let pulses = parseInt(value);
            pulsesSpan.innerHTML = pulses.toString();
        }
    });
}