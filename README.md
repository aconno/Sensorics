# Sensorics ![](https://github.com/aconno/Sensorics/workflows/Android%20CI/badge.svg)

Android app for displaying sensor readings from the ACNSENSA aconno board. Sensor readings are
gotten from the board using only BLE advertisements. For this purpose two advertisements are used:

- Advertisement 0: This advertisement contains all the data for 3D values (gyro, accelerometer,
and magnetometer). Additionally, this advertisement also contains the scaling factor for the
accelerometer.

- Advertisement 1: This advertisement contains all the data for 1D values (temperature, humidity,
pressure, light, battery level).

 The detailed format of the Advertisement 0 is as follows:

| Byte | Example | Description                                                                          |
|------|---------|--------------------------------------------------------------------------------------|
| 0    | 0x02    | Length of EIR packet ( Type + Data ) in bytes. Example: 2 bytes                      |
| 1    | 0x01    | Type                                                                                 |
| 2    | 0x04    | Type Value                                                                           |
| 3    | 0x1A    | Length of EIR packet ( Type + Data ) in bytes. Example: 0x1A equals 26 bytes.        |
| 4    | 0xFF    | Type                                                                                 |
| 5    | 0x59    | Byte 1 of Vendor ID. Example: Nordic ID is 0x59 0x00                                 |
| 6    | 0x00    | Byte 2 of Vendor ID. Example: Nordic ID is 0x59 0x00                                 |
| 7    | 0x17    | Byte 1 of App ID. Example: App ID is 0x17 0xCF                                       |
| 8    | 0xCF    | Byte 2 of App ID. Example: App ID is 0x17 0xCF                                       |
| 9    | 0x00    | Advertisement Type. Example: Advertisement type 0.                                   |
| 10   | 0x2C    | Byte 2 of Gyroscope X value. Example: 0x06 0x2c = (short) 1580                       |
| 11   | 0x06    | Byte 1 of Gyroscope X value. Example: 0x06 0x2c = (short) 1580                       |
| 12   | 0xA8    | Byte 2 of Gyroscope Y value. Example: 0x0E 0xA8 = (short) 3752                       |
| 13   | 0x0E    | Byte 1 of Gyroscope Y value. Example: 0x0E 0xA8 = (short) 3752                       |
| 14   | 0x4D    | Byte 2 of Gyroscope Z value. Example: 0x08 0x4D = (short) 2125                       |
| 15   | 0x08    | Byte 1 of Gyroscope Z value. Example: 0x08 0x4D = (short) 2125                       |
| 16   | 0x4A    | Byte 2 of Accelerometer X value. Example: 0xFD 0x4A = (short) -694                   |
| 17   | 0xFD    | Byte 1 of Accelerometer X value. Example: 0xFD 0x4A = (short) -694                   |
| 18   | 0x61    | Byte 2 of Accelerometer Y value. Example: 0x06 0x61 = (short) 1633                   |
| 19   | 0x06    | Byte 1 of Accelerometer Y value. Example: 0x06 0x61 = (short) 1633                   |
| 20   | 0x15    | Byte 2 of Accelerometer Z value. Example: 0xDB 0x15 = (short) -9451                  |
| 21   | 0xDB    | Byte 1 of Accelerometer Z value. Example: 0xDB 0x15 = (short) -9451                  |
| 22   | 0x92    | Byte 2 of Magnetometer X value. Example: 0x0D 0x92 = (short) 3474                    |
| 23   | 0x0D    | Byte 1 of Magnetometer X value. Example: 0x0D 0x92 = (short) 3474                    |
| 24   | 0x50    | Byte 2 of Magnetometer Y value. Example: 0xFE 0x50 = (short) -432                    |
| 25   | 0xFE    | Byte 1 of Magnetometer Y value. Example: 0xFE 0x50 = (short) -432                    |
| 26   | 0x33    | Byte 2 of Magnetometer Z value. Example: 0x09 0x33 = (short) 2355                    |
| 27   | 0x09    | Byte 1 of Magnetometer Z value. Example: 0x09 0x33 = (short) 2355                    |
| 28   | 0x9E    | Byte 2 of Accelerometer scaling factor. Example: 0x0F 0x9E = (unsigned short) 3998   |
| 29   | 0x0F    | Byte 1 of Accelerometer scaling factor. Example: 0x0F 0x9E = (unsigned short) 3998   |

