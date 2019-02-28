//GLOBAL Slot object
let slot;

$(document).ready(function () {

    let emptyMenuItem = generateFrameTypeMenuItem("EMPTY");
    //let uidMenuItem = generateFrameTypeMenuItem("UID"); //Not supported yet
    let urlMenuItem = generateFrameTypeMenuItem("URL");
    let ibeaconMenuItem = generateFrameTypeMenuItem("IBEACON");
    let customMenuItem = generateFrameTypeMenuItem("CUSTOM");

    $('#dropdown_frame_type').append(
        emptyMenuItem /*+ uidMenuItem*/ + urlMenuItem + ibeaconMenuItem + customMenuItem
    );

    //Updates text of dropdown header when selected.
    $(".dropdown-menu").on('click', 'li a', function () {
        $(this).parent().parent().siblings(".btn:first-child").html($(this).text() + ' <span class="caret"></span>');
        $(this).parent().parent().siblings(".btn:first-child").val($(this).text());
        getUpdatedSlot();
    });

    //Dropdown items
    $('#frame_type_empty').click(
        function () {
            $("#advertising_content").empty();
        }
    );

    $('#frame_type_url').click(
        function () {
            $("#advertising_content").empty();

            let whatever = "";
            if (slot != null && slot.frameType == FrameType.URL) {
                whatever = generateURLContent(slot.frame[KEY_ADVERTISING_CONTENT_URL_URL].trim());
            } else {
                whatever = generateURLContent("");
            }

            $("#advertising_content").append(whatever);

            $("#url").bind("change keyup", function () {
                getUpdatedSlot();
            });
        }
    );

    $('#frame_type_uid').click(
        function () {
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

            $("#uid_namespace_id").bind("change keyup", function () {
                getUpdatedSlot();
            });

            $("#uid_instance_id").bind("change keyup", function () {
                getUpdatedSlot();
            });
        }
    );

    $('#frame_type_ibeacon').click(
        function () {
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

            $("#ibeacon_uuid").bind("change keyup", function () {
                getUpdatedSlot();
            });

            $("#ibeacon_major").bind("change keyup", function () {
                getUpdatedSlot();
            });

            $("#ibeacon_minor").bind("change keyup", function () {
                getUpdatedSlot();
            });
        }
    );

    $('#frame_type_custom').click(
        function () {
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

            $("#custom_value").bind("change keyup", function () {
                getUpdatedSlot();
            });

            $('#custom_hex_enabled').change(function () {
                getUpdatedSlot();
            });
        }
    );

    //Sliders
    $('#advertising_interval').slider({
        value: 99,
        formatter: function (value) {
            $('#label_advertising_interval').text((value * 10) + " ms");
            return 'Current value: ' + value;
        }
    });

    $('#advertising_interval').slider().on('slideStop', function (ev) {
        getUpdatedSlot();
    });

    $('#rssi_1m').slider({
        value: 55,
        formatter: function (value) {
            $('#label_rssi_1m').text((value - 100) + " dBm");
            return 'Current value: ' + value;
        }
    });

    $('#rssi_1m').slider().on('slideStop', function (ev) {
        getUpdatedSlot();
    });

    $('#radio_tx').slider({
        value: 2,
        formatter: function (value) {
            $('#label_radio_tx').text((value - 100) + " dBm");
            return 'Current value: ' + value;
        }
    });

    $('#radio_tx').slider().on('slideStop', function (ev) {
        getUpdatedSlot();
    });

    $('#enable_cb').change(function () {
        getUpdatedSlot();
    });

});

function init(slotJson) {
    Android.onError("BEFORE = " + slotJson);
    slot = JSON.parse(slotJson);
    Android.onError("AFTER = " + JSON.stringify(slot));

    //Selected FrameType, assume empty is selected.
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
        default:
            $('#frame_type_empty').click();
    }

    $('#advertising_interval').slider('setValue', slot.advertisingInterval / 10);
    $('#rssi_1m').slider('setValue', slot.rssi1m + 100);
    $('#radio_tx').slider('setValue', slot.radioTx + 100);

    //Trigger
    $("#enable_cb").prop('checked', slot.triggerEnabled);

    if (slot.triggerType == TriggerType.TRIPLE_TAP) {
        $("#btn_triple_tap").click();
    } else if (slot.triggerType == TriggerType.DOUBLE_TAP) {
        $("#btn_double_tap").click();
    } else {
        //No-Op
    }
}

function getUpdatedSlot() {
    //Create new Slot
    let slot = new Slot()

    let frameType = $('#btn_frame_type').text().trim();

    switch (frameType) {
        case "UID":
            slot.frameType = FrameType.UID;
            slot.frame = {
                KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID: $('#uid_namespace_id').val(),
                KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID: $('#uid_instance_id').val()
            };
            break;
        case "URL":
            slot.frameType = FrameType.URL;
            slot.frame = {
                KEY_ADVERTISING_CONTENT_URL_URL: $('#url').val()
            };
            break;
        case "IBEACON":
            slot.frameType = FrameType.IBEACON;
            slot.frame = {
                KEY_ADVERTISING_CONTENT_IBEACON_UUID: $('#ibeacon_uuid').val(),
                KEY_ADVERTISING_CONTENT_IBEACON_MAJOR: $('#ibeacon_major').val(),
                KEY_ADVERTISING_CONTENT_IBEACON_MINOR: $('#ibeacon_minor').val()
            };
            break;
        case "CUSTOM":
            slot.frameType = FrameType.CUSTOM;
            slot.frame = {
                KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM: $('#custom_value').val(),
                KEY_ADVERTISING_CONTENT_CUSTOM_IS_HEX_MODE_ON: $('#custom_hex_enabled').is(":checked")
            };
            break;
        default:
            slot.frameType = FrameType.EMPTY;
            slot.frame = {};
    }


    slot.advertisingInterval = $('#advertising_interval').slider('getValue') * 10;
    slot.rssi1m = $('#rssi_1m').slider('getValue') - 100;
    slot.radioTx = $('#radio_tx').slider('getValue') - 100;

    slot.triggerEnabled = $("#enable_cb").is(":checked");

    let triggerType = $('#dropdown_trigger_type').text().trim();
    if (triggerType.includes("Double")) {
        slot.triggerType = TriggerType.DOUBLE_TAP;
    } else if (triggerType.includes("Triple")) {
        slot.triggerType = TriggerType.TRIPLE_TAP;
    } else {
        //No-Op
    }

    //console.log(slot);
    Android.onDataChanged(JSON.stringify(slot));
}