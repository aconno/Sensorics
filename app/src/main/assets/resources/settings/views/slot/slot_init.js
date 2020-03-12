//GLOBAL Slot object
//let beacon;
let customContnetnTimeoutId;
let urlContentTiemoutId;
let ibeaconUuidTimeoutId;
const TIMEOUT_INTERVAL = 1000;
let slotInitialized = new Map();
const INTERVAL_MS = [50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000,
    11000, 12000, 13000, 14000, 15000, 15000, 20000, 30000, 40000, 50000, 60000, 120000, 180000, 240000, 300000, 360000, 420000, 480000,
    540000, 600000, 660000, 720000, 780000, 840000, 900000, 900000, 1200000, 1800000, 2400000, 3000000, 3600000, 7200000, 10800000,
    14400000, 18000000, 21600000, 25200000, 28800000, 32400000, 36000000, 39600000, 43200000, 46800000, 50400000, 54000000, 57600000,
    61200000, 64800000, 68400000, 72000000, 75600000, 79200000, 82800000, 86400000
];

function onSlotDocumentReady(slotIndex) {
    if (slotInitialized.get(slotIndex)) {
        return;
    }

    let emptyMenuItem = generateFrameTypeMenuItem("EMPTY",slotIndex);
    //let uidMenuItem = generateFrameTypeMenuItem("UID",slotIndex); //Not supported yet
    let urlMenuItem = generateFrameTypeMenuItem("URL",slotIndex);
    let customMenuItem = generateFrameTypeMenuItem("CUSTOM",slotIndex);
    let ibeaconMenuItem = generateFrameTypeMenuItem("IBEACON",slotIndex);

    $('#dropdown-frame-type-'+slotIndex).append(
        /*+ uidMenuItem*/
        emptyMenuItem + urlMenuItem + customMenuItem + ibeaconMenuItem
    );

    $(".dropdown-menu").on('click', 'li a', function() {
        $(this).parent().parent().siblings(".btn:first-child").html($(this).text() + ' <span class="caret"></span>');
        $(this).parent().parent().siblings(".btn:first-child").val($(this).text());
    });
};

function defaultTypeClicked(slotIndex) {
    $("#advertising-content-"+slotIndex).empty();

    let whatever = "";

    if (beacon.slots.slots[slotIndex] != null && beacon.slots.slots[slotIndex].type == FrameType.DEFAULT) {
        whatever = generateDefaultContent(beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_DEFAULT_DATA],slotIndex);

    } else {
        whatever = generateDefaultContent("",slotIndex);
    }

    $("#advertising-content-"+slotIndex).append(whatever);

    $("#default-advertising-content-"+slotIndex).bind("change keyup", function() {
        clearTimeout(customContnetnTimeoutId);
        customContnetnTimeoutId = setTimeout(function() { getUpdatedSlot(slotIndex) }, TIMEOUT_INTERVAL);
    });
}

function emptyTypeClicked(slotIndex) {
    $("#advertising-content-"+slotIndex).empty();
}

function urlTypeClicked(slotIndex) {
    $("#advertising-content-"+slotIndex).empty();

    let whatever = "";
    if (beacon.slots.slots[slotIndex] != null && beacon.slots.slots[slotIndex].type == FrameType.URL) {
        whatever = generateURLContent(beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_URL_URL].trim(),slotIndex);
    } else {
        whatever = generateURLContent("",slotIndex);
    }

    $("#advertising-content-"+slotIndex).append(whatever);

    $("#url-"+slotIndex).bind("change keyup", function() {
        clearTimeout(urlContentTiemoutId);
        urlContentTiemoutId = setTimeout(function() { getUpdatedSlot(slotIndex) }, TIMEOUT_INTERVAL);
    });
}

