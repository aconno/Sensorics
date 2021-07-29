const ENERGY_ADVERTISEMENT_KEY = "Energy";
const POWER_ADVERTISEMENT_KEY = "Power";
const ENERGY_COLOR = "#177a2b";
const POWER_COLOR = "#3f36b0";

let energyElement, powerElement, chart;


function onSensorReadings(json_values) {
    JSON.parse(json_values, function (key, value) {
        switch (key) {
            case ENERGY_ADVERTISEMENT_KEY: {
                let energyInWattHours = parseFloat(value);

                setEnergy(energyInWattHours);
                break;
            }
            case POWER_ADVERTISEMENT_KEY: {
                let powerInWatts = parseFloat(value);

                setPower(powerInWatts);
                break;
            }
        }
    });
}

function setEnergy(energy_in_watt_hours) {
    let energy_in_kwh = energy_in_watt_hours / 1000;

    energyElement.innerHTML = energy_in_kwh.toFixed(3) + " kWh";
    chart.options.data[0].dataPoints.push({x: new Date(), y: energy_in_kwh});
}

function setPower(power_in_watts) {
    let power_in_kw = power_in_watts / 1000;

    powerElement.innerHTML = power_in_kw.toFixed(3) + " kW";
    chart.options.data[1].dataPoints.push({x: new Date(), y: power_in_kw});
}

window.onload = function () {
    chart = new CanvasJS.Chart("chartContainer", {
        theme: "light1", // "light1", "light2", "dark1", "dark2"
        animationEnabled: true,
        zoomEnabled: true,
        title: {
            text: "Energy & Power Chart"
        },
        axisY: [{
            title: "Energy",
            suffix: " kWh",
            titleFontColor: ENERGY_COLOR,
            labelFontColor: ENERGY_COLOR
        }],
        axisY2: [{
            title: "Power",
            suffix: " kW",
            titleFontColor: POWER_COLOR,
            labelFontColor: POWER_COLOR
        }],
        data: [{
            axisYIndex: 0,
            type: "splineArea",
            yValueFormatString: '#0.#',
            color: ENERGY_COLOR,
            xValueType: "dateTime",
            xValueFormatString: "DD MMM YY HH:mm",
            legendMarkerType: "square",
            dataPoints: []
        }, {
            axisYIndex: 1,
            type: "column",
            axisYType: "secondary",
            yValueFormatString: '#0.#',
            color: POWER_COLOR,
            xValueType: "dateTime",
            xValueFormatString: "DD MMM YY HH:mm",
            legendMarkerType: "square",
            dataPoints: []
        }]
    });

    addDataPoints(100);

    chart.render();

    function addDataPoints(noOfDps) {
        let xVal = new Date(), yVal = 100, yVal2 = 100;

        for (let i = 0; i < noOfDps; i++) {
            yVal += Math.random() * 100;
            yVal2 = 100 + Math.random() * 100;
            chart.options.data[0].dataPoints.push({x: xVal, y: yVal});
            chart.options.data[1].dataPoints.push({x: xVal, y: yVal2});
            xVal = new Date(xVal.valueOf() + 864E5);
        }
    }

    energyElement = document.getElementById("energy_value")
    powerElement = document.getElementById("power_value")
}
