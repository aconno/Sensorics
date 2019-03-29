/******************************************************************************************************
*                                                                                                     *
*   Change Log:                                                                                       *
*                                                                                                     *
*   23/02/2019:-                                                                                      *
*     - Added min and max display.                                                                    *
*     - Modified newTemp() to prevent mercury line being drawn outside of thermometer it values       *
*       exceed min/max.                                                                               *
*                                                                                                     *
******************************************************************************************************/
"use strict";
// Declared global variables
var thermometer,        // The Snap.svg element.
    bulbCircle,         // The Snap.svg circle element for the bottom of the thermometer.
    outlinePath,        // A string containing the svg path for the outline.
    outlineDraw,        // The Snap.svg line element for the outline.
    mercuryDraw,        // The Snap.svg line element for the outline.
    maxHeight = 10,     // The minimum x coord for the mercury line.
    minHeight = 52,     // The maximum y coord for the mercury line.
    maxTemp = 40,       // The maximum displayable temperature.
    minTemp = -10,      // The minimum displayable temperature.
    currTemp = 0,       // The currently displayed temperature.
    height = 200,       // The viewport height.
    width = 50,         // The viewport width.
    mercuryText,        // The Snap.svg text element for the temperature displayed in the bulb.
    enableHighLow,      // Boolean value to enable or disable showing the max and min temperature values.
    highText,           // Text element for the high temperature.
    lowText;            // Text element for the low temperature.

/******************************************************************************************************
*                                                                                                     *
*   The main function.                                                                                *
*                                                                                                     *
*                                                                                                     *
*   Arguments:                                                                                        *
*                                                                                                     *
*   (string)  svg          : The DOM element string tag for the svg object (e.g. "#Thermometer");     *
*   (int)     initialT     : The initial temperature to display.                                      *
*   (int)     minT         : The minimum displayable temperature.                                     *
*   (int)     maxT         : The maximum displayable temperature.                                     *
*   (int)     h            : The svg viewport height.                                                 *
*   (int)     w            : The svg viewport width.                                                  *
*                                                                                                     *
******************************************************************************************************/
function initialiseThermometer(svg, initialT, minT, maxT, h, w, enablehighlow) {
  // Initialise the variables.
  maxTemp = maxT;
  minTemp = minT;
  currTemp = initialT;
  height = h;
  width = w;
  thermometer = Snap(svg);
  enableHighLow = enablehighlow;

  // Create the filled in circle for the mercury in the bulb.
  bulbCircle = thermometer.circle(13, 58.5, 8);  // circle(x, y, radius)
  bulbCircle.attr({
    fill: "#ff0000" // Fill color is red.
  });

  /*************************************************************************************************************************************
  * Define the outline path:                                                                                                           *
  *                                                                                                                                    *
  * "m 8,10" : Move to x:8 and y:10.                                                                                                   *
  *                                                                                                                                    *
  * "a5,5 1 1,1 10,0" : Draw the top arc of the thermometer.                                                                           *
  *                                                                                                                                    *
  * "v40" : Draw the right side vertical line down to the bulb.                                                                        *
  *                                                                                                                                    *
  * "a10,10 0 1,1 -10,0" : Draw the bulb of the thermometer.                                                                           *
  *                                                                                                                                    *
  * "z" : Close the outline.  Automatically draws the vertical line from the last point of the bulb to the first part of the outline.  *
  *                                                                                                                                    *
  *************************************************************************************************************************************/
  outlinePath = "m 8,10 a5,5 1 1,1 10,0 v40 a10,10 0 1,1 -10,0 z";
  outlineDraw = thermometer.path(outlinePath); // Draw the outline.
  outlineDraw.attr({
    fill: "none",
    stroke: "#000000",
    "stroke-width": 2,
    "stroke-linecap": "round",
    "stroke-linejoin": "round",
    "stroke-miterlimit": 10
  });

  /********************************************************************************************
  *                                                                                           *
  * Draw the mercury:                                                                         *
  *                                                                                           *
  * A simple line that starts at the bulb and draws up.                                       *
  * The length is determined by the temperature supplied converted to the acceptable range.   *
  *                                                                                           *
  * line(firstX, firstY, secondX, secondY)                                                    *
  *                                                                                           *
  ********************************************************************************************/
  mercuryDraw = thermometer.line(13, 51, 13, scaleToRange(currTemp));
  mercuryDraw.attr({
    fill: "none",
    stroke: "#FF0000",
    "stroke-width": 6,
    "stroke-linecap": "round",
    "stroke-linejoin": "round",
    "stroke-miterlimit": 10
  });

  /****************************************************
  *                                                   *
  * Draw the temperature value as text in the bulb.   *
  *                                                   *
  * text(x,y,string)                                  *
  *                                                   *
  ****************************************************/
  mercuryText = thermometer.text(12.5, 61, currTemp + "\u00B0");
  mercuryText.attr({
    "text-anchor": "middle",
    stroke: "none",
    fill: "#000000",
    "font-size": "6px",
    "font-family": "Arial",
    "font-weight": "bold"
  });


  /****************************************************
  *                                                   *
  * If enableHighLow is true then draw the max and    *
  * min values.                                       *
  *                                                   *
  ****************************************************/
  if(enableHighLow) {
    highText = thermometer.text(26, 10, currTemp + "\u00B0");
    highText.attr({
      "text-anchor": "middle",
      stroke: "none",
      fill: "#ff0000",
      "font-size": "6px",
      "font-family": "Arial",
      "font-weight": "bold"
    });


  lowText = thermometer.text(0, 51, currTemp + "\u00B0");
    lowText.attr({
      "text-anchor": "middle",
      stroke: "none",
      fill: "#0000ff",
      "font-size": "6px",
      "font-family": "Arial",
      "font-weight": "bold"
    });
  }
}

/**********************************************************
*                                                         *
* The function to call when you want to display           *
* a new value.                                            *
*                                                         *
*   Arguments:                                            *
*                                                         *
*   (int)  temp          : The temperature to display.    *
*                                                         *
**********************************************************/
function newTemp(temp, max, min) {
  mercuryText.attr({
    text: temp + "\u00B0"
  });
  var newHeight;


  var t;
  if(temp > maxTemp) {
    t = maxTemp;
  } else if(temp < minTemp) {
    t = minTemp;
  } else {
    t = temp;
  }
  newHeight = scaleToRange(t);
  mercuryDraw.animate({ y2: newHeight, y1: minHeight }, 1000, mina.linear);

  if(enableHighLow) {
    highText.attr({
      text: max + "\u00B0"
    });
    var newHeight;
    var tMax = (max > maxTemp) ? maxTemp : max;
    newHeight = scaleToRange(tMax);
    highText.animate({ y: newHeight }, 1000, mina.linear);

    lowText.attr({
      text: min + "\u00B0"
    });
    var newHeight;
    var tMin = (min > minTemp) ? minTemp : min;
    newHeight = scaleToRange(tMin);
    lowText.animate({ y: newHeight }, 1000, mina.linear);
  }
}

/**********************************************************
*                                                         *
* This function is used internally to convert the         *
* supplied temperature to a value within the height range *
* of the mercury.                                         *
*                                                         *
*   Arguments:                                            *
*                                                         *
*   (int)  x          : The temperature to convert.       *
*                                                         *
**********************************************************/
function scaleToRange(x) {
  var t;
  t = minHeight + ((x - minTemp) * (maxHeight - minHeight)) / (maxTemp - minTemp);  // Based on a formula googled from various sources.
  return t;
}

initialiseThermometer("#thermometer", 0, -10, 40, 200, 50, true);