function uidTypeClicked(slotIndex) {
    $("#advertising-content-"+slotIndex).empty();

    let whatever = "";
    if (beacon.slots.slots[slotIndex] != null && beacon.slots.slots[slotIndex].type == FrameType.UID) {
        whatever = generateUIDContent(
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID],
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID],
            slotIndex
        );
    } else {
        whatever = generateUIDContent("", "",slotIndex);
    }

    $("#advertising-content-"+slotIndex).append(whatever);

    $("#uid-namespace-id-"+slotIndex).bind("change keyup", function() {
        getUpdatedSlot(slotIndex);
    });

    $("#uid-instance-id-"+slotIndex).bind("change keyup", function() {
        getUpdatedSlot(slotIndex);
    });
}

function ibeaconTypeClicked(slotIndex) {
    $("#advertising-content-"+slotIndex).empty();

    let whatever = "";
    if (beacon.slots.slots[slotIndex] != null && beacon.slots.slots[slotIndex].type == FrameType.IBEACON) {
        whatever = generateIBeaconContent(
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_UUID],
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MAJOR],
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MINOR],
            slotIndex
        );
    } else {
        whatever = generateIBeaconContent("", "", "",slotIndex);
    }

    $("#advertising-content-"+slotIndex).append(whatever);

    $("#ibeacon-uuid-"+slotIndex).bind("change keyup", function() {
        clearTimeout(ibeaconUuidTimeoutId);
        ibeaconUuidTimeoutId = setTimeout(function() { getUpdatedSlot(slotIndex) }, TIMEOUT_INTERVAL);
    });

    $("#ibeacon-major-"+slotIndex).bind("change keyup", function() {
        getUpdatedSlot(slotIndex);
    });

    $("#ibeacon-minor-"+slotIndex).bind("change keyup", function() {
        getUpdatedSlot(slotIndex);
    });
}

function customTypeClicked(slotIndex) {
    $("#advertising-content-"+slotIndex).empty();

    let whatever = "";
    if (beacon.slots.slots[slotIndex] != null && beacon.slots.slots[slotIndex].type == FrameType.CUSTOM) {
        whatever = generateCustomContent(
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM],
            false,
            slotIndex
        );
    } else {
        whatever = generateCustomContent("", "",slotIndex);
    }

    $("#advertising-content-"+slotIndex).append(whatever);


    $("#custom-value-"+slotIndex).prop('disabled', beacon.slots.slots[slotIndex].readOnly)


    $("#custom-value-"+slotIndex).bind("change keyup", function() {
        clearTimeout(customContnetnTimeoutId);
        customContnetnTimeoutId = setTimeout(function() { getUpdatedSlot(slotIndex) }, TIMEOUT_INTERVAL);
    });
}

