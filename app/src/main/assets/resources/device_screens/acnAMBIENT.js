function onSensorReadings(json_values){
    JSON.parse(json_values, function (key, value) {
        if( key == "Temperature")
        {
             var temperature_value = document.getElementById("temperature_value");
             temperature_value.innerHTML = parseFloat(value).toFixed(2) + " °C";
        }
        else if ( key == "Humidity")
        {
            var humidity_value = document.getElementById("humidity_value");
            humidity_value.innerHTML = parseFloat(value).toFixed(2) + " %";
        }
        else if (key == "Illumination")
        {
            var illumination_value = document.getElementById("illumination_value");
            illumination_value.innerHTML = parseFloat(value).toFixed(2) + " lux";
        }
    });
}