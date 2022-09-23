const temperature = document.getElementById("temperature");
const image = document.getElementById("image");

function onSensorReadings(json_values){
    JSON.parse(json_values, function (key, value) {
        if (key === "DoorState") {
            image.src = `./acndoor_assets/door_${value === 1 ? "open": "closed"}.png`;
        }

        if (key === "Temperature") {
            temperature.innerHTML = value.toString();
        }
    });
}