function initListeners(slotIndex) {
    if (slotInitialized.get(slotIndex)) {
        return;
    }

    //Updates text of dropdown header when selected.
    $(".dropdown-menu").on('click', 'li a', function() {
        getUpdatedSlot(slotIndex);
    });

    $('#slot-name-text-'+slotIndex).on('change keyup paste', function() {
        $('#slot-name-text-'+slotIndex).val($(this).val());
        getUpdatedSlot(slotIndex);
    });

    $('#eventable-params-value-'+slotIndex).on('keyup paste', function() {
        getUpdatedSlot(slotIndex);
    });

    $('#eventable-params-value-'+slotIndex).on('keypress', isNumber);

    $(document).on("change", "#toggle-advertise-switch-"+slotIndex, function() {
        getUpdatedSlot(slotIndex);

    });

    $(document).on("change", "#toggle-internal-switch-"+slotIndex, function() {
        let mode = $('#toggle-internal-switch-'+slotIndex).prop("checked") ? "EVENT" : "INTERVAL";
        if (mode == 'INTERVAL') {
            $('#eventable-params-body-'+slotIndex).hide();
            $('#advertising-interval-body-'+slotIndex).show();
        } else {
            $('#eventable-params-body-'+slotIndex).show();
            $('#advertising-interval-body-'+slotIndex).hide();
        }
        getUpdatedSlot(slotIndex);
    });

    $(document).on("input", "#range-packetCount-"+slotIndex, function() {
        // Real value is range value+1, because range starting position (0) is 1
        let realValue = parseInt($(this).val()) + 1
        $('label[for="range-packetCount-'+slotIndex+'"]').html(realValue);
        getUpdatedSlot(slotIndex);
    });

    $(document).on("input", "#advertising-interval-time-range-"+slotIndex, function() {
        let index = $(this).val();
        console.log('range index is ' + index)
        $('#advertising-interval-time-'+slotIndex).text(timeToHighestOrder(INTERVAL_MS[index]));
        getUpdatedSlot(slotIndex);
    });

    //Dropdown items
    $('#frame-type-default-'+slotIndex).click(function() {
        defaultTypeClicked(slotIndex);
    });

    $('#frame-type-empty-'+slotIndex).click(function() {
            emptyTypeClicked(slotIndex);
        });
    $('#frame-type-url-'+slotIndex).click(function() {
            urlTypeClicked(slotIndex);
        });

    $('#frame-type-uid-'+slotIndex).click(function() {
           uidTypeClicked(slotIndex);
       });

    $('#frame-type-ibeacon-'+slotIndex).click(function() {
           ibeaconTypeClicked(slotIndex);
       });

    $('#frame-type-custom-'+slotIndex).click(function() {
          customTypeClicked(slotIndex);
      });

    $('#ibeacon-uuid-'+slotIndex).on('input',function() {
        let uuid = $(this).val();
        if(!isValidUUID(uuid)) {
            $(this).css('background-color', '#ffe6e6');
        } else {
            $(this).css('background-color', '');
        }
    });

    slotInitialized.set(slotIndex,true);
}

// Callback which will be invoked when one of the enum is choosen (now considering only enum with tx power data)
function dropDownChanged(id, element, position, index,slotIndex) {
    // For some reason can't do it with jQuery
    document.getElementById("ddl-menu-button-" + id + '-' + slotIndex).innerHTML = element;
    getUpdatedSlot(slotIndex)
}

//Function that converts ascii to hex
function ascii_to_hexa(str) {
    var arr1 = [];
    for (var n = 0, l = str.length; n < l; n++) {
        var hex = Number(str.charCodeAt(n)).toString(16);
        arr1.push(hex);
    }
    return arr1.join('');
}

//Function that converts hex to ascii
function hex_to_ascii(str1) {
    var hex = str1.toString();
    var str = '';
    for (var n = 0; n < hex.length; n += 2) {
        str += String.fromCharCode(parseInt(hex.substr(n, 2), 16));
    }
    return str;
}

