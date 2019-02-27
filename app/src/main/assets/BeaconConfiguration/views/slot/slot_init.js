$(document).ready(function () {
    init("a");
});

function init(slot) {
    //Init slot..
    let emptyMenuItem = generateFrameTypeMenuItem("EMPTY");
    let uidMenuItem = generateFrameTypeMenuItem("UID");
    let urlMenuItem = generateFrameTypeMenuItem("URL");
    let ibeaconMenuItem = generateFrameTypeMenuItem("IBEACON");
    let customMenuItem = generateFrameTypeMenuItem("CUSTOM");

    $('#dropdown_frame_type').append(
        emptyMenuItem + uidMenuItem + urlMenuItem + ibeaconMenuItem + customMenuItem
    );

    //Updates text of dropdown header when selected.
    $(".dropdown-menu").on('click', 'li a', function () {
        $(this).parent().parent().siblings(".btn:first-child").html($(this).text() + ' <span class="caret"></span>');
        $(this).parent().parent().siblings(".btn:first-child").val($(this).text());
    });

    //Dropdown items
    $('#frame_type_empty').click(
        function () {
            $("#advertising_content").empty();
        }
    )

    $('#frame_type_url').click(
        function () {
            $("#advertising_content").empty();
            let whatver = generateURLContent("https://www.youtube.com/watch?v=mIYzp5rcTvU");
            $("#advertising_content").append(whatver);
        }
    )

    $('#frame_type_uid').click(
        function () {
            $("#advertising_content").empty();
            let whatver = generateUIDContent("12333", "213124");
            $("#advertising_content").append(whatver);
        }
    )

    $('#frame_type_ibeacon').click(
        function () {
            $("#advertising_content").empty();
            let whatver = generateIBeaconContent("cc520000-0000-0000-0000-000000000000", 3, 5);
            $("#advertising_content").append(whatver);
        }
    )

    $('#frame_type_custom').click(
        function () {
            $("#advertising_content").empty();
            let whatver = generateCustomContent("cc520000-0000-0000-0000-000000000000", true);
            $("#advertising_content").append(whatver);
            $("input[data-toggle='toggle']").bootstrapToggle();
        }
    )

    //Selected FrameType, assume empty is selected.
    //$('#frame_type_empty').click();
    //$('#frame_type_url').click();
    //$('#frame_type_uid').click();
    $('#frame_type_ibeacon').click();
    //$('#frame_type_custom').click();

    //Sliders
    $('#advertising_interval').slider({
        value: 99,
        formatter: function (value) {
            $('#label_advertising_interval').text((value * 10) + " ms");
            return 'Current value: ' + value;
        }
    });

    $('#rssi_1m').slider({
        value: 55,
        formatter: function (value) {
            $('#label_rssi_1m').text((value - 100) + " dBm");
            return 'Current value: ' + value;
        }
    });

    $('#radio_tx').slider({
        value: 2,
        formatter: function (value) {
            $('#label_radio_tx').text((value - 100) + " dBm");
            return 'Current value: ' + value;
        }
    });

    //Trigger
    $("#enable_cb").bootstrapToggle('on');

    //$("btn_double_tap").click() 
    //or
    $("#btn_triple_tap").click()
}

function getUpdatedSlot() {
    //Create new Slot
    let slot = new Slot()

    let frameType = $('#btn_frame_type').text().trim();

    //TODO frametype swtich case doesnt work
    switch (frameType) {
        case "EMPTY":
            //Do Nothing Default is EMPTY
            console.log("EMPTY");
            break;
        case "UID":
            slot.FrameType = FrameType.UID;
            slot.frame = {
                namespaceId : $('#uid_namespace_id').val(),
                instanceId : $('#uid_instance_id').val()
            };
            console.log(slot.frame);
            break;
        case "URL":
            console.log("URL");
            break;
        case "IBEACON":
            console.log("IBEACON");
            break;
        case "CUSTOM":
            console.log("CUSTOM");
            break;
        default:
            console.log("Not equal");
    }
}