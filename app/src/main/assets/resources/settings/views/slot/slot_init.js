//GLOBAL Slot object
let slot;
let inited = false
const INTERVAL_MS = [50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000,
    11000, 12000, 13000, 14000, 15000, 15000, 20000, 30000, 40000, 50000, 60000, 120000, 180000, 240000, 300000, 360000, 420000, 480000,
    540000, 600000, 660000, 720000, 780000, 840000, 900000, 900000, 1200000, 1800000, 2400000, 3000000, 3600000, 7200000, 10800000,
    14400000, 18000000, 21600000, 25200000, 28800000, 32400000, 36000000, 39600000, 43200000, 46800000, 50400000, 54000000, 57600000,
    61200000, 64800000, 68400000, 72000000, 75600000, 79200000, 82800000, 86400000
]

$(document).ready(function() {
    if (inited) {
        return;
    }

    let emptyMenuItem = generateFrameTypeMenuItem("DEFAULT");
    //let uidMenuItem = generateFrameTypeMenuItem("UID"); //Not supported yet
    let urlMenuItem = generateFrameTypeMenuItem("URL");
    let customMenuItem = generateFrameTypeMenuItem("CUSTOM");
    let ibeaconMenuItem = generateFrameTypeMenuItem("IBEACON");


    $('#dropdown_frame_type').append(
        emptyMenuItem /*+ uidMenuItem*/ + urlMenuItem + customMenuItem + ibeaconMenuItem
    );

    //Updates text of dropdown header when selected.
    $(".dropdown-menu").on('click', 'li a', function() {
        $(this).parent().parent().siblings(".btn:first-child").html($(this).text() + ' <span class="caret"></span>');
        $(this).parent().parent().siblings(".btn:first-child").val($(this).text());
        getUpdatedSlot();
    });

    $('#slot_name_text').on('change keyup paste', function() {
        $('#slot_name_text').val($(this).val());
        getUpdatedSlot();
    });

    $('#toggle-advertise-switch').change(function() {
        $('#toggle-advertise-switch').val($(this).val());
        getUpdatedSlot();

    });

    $('#toggle-internal-switch').change(function() {
        $('#toggle-internal-switch').val($(this).val());
        getUpdatedSlot();

    });


    $(document).on("input", "#range-packetCount", function() {
        // Real value is range value+1, because range starting position (0) is 1
        let realValue = parseInt($(this).val()) + 1
        $('label[for="range-packetCount"]').html(realValue);
        getUpdatedSlot();
    });

    $(document).on("input", "#advertising_interval_time_range", function() {
        let index = $(this).val();
        $('#advertising_interval_time').text(timeToHighestOrder(INTERVAL_MS[index]));
        getUpdatedSlot();
    });

    //Dropdown items
    $('#frame_type_default').click(
        function() {
            $("#advertising_content").empty();

            let whatever = "";

            if (slot != null && slot.frameType == FrameType.DEFAULT) {
                whatever = generateDefaultContent(slot.frame[KEY_ADVERTISING_CONTENT_DEFAULT_DATA]);

            } else {
                whatever = generateDefaultContent("");
            }

            $("#advertising_content").append(whatever);

            $("#default_advertising_content").bind("change keyup", function() {
                getUpdatedSlot();
            });
        });

    $('#frame_type_empty').click(
        function() {
            $("#advertising_content").empty();
        }
    );
    $('#frame_type_url').click(
        function() {
            $("#advertising_content").empty();

            let whatever = "";
            if (slot != null && slot.frameType == FrameType.URL) {
                whatever = generateURLContent(slot.frame[KEY_ADVERTISING_CONTENT_URL_URL].trim());
            } else {
                whatever = generateURLContent("");
            }

            $("#advertising_content").append(whatever);

            $("#url").bind("change keyup", function() {
                getUpdatedSlot();
            });
        }
    );

    $('#frame_type_uid').click(
        function() {
            $("#advertising_content").empty();

            let whatever = "";
            if (slot != null && slot.frameType == FrameType.UID) {
                whatever = generateUIDContent(
                    slot.frame[KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID],
                    slot.frame[KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID]
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
    );

    $('#frame_type_ibeacon').click(
        function() {
            $("#advertising_content").empty();

            let whatever = "";
            if (slot != null && slot.frameType == FrameType.IBEACON) {
                whatever = generateIBeaconContent(
                    slot.frame[KEY_ADVERTISING_CONTENT_IBEACON_UUID],
                    slot.frame[KEY_ADVERTISING_CONTENT_IBEACON_MAJOR],
                    slot.frame[KEY_ADVERTISING_CONTENT_IBEACON_MINOR]
                );
            } else {
                whatever = generateIBeaconContent("", "", "");
            }

            $("#advertising_content").append(whatever);

            $("#ibeacon_uuid").bind("change keyup", function() {
                getUpdatedSlot();
            });

            $("#ibeacon_major").bind("change keyup", function() {
                getUpdatedSlot();
            });

            $("#ibeacon_minor").bind("change keyup", function() {
                getUpdatedSlot();
            });
        }
    );

    $('#frame_type_custom').click(
        function() {
            $("#advertising_content").empty();

            let whatever = "";
            if (slot != null && slot.frameType == FrameType.CUSTOM) {
                whatever = generateCustomContent(
                    slot.frame[KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM],
                    false
                );
            } else {
                whatever = generateCustomContent("", "");
            }

            $("#advertising_content").append(whatever);

            $("#custom_value").bind("change keyup", function() {
                getUpdatedSlot();
            });
        }
    );

    inited = true;
});

// Callback which will be invoked when one of the enum is choosen (now considering only enum with tx power data)
function dropDownChanged(id, element, position, index) {
    // For some reason can't do it with jQuery
    document.getElementById("ddl-menu-button-tx_power").innerHTML = element;
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

function init(slotJson) {
    slot = JSON.parse(slotJson);
    console.log("SlotJson: " + slotJson)
    switch (slot.frameType) {
        case FrameType.UID:
            $('#frame_type_uid').click();
            break;
        case FrameType.URL:
            $('#frame_type_url').click();
            break;
        case FrameType.IBEACON:
            $('#frame_type_ibeacon').click();
            break;
        case FrameType.CUSTOM:
            $('#frame_type_custom').click();
            break;
        case FrameType.DEFAULT:
            $('#frame_type_default').click();
            break;
        default:
            $('#frame_type_empty').click();
    }

    $('#slot_name_text').val(slot.name);

    if (slot.frameType == FrameType.DEFAULT) {
        $('#slot_name_text').prop('disabled', true);
    }

    let params = generateSwitchContent(slot.frameType, slot.advertising, "Slot Advertising", "advertise-switch");
    $('#slot_advertising').empty();
    $('#slot_advertising').append(params);

    $('#base_parameter').empty();

    params = generateBaseParameter();
    $('#base_parameter').append(params);

    params = generateSwitchContent(true, "Internal/Event", "internal-switch");
    $('#base_parameter').append(params);

    if (slot.addInterval) {
        let rangePosition = getClosestElementPosition(INTERVAL_MS, slot.addInterval) + 1;
        let intervalTextRepresentation = timeToHighestOrder(slot.addInterval);
        params = generateAdvertisingIntervalRange("Advertising Interval", intervalTextRepresentation, rangePosition, INTERVAL_MS.length - 1);
        $('#base_parameter').append(params);
    }

    params = generatePacketCountRange("Advertising PacketCount", slot.packetCount, 'packetCount', 9);
    $('#base_parameter').append(params);

    params = generateEnums(slot.supportedtxPower, "tx_power", "Advertising Tx Power", slot.txPower, 0)
    $('#base_parameter').append(params);

    if (slot.readOnly == true) {
        $('#btn_frame_type').prop('disabled', true)
    }

}

function getUpdatedSlot() {
    //Create new Slot
    let slot_new = new Slot();

    slot_new.name = $('#slot_name_text').val();
    let frameType = $('#btn_frame_type').text().trim();
    let adv = false;
    if ($('#toggle-advertise-switch').val() == 'on')
        adv = true;

    slot_new.advertising = adv;
    console.log("GetJSON IS called: " + slot_new.name + " " + frameType + " " + $('#range-packetCount').val());
    let packetValue = parseInt($('#range-packetCount').val()) + 1
    slot_new.packetCount = packetValue;
    slot_new.addInterval = $('#range-addInterval').val();
    slot_new.supportedtxPower = slot.supportedtxPower;

    let txPower;
    if($('#ddl-menu-button-tx_power').text()){
        txPower = $('#ddl-menu-button-tx_power').text();
    }else{
        txPower = slot.txPower;
    }
    slot_new.txPower = txPower;
    slot_new.readOnly = $('#range-internal-switch').val();

    switch (frameType) {
        case "UID":
            slot_new.frameType = FrameType.UID;
            slot_new.frame = {
                KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID: $('#uid_namespace_id').val(),
                KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID: $('#uid_instance_id').val()
            };

            break;
        case "URL":
            slot_new.frameType = FrameType.URL;
            slot_new.frame = {
                KEY_ADVERTISING_CONTENT_URL_URL: $('#url').val()
            };

            break;
        case "IBEACON":
            slot_new.frameType = FrameType.IBEACON;
            slot_new.frame = {
                KEY_ADVERTISING_CONTENT_IBEACON_UUID: $('#ibeacon_uuid').val(),
                KEY_ADVERTISING_CONTENT_IBEACON_MAJOR: $('#ibeacon_major').val(),
                KEY_ADVERTISING_CONTENT_IBEACON_MINOR: $('#ibeacon_minor').val()
            };

            break;
        case "CUSTOM":
            slot_new.frameType = FrameType.CUSTOM;
            slot_new.frame = {
                KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM: $('#custom_value').val(),
            };

            break;

        case "DEFAULT":
            slot_new.frameType = FrameType.DEFAULT;
            slot_new.frame = {
                KEY_ADVERTISING_CONTENT_DEFAULT_DATA: $('#default_advertising_content').val(),
            };

            break;

        default:
            slot_new.frameType = FrameType.EMPTY;
            slot_new.frame = {};

    }

   Android.onDataChanged(JSON.stringify(slot_new));
}