function initSlot(slotJson, slotIndex) {
    beacon = JSON.parse(slotJson.replace(/\\u0000/g, ''));

    switch (beacon.slots.slots[slotIndex].type) {
        case FrameType.UID:
            uidTypeClicked(slotIndex);
            $('#btn-frame-type-'+slotIndex).html(FrameType.UID);
            break;
        case FrameType.URL:
            urlTypeClicked(slotIndex);
            $('#btn-frame-type-'+slotIndex).html(FrameType.URL);
            break;
        case FrameType.IBEACON:
            ibeaconTypeClicked(slotIndex);
            $('#btn-frame-type-'+slotIndex).html(FrameType.IBEACON);
            break;
        case FrameType.CUSTOM:
            customTypeClicked(slotIndex);
            $('#btn-frame-type-'+slotIndex).html(FrameType.CUSTOM);
            break;
        case FrameType.DEFAULT:
            defaultTypeClicked(slotIndex);
            $('#btn-frame-type-'+slotIndex).html(FrameType.DEFAULT);
            let defaultMenuItem = generateFrameTypeMenuItem("DEFAULT",slotIndex);
            $('#dropdown-frame-type-'+slotIndex).prepend(defaultMenuItem);
            break;
        default:
            emptyTypeClicked(slotIndex);
            $('#btn-frame-type-'+slotIndex).html(FrameType.EMPTY);
    }

    $('#slot-name-text-'+slotIndex).val(beacon.slots.slots[slotIndex].name);

    $('#slot-name-text-'+slotIndex).prop('disabled', beacon.slots.slots[slotIndex].readOnly)


    let params = generateSwitchContent(beacon.slots.slots[slotIndex].readOnly, beacon.slots.slots[slotIndex].active, "Slot Advertising", "advertise-switch",slotIndex);
    $('#slot-advertising-'+slotIndex).empty();
    $('#slot-advertising-'+slotIndex).append(params);

    $('#base-parameter-'+slotIndex).empty();

    params = generateBaseParameter();
    $('#base-parameter-'+slotIndex).append(params);

    params = generateSwitchContent(beacon.slots.slots[slotIndex].readOnly, beacon.slots.slots[slotIndex].advertisingMode == "EVENT", "Interval/Event", "internal-switch",slotIndex);
    $('#base-parameter-'+slotIndex).append(params);

    insertEventableParams(slotIndex)

    insertAdvIntervalParams(slotIndex)

    if (beacon.slots.slots[slotIndex].advertisingMode == 'INTERVAL') {
        $('#eventable-params-body-'+slotIndex).hide();
    } else {
        $('#advertising-interval-body-'+slotIndex).hide();
    }

    params = generatePacketCountRange("Advertising PacketCount", beacon.slots.slots[slotIndex].packetCount, 'packetCount', 9,slotIndex);
    $('#base-parameter-'+slotIndex).append(params);

    params = generateEnums(beacon.parameters.parameters["Basic config"].find(e => e.name === "Supported TX powers").value.split(','), "tx-power", "Advertising Tx Power", beacon.slots.slots[slotIndex].txPower, 0,slotIndex)
    $('#base-parameter-'+slotIndex).append(params);

    $('#btn-frame-type-'+slotIndex).prop('disabled', beacon.slots.slots[slotIndex].readOnly)

    initListeners(slotIndex);
}

function insertAdvIntervalParams(slotIndex) {
    let advInterval = beacon.slots.slots[slotIndex].advertisingModeParameters.interval
    if (!advInterval) advInterval = 250;
    params = generateAdvertisingIntervalInfo(advInterval,slotIndex);
    $('#base-parameter-'+slotIndex).append('<div id="advertising-interval-body-'+slotIndex+'"></div>');
    $('#advertising-interval-body-'+slotIndex);
    let rangePosition = getClosestElementPosition(INTERVAL_MS, advInterval) + 1;
    let intervalTextRepresentation = timeToHighestOrder(advInterval);
    params = generateAdvertisingIntervalRange("Advertising Interval", intervalTextRepresentation, rangePosition, INTERVAL_MS.length - 1,slotIndex)
    $('#advertising-interval-body-'+slotIndex).append(params);
}

function insertEventableParams(slotIndex) {
    let paramId = beacon.slots.slots[slotIndex].advertisingModeParameters.parameterId;
    let eventableParams = beacon.eventableParams.params;
    let eventableParam
    eventableParams.find(function(element) {
        if (element.id == paramId) {
            eventableParam = element.id + '-' + element.name;
            return;
        }
    })

    if (!eventableParam) {
        eventableParam = eventableParams[0].id + '-' + eventableParams[0].name;
    }

    if (!eventableParam) {
        // we don't have any eventable parameters available.
        return;
    }

    let sign = beacon.slots.slots[slotIndex].advertisingModeParameters.sign;
    if (!sign) {
        sign = beacon.eventableParams.signs[0]
    }
    if (!sign) {
        // we don't have any eventable sings available.
        return;
    }

    params = generateEventableParams(beacon.eventableParams.params.flatMap(element => element.id + '-' + element.name), eventableParam, beacon.eventableParams.signs, sign, "Value",slotIndex);


    $('#base-parameter-'+slotIndex).append(params);

    if (beacon.slots.slots[slotIndex].readOnly) {
        $('#ddl-menu-button-eventable-params-'+slotIndex).prop('disabled', true);
        $('#ddl-menu-button-eventable-params-sings-'+slotIndex).prop('disabled', true);
        $('#eventable-params-value-'+slotIndex).prop('disabled', true);
    }
    let value = beacon.slots.slots[slotIndex].advertisingModeParameters.thresholdInt;
    if (!value) {
        value = 0;
    }
    $('#eventable-params-value-'+slotIndex).val(value);
}

