//GLOBAL Slot object
let slot;
let inited = false

$(document).ready(function () {
    if (inited) {
        return;
    }
    console.log("Ready Executed");
    let emptyMenuItem = generateFrameTypeMenuItem("EMPTY");
    //let uidMenuItem = generateFrameTypeMenuItem("UID"); //Not supported yet
    let urlMenuItem = generateFrameTypeMenuItem("URL");
    let customMenuItem = generateFrameTypeMenuItem("CUSTOM");
    let ibeaconMenuItem = generateFrameTypeMenuItem("IBEACON");


    $('#dropdown_frame_type').append(
        emptyMenuItem /*+ uidMenuItem*/ + urlMenuItem + customMenuItem + ibeaconMenuItem
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
                let isHexEnabledButtonChecked = $('#custom_hex_enabled').is(":checked");
                let sad = $('#custom_value').val();

                if (isHexEnabledButtonChecked) {
                    $('#custom_value').val(ascii_to_hexa(sad));
                } else {
                    $('#custom_value').val(hex_to_ascii(sad));
                }

                getUpdatedSlot();
            });
        }
    );

    inited = true;
});

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
     console.log("Slot name is: "+slot.name);
     if(slot.name) {
         $('#slot_name_text').val(slot.name);
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
    Android.onDataChanged(JSON.stringify(slot));
}