Furthermore, the short values gotten for the Gyroscope, Accelerometer, and Magnetometer need to be
scaled using the following factors.

| Type          | Scale Factor          | Example                               |
|---------------|-----------------------|---------------------------------------|
| Gyroscope     |  245.0f / 32768.0f    | 1580 * 245.0f /32768.0f = 11.8133545  |
| Accelerometer |  scaleFactor / 65536  | -9451 * (3998 / 65536f) = -576.5548   |
| Magnetometer  |  0.00014f             | 3474 * 0.00014 = 0.48636              |

For the Advertisement 1, the format is as follows:

| Byte | Example | Description                                                                  |
|------|---------|------------------------------------------------------------------------------|
| 0    | 0x02    | Length of EIR packet ( Type + Data ) in bytes. Example: 2 bytes              |
| 1    | 0x01    | Type                                                                         |
| 2    | 0x04    | Type Value                                                                   |
| 3    | 0x1A    | Length of EIR packet ( Type + Data ) in bytes. Example: 0x1A equals 26 bytes.|
| 4    | 0xFF    | Type                                                                         |
| 5    | 0x59    | Byte 1 of Vendor ID. Example: Nordic ID is 0x59 0x00                         |
| 6    | 0x00    | Byte 2 of Vendor ID. Example: Nordic ID is 0x59 0x00                         |
| 7    | 0x17    | Byte 1 of App ID. Example: App ID is 0x17 0xCF                               |
| 8    | 0xCF    | Byte 2 of App ID. Example: App ID is 0x17 0xCF                               |
| 9    | 0x01    | Advertisement type. Example: Advertisement type 1.                           |
| 10   | 0xB9    | Byte 4 of temperature. Example: 0x41 0xE3 0x6D 0xB9 = (float) 28.428576      |
| 11   | 0x6D    | Byte 3 of temperature. Example: 0x41 0xE3 0x6D 0xB9 = (float) 28.428576      |
| 12   | 0xE3    | Byte 2 of temperature. Example: 0x41 0xE3 0x6D 0xB9 = (float) 28.428576      |
| 13   | 0x41    | Byte 1 of temperature. Example: 0x41 0xE3 0x6D 0xB9 = (float) 28.428576      |
| 14   | 0x28    | Byte 4 of humidity. Example: 0x42 0x45 0x54 0x28 = (float) 49.332184         |
| 15   | 0x54    | Byte 3 of humidity. Example: 0x42 0x45 0x54 0x28 = (float) 49.332184         |
| 16   | 0x45    | Byte 2 of humidity. Example: 0x42 0x45 0x54 0x28 = (float) 49.332184         |
| 17   | 0x42    | Byte 1 of humidity. Example: 0x42 0x45 0x54 0x28 = (float) 49.332184         |
| 18   | 0xB2    | Byte 4 of pressure. Example: 0x44 0x80 0x4E 0xB2 = (float) 1026.4592         |
| 19   | 0x4E    | Byte 3 of pressure. Example: 0x44 0x80 0x4E 0xB2 = (float) 1026.4592         |
| 20   | 0x80    | Byte 2 of pressure. Example: 0x44 0x80 0x4E 0xB2 = (float) 1026.4592         |
| 21   | 0x44    | Byte 1 of pressure. Example: 0x44 0x80 0x4E 0xB2 = (float) 1026.4592         |
| 22   | 0x82    | Byte 4 of light. Example: 0x40 0x48 0x0C 0x82 = (float) 3.1257634            |
| 23   | 0x0C    | Byte 3 of light. Example: 0x40 0x48 0x0C 0x82 = (float) 3.1257634            |
| 24   | 0x48    | Byte 2 of light. Example: 0x40 0x48 0x0C 0x82 = (float) 3.1257634            |
| 25   | 0x40    | Byte 1 of light. Example: 0x40 0x48 0x0C 0x82 = (float) 3.1257634            |
| 26   | 0x4B    | Battery level percentage. Example:  0x4B = (byte) 75 decimal                 |
| 27   | 0x09    | Unused. This value is not guaranteed to be 0x00.                             |
| 28   | 0x00    | Unused. This value is not guaranteed to be 0x00.                             |
| 29   | 0x00    | Unused. This value is not guaranteed to be 0x00.                             |