function getUpdatedSlot(slotIndex) {

    beacon.slots.slots[slotIndex].name = $('#slot-name-text-'+slotIndex).val();
    let type = $('#btn-frame-type-'+slotIndex).text().trim();

    beacon.slots.slots[slotIndex].active = $('#toggle-advertise-switch-'+slotIndex).prop("checked");

    let advMode = $('#toggle-internal-switch-'+slotIndex).prop("checked") ? "EVENT" : "INTERVAL";

    beacon.slots.slots[slotIndex].advertisingMode = advMode;
    if (advMode == "EVENT") {
        let parameter = $('#ddl-menu-button-eventable-params-'+slotIndex).html();
        let parameterId = parseInt(parameter.split('-')[0]);
        let sign = $('#ddl-menu-button-eventable-params-sings-'+slotIndex).html();
        let intValue = parseInt($('#eventable-params-value-'+slotIndex).val());
        if (!intValue) {
            intValue = 0;
        }
        beacon.slots.slots[slotIndex].advertisingModeParameters.parameterId = parameterId;
        beacon.slots.slots[slotIndex].advertisingModeParameters.sign = sign;
        beacon.slots.slots[slotIndex].advertisingModeParameters.thresholdInt = intValue;
    } else {
        let rangePosition = $('#advertising-interval-time-range-'+slotIndex).val();
        let adInterval = INTERVAL_MS[rangePosition];
        beacon.slots.slots[slotIndex].advertisingModeParameters.interval = adInterval
    }

    let packetValue = parseInt($('#range-packetCount-'+slotIndex).val()) + 1
    beacon.slots.slots[slotIndex].packetCount = packetValue;

    let txPower;
    if ($('#ddl-menu-button-tx-power-'+slotIndex).text()) {
        txPower = parseInt($('#ddl-menu-button-tx-power-'+slotIndex).text());
    } else {
        txPower = beacon.slots.slots[slotIndex].txPower;
    }
    beacon.slots.slots[slotIndex].txPower = txPower;
    beacon.slots.slots[slotIndex].readOnly = $('#range-internal-switch-'+slotIndex).val();

    switch (type) {
        case "UID":
            beacon.slots.slots[slotIndex].type = FrameType.UID;
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID] = $('#uid-namespace-id-'+slotIndex).val();
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID] = $('#uid-instance-id-'+slotIndex).val();
            break;
        case "URL":
            beacon.slots.slots[slotIndex].type = FrameType.URL;
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_URL_URL] = $('#url-'+slotIndex).val();
            break;
        case "IBEACON":
            beacon.slots.slots[slotIndex].type = FrameType.IBEACON;
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_UUID] = $('#ibeacon-uuid-'+slotIndex).val();
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MAJOR] = $('#ibeacon-major-'+slotIndex).val();
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MINOR] = $('#ibeacon-minor-'+slotIndex).val();
            break;
        case "CUSTOM":
            beacon.slots.slots[slotIndex].type = FrameType.CUSTOM;
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM] = $('#custom-value-'+slotIndex).val()
            break;

        case "DEFAULT":
            beacon.slots.slots[slotIndex].type = FrameType.DEFAULT;
            beacon.slots.slots[slotIndex]["advertisingContent"][KEY_ADVERTISING_CONTENT_DEFAULT_DATA] = $('#default-advertising-content-'+slotIndex).val();
            break;

        default:
            beacon.slots.slots[slotIndex].type = FrameType.EMPTY;
            beacon.slots.slots[slotIndex]["advertisingContent"] = {};
    }

    native.onDataChanged(JSON.stringify(beacon));
}

function isValidUUID(uuid) {
    validatedUuid = uuid.match('^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$');
    if (validatedUuid === null) {
      return false;
    }
    return true;
}