## Usage

Please do not use General Values with Specific Values in the same request. It is not valid.

Beacon advertisements can not be combined in one request.
For example you have 2 beacons one provides Temperature and Light and the other one provides Battery Level. If you put your data string as

      {$temperature,$light,$battery_level}

When device gets advertisement from Battery Level = 100, it will send as

	   {$temperature,$light,100}

. Ex: Temperature = 35, Light =18 send result is

       {35,18,\$battery_level}

