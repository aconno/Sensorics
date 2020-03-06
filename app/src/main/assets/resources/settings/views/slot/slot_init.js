//GLOBAL Slot object
//let beacon;
let index;
let customContnetnTimeoutId;
let urlContentTiemoutId;
let ibeaconUuidTimeoutId;
const TIMEOUT_INTERVAL = 1000;
let inited = false;
const INTERVAL_MS = [50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000,
    11000, 12000, 13000, 14000, 15000, 15000, 20000, 30000, 40000, 50000, 60000, 120000, 180000, 240000, 300000, 360000, 420000, 480000,
    540000, 600000, 660000, 720000, 780000, 840000, 900000, 900000, 1200000, 1800000, 2400000, 3000000, 3600000, 7200000, 10800000,
    14400000, 18000000, 21600000, 25200000, 28800000, 32400000, 36000000, 39600000, 43200000, 46800000, 50400000, 54000000, 57600000,
    61200000, 64800000, 68400000, 72000000, 75600000, 79200000, 82800000, 86400000
];

function onSlotDocumentReady(slotIndex) {
    if (inited) {
        return;
    }

    let emptyMenuItem = generateFrameTypeMenuItem("EMPTY");
    //let uidMenuItem = generateFrameTypeMenuItem("UID"); //Not supported yet
    let urlMenuItem = generateFrameTypeMenuItem("URL");
    let customMenuItem = generateFrameTypeMenuItem("CUSTOM");
    let ibeaconMenuItem = generateFrameTypeMenuItem("IBEACON");

    $('#dropdown_frame_type-'+slotIndex).append(
        /*+ uidMenuItem*/
        emptyMenuItem + urlMenuItem + customMenuItem + ibeaconMenuItem
    );

    $(".dropdown-menu").on('click', 'li a', function() {
        $(this).parent().parent().siblings(".btn:first-child").html($(this).text() + ' <span class="caret"></span>');
        $(this).parent().parent().siblings(".btn:first-child").val($(this).text());
    });
};

function defaultTypeClicked() {
    $("#advertising_content").empty();

    let whatever = "";

    if (beacon.slots.slots[index] != null && beacon.slots.slots[index].type == FrameType.DEFAULT) {
        whatever = generateDefaultContent(beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_DEFAULT_DATA]);

    } else {
        whatever = generateDefaultContent("");
    }

    $("#advertising_content").append(whatever);

    $("#default_advertising_content").bind("change keyup", function() {
        clearTimeout(customContnetnTimeoutId);
        customContnetnTimeoutId = setTimeout(getUpdatedSlot, TIMEOUT_INTERVAL);
    });
}

function emptyTypeClicked() {
    $("#advertising_content").empty();
}

function urlTypeClicked() {
    $("#advertising_content").empty();

    let whatever = "";
    if (beacon.slots.slots[index] != null && beacon.slots.slots[index].type == FrameType.URL) {
        whatever = generateURLContent(beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_URL_URL].trim());
    } else {
        whatever = generateURLContent("");
    }

    $("#advertising_content").append(whatever);

    $("#url").bind("change keyup", function() {
        clearTimeout(urlContentTiemoutId);
        urlContentTiemoutId = setTimeout(getUpdatedSlot, TIMEOUT_INTERVAL);
    });
}

function uidTypeClicked() {
    $("#advertising_content").empty();

    let whatever = "";
    if (beacon.slots.slots[index] != null && beacon.slots.slots[index].type == FrameType.UID) {
        whatever = generateUIDContent(
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID],
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID]
        );
    } else {
        whatever = generateUIDContent("", "");
    }

    $("#advertising_content").append(whatever);

    $("#uid_namespace_id").bind("change keyup", function() {
        getUpdatedSlot();
    });

    $("#uid_instance_id").bind("change keyup", function() {
        getUpdatedSlot();
    });
}

function ibeaconTypeClicked() {
    $("#advertising_content").empty();

    let whatever = "";
    if (beacon.slots.slots[index] != null && beacon.slots.slots[index].type == FrameType.IBEACON) {
        whatever = generateIBeaconContent(
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_UUID],
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MAJOR],
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MINOR]
        );
    } else {
        whatever = generateIBeaconContent("", "", "");
    }

    $("#advertising_content").append(whatever);

    $("#ibeacon_uuid").bind("change keyup", function() {
        clearTimeout(ibeaconUuidTimeoutId);
        ibeaconUuidTimeoutId = setTimeout(getUpdatedSlot, TIMEOUT_INTERVAL);
    });

    $("#ibeacon_major").bind("change keyup", function() {
        getUpdatedSlot();
    });

    $("#ibeacon_minor").bind("change keyup", function() {
        getUpdatedSlot();
    });
}

function customTypeClicked() {
    $("#advertising_content").empty();

    let whatever = "";
    if (beacon.slots.slots[index] != null && beacon.slots.slots[index].type == FrameType.CUSTOM) {
        whatever = generateCustomContent(
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM],
            false
        );
    } else {
        whatever = generateCustomContent("", "");
    }

    $("#advertising_content").append(whatever);


    $("#custom_value").prop('disabled', beacon.slots.slots[index].readOnly)


    $("#custom_value").bind("change keyup", function() {
        clearTimeout(customContnetnTimeoutId);
        customContnetnTimeoutId = setTimeout(getUpdatedSlot, TIMEOUT_INTERVAL);
    });
}

