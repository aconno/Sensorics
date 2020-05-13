# Advertisement matching and data extraction
## Advertisement file example
```
{
    "id": "080000",
    "name": "acnSENSA",
    "icon": "ic_sensa",
    "format": [
        {
            "name": "Not-Useful-Data",
            "start_index_inclusive": 3,
            "end_index_exclusive": 4,
            "reversed": false,
            "data_type": "BYTE"
        }
    ],
    "format_required": [
        {
            "name": "Nordic ID 1",
            "index": 0,
            "value": "0x59"
            "source": "0xFF"
        },
        {
            "name": "Nordic ID 2",
            "index": 1,
            "value": "0x00"
        },
        {
            "name": "Aconno ID 1",
            "index": 2,
            "value": "0x69"
        },
        {
            "name": "Product ID 2",
            "index": 3,
            "value": "0x08"
        },
        {
            "name": "Version Number",
            "index": 4,
            "value": "0x01"
        }
    ],
    "settings_support": {
        "mask": "0x80",
        "index": 4
    }
}
```

### Root elements
#### id
Id is an internal reference that is used to ID the format file, it needs to be unique.
#### name
Name is the name of the device that emits an advertisement that matches this deserializer.
#### icon
Icon is the name of the asset that is going to be displayed in the application for the device that emits and advertisement that matches this deserializer.
#### format
The format element is an array of sub-elements that specify the data to be extracted from the advertisement.
After the data is extracted from the advertisement it will be stored into a map.
These elements have the following attributes:
##### format: name
The name attribute specifies the key under which the deserialized data will be stored.
##### format: start_index_inclusive
Start_index_inclusive specifies the starting index of the data (represented by an array of bytes) to be extracted in the contents of the **manufacturer specific data** (more on that further) portion of the advertisement from which we are extracting the data, this index is inclusive.
##### format: end_index_exclusive
End_index_exclusive specifies the ending index of the data (represented by an array of bytes) to be extracted in the contents of the **manufacturer specific data** (more on that further) portion of the advertisement from which we are extracting the data, this index is exclusive.
##### format: reversed
Reversed specified if the byte order is reversed. If reversed is true that means that the byte-order is Little Endian.
##### format: data_type
The data_type property specifies the deserializer to be used to extract the data from the now isolated byte array.
The following deserializers are available:

| Value | C Equivalent| Internal Kotlin Type|
|:---|:---|:---|
| BYTE | int8 | Byte |
| UNSIGNED_BYTE | uint8 | Short |
| SHORT | int16 | Short |
| UNSIGNED_SHORT | uint16 | Int |
| INT | int32 | Int |
| UNSIGNED_INT | uint32 | Long |
| LONG | int64 | Long |
| FLOAT | float | Float |

