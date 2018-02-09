# ACNSENSA Demo App

Android app for displaying sensor readings from the ACNSENSA aconno board. Sensor readings are
gotten from the board using only BLE advertisements. For this purpose two advertisements are used:

- Advertisement 0: This advertisement contains all the data for 3D values (gyro, accelerometer,
and magnetometer).

- Advertisement 1: This advertisement contains all the data for 1D values (temperature, humidity,
pressure, light).

 The detailed format of the Advertisement 0 is as follows:

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
| 9    | 0x00    | Advertisement Type. Example: Advertisement type 0.                           |
| 10   | 0x2C    | Byte 2 of Gyroscope X value. Example: 0x06 0x2c = (short) 1580               |
| 11   | 0x06    | Byte 1 of Gyroscope X value. Example: 0x06 0x2c = (short) 1580               |
| 12   | 0xA8    | Byte 2 of Gyroscope Y value. Example: 0x0E 0xA8 = (short) 3752               |
| 13   | 0x0E    | Byte 1 of Gyroscope Y value. Example: 0x0E 0xA8 = (short) 3752               |
| 14   | 0x4D    | Byte 2 of Gyroscope Z value. Example: 0x08 0x4D = (short) 2125               |
| 15   | 0x08    | Byte 1 of Gyroscope Z value. Example: 0x08 0x4D = (short) 2125               |
| 16   | 0x4A    | Byte 2 of Accelerometer X value. Example: 0xFD 0x4A = (short) -694           |
| 17   | 0xFD    | Byte 1 of Accelerometer X value. Example: 0xFD 0x4A = (short) -694           |
| 18   | 0x61    | Byte 2 of Accelerometer Y value. Example: 0x06 0x61 = (short) 1633           |
| 19   | 0x06    | Byte 1 of Accelerometer Y value. Example: 0x06 0x61 = (short) 1633           |
| 20   | 0xC0    | Byte 2 of Accelerometer Z value. Example: 0xB5 0xC0 = (short) -19008         |
| 21   | 0xB5    | Byte 1 of Accelerometer Z value. Example: 0xB5 0xC0 = (short) -19008         |
| 22   | 0x92    | Byte 2 of Magnetometer X value. Example: 0x0D 0x92 = (short) 3474            |
| 23   | 0x0D    | Byte 1 of Magnetometer X value. Example: 0x0D 0x92 = (short) 3474            |
| 24   | 0x50    | Byte 2 of Magnetometer Y value. Example: 0xFE 0x50 = (short) -432            |
| 25   | 0xFE    | Byte 1 of Magnetometer Y value. Example: 0xFE 0x50 = (short) -432            |
| 26   | 0x33    | Byte 2 of Magnetometer Z value. Example: 0x09 0x33 = (short) 2355            |
| 27   | 0x09    | Byte 1 of Magnetometer Z value. Example: 0x09 0x33 = (short) 2355            |
| 28   | 0x00    | Unused. This value is not guaranteed to be 0x00                              |
| 29   | 0x00    | Unused. This value is not guaranteed to be 0x00                              |