function initListeners() {
    if (inited) {
        return;
    }

    //Updates text of dropdown header when selected.
    $(".dropdown-menu").on('click', 'li a', function() {
        getUpdatedSlot();
    });

    $('#slot-name-text').on('change keyup paste', function() {
        $('#slot-name-text').val($(this).val());
        getUpdatedSlot();
    });

    $('#eventable-params-value').on('keyup paste', function() {
        getUpdatedSlot();
    });

    $('#eventable-params-value').on('keypress', isNumber);

    $(document).on("change", "#toggle-advertise-switch", function() {
        getUpdatedSlot();

    });

    $(document).on("change", "#toggle-internal-switch", function() {
        let mode = $('#toggle-internal-switch').prop("checked") ? "EVENT" : "INTERVAL";
        if (mode == 'INTERVAL') {
            $('#eventable-params-body').hide();
            $('#advertising-interval-body').show();
        } else {
            $('#eventable-params-body').show();
            $('#advertising-interval-body').hide();
        }
        getUpdatedSlot();
    });


    $(document).on("input", "#range-packetCount", function() {
        // Real value is range value+1, because range starting position (0) is 1
        let realValue = parseInt($(this).val()) + 1
        $('label[for="range-packetCount"]').html(realValue);
        getUpdatedSlot();
    });

    $(document).on("input", "#advertising-interval-time-range", function() {
        let index = $(this).val();
        console.log('range index is ' + index)
        $('#advertising-interval-time').text(timeToHighestOrder(INTERVAL_MS[index]));
        getUpdatedSlot();
    });

    //Dropdown items
    $('#frame-type-default').click(defaultTypeClicked);

    $('#frame-type-empty').click(emptyTypeClicked);
    $('#frame-type-url').click(urlTypeClicked);

    $('#frame-type-uid').click(uidTypeClicked);

    $('#frame-type-ibeacon').click(ibeaconTypeClicked);

    $('#frame-type-custom').click(customTypeClicked);

    inited = true;
}

// Callback which will be invoked when one of the enum is choosen (now considering only enum with tx power data)
function dropDownChanged(id, element, position, index,slotIndex) {
    // For some reason can't do it with jQuery
    document.getElementById("ddl-menu-button-" + id + '-' + slotIndex).innerHTML = element;
    getUpdatedSlot()
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
    index = slotIndex;
    beacon = JSON.parse(slotJson.replace(/\\u0000/g, ''));

    switch (beacon.slots.slots[index].type) {
        case FrameType.UID:
            uidTypeClicked(slotIndex);
            $('#btn-frame-type').html(FrameType.UID);
            break;
        case FrameType.URL:
            urlTypeClicked(slotIndex);
            $('#btn-frame-type').html(FrameType.URL);
            break;
        case FrameType.IBEACON:
            ibeaconTypeClicked(slotIndex);
            $('#btn-frame-type').html(FrameType.IBEACON);
            break;
        case FrameType.CUSTOM:
            customTypeClicked(slotIndex);
            $('#btn-frame-type').html(FrameType.CUSTOM);
            break;
        case FrameType.DEFAULT:
            defaultTypeClicked(slotIndex);
            $('#btn-frame-type').html(FrameType.DEFAULT);
            let defaultMenuItem = generateFrameTypeMenuItem("DEFAULT",slotIndex);
            $('#dropdown-frame-type').prepend(defaultMenuItem);
            break;
        default:
            emptyTypeClicked(slotIndex);
            $('#btn-frame-type').html(FrameType.EMPTY);
    }

    console.log("Slot name: " + beacon.slots.slots[index].name);

    $('#slot-name-text').val(beacon.slots.slots[index].name);

    $('#slot-name-text').prop('disabled', beacon.slots.slots[index].readOnly)


    let params = generateSwitchContent(beacon.slots.slots[index].readOnly, beacon.slots.slots[index].active, "Slot Advertising", "advertise-switch");
    $('#slot-advertising').empty();
    $('#slot-advertising').append(params);

    $('#base-parameter').empty();

    params = generateBaseParameter();
    $('#base-parameter').append(params);

    params = generateSwitchContent(beacon.slots.slots[index].readOnly, beacon.slots.slots[index].advertisingMode == "EVENT", "Interval/Event", "internal-switch");
    $('#base-parameter').append(params);

    insertEventableParams()

    insertAdvIntervalParams()

    if (beacon.slots.slots[index].advertisingMode == 'INTERVAL') {
        $('#eventable-params-body').hide();
    } else {
        $('#advertising-interval-body').hide();
    }

    params = generatePacketCountRange("Advertising PacketCount", beacon.slots.slots[index].packetCount, 'packetCount', 9);
    $('#base-parameter').append(params);

    params = generateEnums(beacon.parameters.parameters["Basic config"].find(e => e.name === "Supported TX powers").value.split(','), "tx_power", "Advertising Tx Power", beacon.slots.slots[index].txPower, 0)
    $('#base-parameter').append(params);

    $('#btn-frame-type').prop('disabled', beacon.slots.slots[index].readOnly)

    initListeners();
}