#### format_required
The format_required element is an array of sub-elements that are basically a filter.
Each one of these elements specified a simple check on a certain index of the advertisement for a certain value (more on that later).
If the advertisement is validated only then can data extraction through [format](#format) start.

##### format_required: name
The name element is used for internal debugging and specified what the byte that is being checked means.
##### format_required: index
The index element specifies the index of the byte in the portion of the advertisement data specified by the [source](#format-required-source) value to which our [value](#format-required-value) is being compared to
##### format_required: value
The value element specified what the byte at a given [index](#format-required-index) in the portion of the advertisement data specified by the [source](#format-required-source) value should be. This is represented by a hex value.
##### format_required: source
The source element is an **optional** element, which defaults to 0xFF (i.e. the manufacturers data portion of the advertisement) that specifies which part of the advertisement data we are looking at when comparing our [value](#format-required-value) at a given [index](#format-required-index).

#### settings_support
Setting support is a special **optional** element. It specifies where the bit that specifies the device that emits and advertisement that matches this deserializer is settings compatible. It contains 2 sub-elements.
##### settings_support: mask
Mask is bitmask that we apply to a byte at the specified [index](#settings-support-index) in the manufacturers data (0xFF) portion of the advertisement. If after performing an AND operation between the mask and the byte the resulting value yields the value of the mask it means that this device is settings compatible.
##### settings_support: index
The index element specified the index of the byte in the manufacturers data (0xFF) portion of the advertisement data where the [mask](#settings-support-mask) should be applied.

## Example process of deserializing the data

### Example advertisement data

|  Type   |  Data  |
|:---|:---|
|  0xFF (manufacturer specific data type)  |  0xFF 0x00 0x69 0x08 0x81  |

### Step 1: Filtering

Before we can deserialize the data we need to make sure that this advertisement conforms to one of our formats (specified by [format files](#advertisement-file-example)).

#### Step 1.1: Check advertisement length
Check that the advertisement length is at least the length of the format_required array. This eliminates needless checking of advertisements that are too short (this will probably be changed to checking that the length is at least the highest [index](#format-required-index) value + 1).

For the given example data this check passes since 5 (advertisement data length) >= 5 (format_required size)

#### Step 1.2: Strip settings support mask
We need to strip the settings support mask so that we can properly filter later.

The byte at index `4` of type `0xFF` is taken `(0x81)` and an `AND` operation is performed between it and an inverse of our [settings support mask](#settings-support-mask) `0x80` to strip the mask.  
`0x81 & ~0x80 -> 0x81 & 0x7F -> 0x01`.

#### Step 1.3: Compare all format_required values

##### Step 1.3.1: Group all format_required values by source
This step is optional but it just makes for cleaner code and less map accesses in the code.

##### Step 1.3.2: Check each format_required value
###### Iteration 1
The current format_required value is:

```
{
    "name": "Nordic ID 1",
    "index": 0,
    "value": "0x59"
    "source": "0xFF"
}
```

We take the value at index 0 (specified by the [index](#format-required-index) of the [example](#example-advertisement-data) advertisement at the type 0xFF (specified by the [source](#format-required-source)) and compare it to 0x59 (specified by the [value](#format-required-value)]). This can be illustrated by the following code:

```
advertisementDataMap[source][index] == value -> 0x59 == 0x59 -> true
```

If the yielded value is true we continue the checks.

###### Iteration 2
The current format_required value is:

```
{
    "name": "Nordic ID 2",
    "index": 1,
    "value": "0x00"
}
```

We take the value at index 1 (specified by the [index](#format-required-index) of the [example](#example-advertisement-data) advertisement at the type 0xFF (usually specified by the [source](#format-required-source), but it defaults to 0xFF which is the manufacturer specific data type) and compare it to 0x00 (specified by the [value](#format-required-value)]). This can be illustrated by the following code:

```
advertisementDataMap[source][index] == value -> 0x00 == 0x00 -> true
```

If the yielded value is true we continue the checks.

###### Iteration 3
The current format_required value is:

```
{
    "name": "Aconno ID 1",
    "index": 2,
    "value": "0x69"
}
```

We take the value at index 3 (specified by the [index](#format-required-index) of the [example](#example-advertisement-data) advertisement at the type 0xFF (usually specified by the [source](#format-required-source), but it defaults to 0xFF which is the manufacturer specific data type) and compare it to 0x69 (specified by the [value](#format-required-value)]). This can be illustrated by the following code:

```
advertisementDataMap[source][index] == value -> 0x69 == 0x69 -> true
```

If the yielded value is true we continue the checks.

###### Iteration 4
The current format_required value is:

```
{
    "name": "Product ID 2",
    "index": 3,
    "value": "0x08"
}
```

We take the value at index 3 (specified by the [index](#format-required-index) of the [example](#example-advertisement-data) advertisement at the type 0xFF (usually specified by the [source](#format-required-source), but it defaults to 0xFF which is the manufacturer specific data type) and compare it to 0x08 (specified by the [value](#format-required-value)]). This can be illustrated by the following code:

```
advertisementDataMap[source][index] == value -> 0x08 == 0x08 -> true
```

If the yielded value is true we continue the checks.

###### Iteration 5
The current format_required value is:

```
{
    "name": "Version Number",
    "index": 4,
    "value": "0x01"
}
```

We take the value at index 4 (specified by the [index](#format-required-index) of the [example](#example-advertisement-data) advertisement at the type 0xFF (usually specified by the [source](#format-required-source), but it defaults to 0xFF which is the manufacturer specific data type) and compare it to 0x01 (specified by the [value](#format-required-value)]). Even though this value is originally `0x81` we need to remember that we stripped it of the mask [before](#step-12-strip-settings-support-mask) and that now it is `0x01`. This can be illustrated by the following code:

```
advertisementDataMap[source][index] == value -> 0x01 == 0x01 -> true
```

Since this is the last iteration and it returns true that means that we can start to extract data.

#### Step 2: Extraction of data specified by each format element

#### Iteration 1
The current format element is:
```
{
    "name": "Not-Useful-Data",
    "start_index_inclusive": 3,
    "end_index_exclusive": 4,
    "reversed": false,
    "data_type": "BYTE"
}
```
##### Step 1: Extract data
Extract byte array from the 0xFF portion of the [advertisement](#example-advertisement-data) between the indices 3 (specified by [start_index_inclusive](#format-start-index-inclusive)) and 4 (specified by [end_index_exclusive](#format-end-index-exclusive)). Let's show that example on the following array:

| Index 0 | Index 1 | Index 2 | Index 3 | Index 4 |
|---|---|---|---|---|
| 0xFF | 0x00 | 0x69 | 0x08 | 0x81 |

The extracted data would be `0x08` since the first index is inclusive and the second is exclusive.

##### Step 2: Reverse array if needed
We need to reverse the array if the [reversed](#format-reversed) value is true. Since this is a 1 byte array this wouldn't change anything but it is `false` so we do not need to reverse it anyways. This is done because sometimes the data is advertised in the **Little Endian** byteorder and sometimes it is advertised in the **Big Endian** byteorder.

##### Step 3: Determine which deserializer we need to use
It is pretty obvious from the [data_type](#format-data-type) value that we need to treat this as a byte. This means that we transform it into a Byte primitive internally which is the equivalent of int8 in the C programming language. This value now becomes the number `8`.

##### Step 4: Store value into map
We need to save the value somewhere so we store it into the data map for this advertisement under a key specified by the [name](#format-name) value. This can be illustrated by the following code:

```
dataToReturn[name] = deserialized_value -> dataToReturn["Not-Useful-Data"] = 8
```

##### Step 5: Use the data
The data is now ready to be used.