function insertAdvIntervalParams() {
    let advInterval = beacon.slots.slots[index].advertisingModeParameters.interval
    if (!advInterval) advInterval = 250;
    params = generateAdvertisingIntervalInfo(advInterval);
    $('#base-parameter').append('<div id="advertising-interval-body"></div>');
    $('#advertising-interval-body');
    let rangePosition = getClosestElementPosition(INTERVAL_MS, advInterval) + 1;
    let intervalTextRepresentation = timeToHighestOrder(advInterval);
    params = generateAdvertisingIntervalRange("Advertising Interval", intervalTextRepresentation, rangePosition, INTERVAL_MS.length - 1)
    $('#advertising-interval-body').append(params);
}

function insertEventableParams() {
    let paramId = beacon.slots.slots[index].advertisingModeParameters.parameterId;
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

    let sign = beacon.slots.slots[index].advertisingModeParameters.sign;
    if (!sign) {
        sign = beacon.eventableParams.signs[0]
    }
    if (!sign) {
        // we don't have any eventable sings available.
        return;
    }

    params = generateEventableParams(beacon.eventableParams.params.flatMap(element => element.id + '-' + element.name), eventableParam, beacon.eventableParams.signs, sign, "Value");


    $('#base-parameter').append(params);

    if (beacon.slots.slots[index].readOnly) {
        $('#ddl-menu-button-eventable-params').prop('disabled', true);
        $('#ddl-menu-button-eventable-params-sings').prop('disabled', true);
        $('#eventable-params-value').prop('disabled', true);
    }
    let value = beacon.slots.slots[index].advertisingModeParameters.thresholdInt;
    if (!value) {
        value = 0;
    }
    $('#eventable-params-value').val(value);
}

function getUpdatedSlot() {
    beacon.slots.slots[index].name = $('#slot-name-text').val();
    let type = $('#btn-frame-type').text().trim();

    beacon.slots.slots[index].active = $('#toggle-advertise-switch').prop("checked");

    let advMode = $('#toggle-internal-switch').prop("checked") ? "EVENT" : "INTERVAL";

    beacon.slots.slots[index].advertisingMode = advMode;
    if (advMode == "EVENT") {
        let parameter = $('#ddl-menu-button-eventable-params').html();
        let parameterId = parseInt(parameter.split('-')[0]);
        let sign = $('#ddl-menu-button-eventable-params-sings').html();
        let intValue = parseInt($('#eventable-params-value').val());
        if (!intValue) {
            intValue = 0;
        }
        beacon.slots.slots[index].advertisingModeParameters.parameterId = parameterId;
        beacon.slots.slots[index].advertisingModeParameters.sign = sign;
        beacon.slots.slots[index].advertisingModeParameters.thresholdInt = intValue;
    } else {
        let rangePosition = $('#advertising-interval-time-range').val();
        let adInterval = INTERVAL_MS[rangePosition];
        beacon.slots.slots[index].advertisingModeParameters.interval = adInterval
    }

    console.log("sending updating for slot : " + beacon.slots.slots[index].name + " " + type);
    let packetValue = parseInt($('#range-packetCount').val()) + 1
    beacon.slots.slots[index].packetCount = packetValue;

    let txPower;
    if ($('#ddl-menu-button-tx-power').text()) {
        txPower = parseInt($('#ddl-menu-button-tx-power').text());
    } else {
        txPower = beacon.slots.slots[index].txPower;
    }
    beacon.slots.slots[index].txPower = txPower;
    beacon.slots.slots[index].readOnly = $('#range-internal-switch').val();

    switch (type) {
        case "UID":
            beacon.slots.slots[index].type = FrameType.UID;
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID] = $('#uid-namespace-id').val();
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID] = $('#uid-instance-id').val();
            break;
        case "URL":
            beacon.slots.slots[index].type = FrameType.URL;
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_URL_URL] = $('#url').val();
            break;
        case "IBEACON":
            beacon.slots.slots[index].type = FrameType.IBEACON;
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_UUID] = $('#ibeacon-uuid').val();
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MAJOR] = $('#ibeacon-major').val();
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_IBEACON_MINOR] = $('#ibeacon-minor').val();
            break;
        case "CUSTOM":
            beacon.slots.slots[index].type = FrameType.CUSTOM;
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM] = $('#custom-value').val()
            break;

        case "DEFAULT":
            beacon.slots.slots[index].type = FrameType.DEFAULT;
            beacon.slots.slots[index]["advertisingContent"][KEY_ADVERTISING_CONTENT_DEFAULT_DATA] = $('#default-advertising-content').val();
            break;

        default:
            beacon.slots.slots[index].type = FrameType.EMPTY;
            beacon.slots.slots[index]["advertisingContent"] = {};
    }

    native.onDataChanged(JSON.stringify(beacon));
}