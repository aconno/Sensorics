function generateUIDContent(namespaceId, instanceId) {
    return '<div class="row">' +
        '<div class="col-2">' +
        '<label>Namespace Id</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input type="text" style="width: 100%;" id="uid_namespace_id" value="' + namespaceId + '" class="holo">' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        ' <div class="col-3">' +
        ' <label>Instance Id</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input type="text" style="width: 100%;" id="uid_instance_id" value="' + instanceId + '" class="holo">' +
        '</div>' +
        '</div>';
}

function generateURLContent(url) {
    return '<div class="row">' +
        '<div class="col-3">' +
        '<label>URL</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="text" id="url" value="' + url + '" class="holo">' +
        '</div>' +
        '</div>';
}

function generateIBeaconContent(uuid, major, minor) {
    return '<div class="row">' +
        '<div class="col-3">' +
        '<label>UUID</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="text" id="ibeacon_uuid" value="' + uuid + '" class="holo">' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        '<div class="col-3">' +
        '<label>Major</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="number" min="0" id="ibeacon_major" value="' + major + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        '<div class="col-3">' +
        '<label>Minor</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="number" min="0" id="ibeacon_minor" value="' + minor + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">' +
        '</div>' +
        '</div>';
}

function generateCustomContent(customValue, isHexModeOn) {
    return '<div class="row">' +
        '<div class="col-12">' +
        '<label>Custom</label>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        '<div class="col-3">' +
        '<label>Custom Value</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<textarea style="width: 100%"; id="custom_value" class="holo" wrap="soft" rows="4">' + customValue + '</textarea>' +
        '</div>' +
        '</div>';
}

function generateDefaultContent(value) {

    return '<div class="panel-body">' +
        '<div class="panel-body">' +
        '<input type="text" value="' + value + '" id="default_advertising_content" style="width: 100%"/>' +
        '</div>' +
        '</div>';
}

function generateBaseParameter(interval_checked) {
    return '<div class="row">' +
        '<div class="col-12">' +
        '<label>Base Parameter</label>' +
        '</div>' +
        '</div>';
}

function generateEnums(elements, id, name, value, index) {
    let body = generateEnumsBody(id, elements, index)
    let html = '<div class="panel-body">';
    html += '<div class="form-group">';
    html += '<label for="ddl-lbl-' + id + '">' + name + '</label>';
    html += '<div id="ddl-' + id + '" class="dropdown">';
    html += '<button class="btn btn-secondary dropdown-toggle" type="button" id="ddl-menu-button-' + id + '"  data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">';
    html += elements[value];
    html += "</button>";
    html += body;
    html += '</div>';
    html += '</div>';
    html += '</div>';
    return html;
}

function generateEnumsBody(id, elements, index) {
    var html = '<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">';
    elements.forEach((element, position) => {
        html += generateSingleEnum(id, element, position, index);
    });
    html += '</div>';
    return html
}

function generateSingleEnum(id, element, position, index) {
    return '<a onclick="dropDownChanged(' + id + ', \'' + element + '\', ' + position + ' ,' + index + ')" class="dropdown-item" >' + element + '</a>';
}

function generateAdvertisingIntervalRange(name, advertisingInterval, value, maxValue) {

    return '<div class="panel-body">' +
        '<label>' + name + ' </label> </div>' +
        '<div class="panel-body">' +
        '<label id="advertising_interval_time">' + advertisingInterval + '</label> <br>' +
        '<input type="range" style="width:90%" value=' + value + ' id="advertising_interval_time_range" max ="' + maxValue + '"/>' +
        '</div>' +
        '</div>';
}

function generatePacketCountRange(name, value, id, maxValue) {
    // Range starting position is 1
    let rangeValue = value - 1;

    return '<div class="panel-body">' +
        '<label>' + name + ' </label> <label for="range-' + id + '">' + value + '</label></div>' +
        '<div class="panel-body">' +
        '<input style="width:90%" type="range" value="' + rangeValue + '" id="range-' + id + '" max="' + maxValue + '"/>' +
        '</div>' +
        '</div>';
}

function generateSwitchContent(frameType, isChecked, name, id) {
    let checked = isChecked ? " checked" : "";
    let disabled = frameType == FrameType.DEFAULT;

    let checkbox = '<input type="checkbox" class="custom-control-input" id="toggle-' + id + '"' + checked + ' ';
    if (disabled) {
        checkbox += "disabled";
    }
    checkbox += ' >';

    return '<div class="row">' +
        '<div class="col-12">' +
        '<div class="form-group">' +
        '<div class="custom-control custom-switch" >' +
        checkbox +
        '<label class="custom-control-label" for="toggle-' + id + '">' + name + '</label>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';
}

function generateFrameTypeMenuItem(name) {
    return '<li><a class="dropdown-item" id="frame_type_' + name.toLowerCase() + '" href="#">' + name + '</a></li>'
}

function onlyPositiveNumberFilter(event) {
    return (event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0
}

function getClosestElementPosition(intArray, to) {
    let chosen = intArray[0];
    let diff = Math.abs(to - chosen);

    intArray.forEach(function(it) {
        let newDiff = Math.abs(to - it);
        if (newDiff < diff) {
            chosen = it;
            diff = newDiff;
        }
    })

    return intArray.indexOf(chosen);
}


function timeToHighestOrder(copy) {

    let millis = (copy % 1000);
    copy -= millis
    let seconds = ((copy / 1000) % 60);
    copy -= 1000 * seconds
    let minutes = ((copy / 60000) % 60);
    copy -= 60000 * seconds
    let hours = (copy / 3600000);
    copy -= 3600000 * hours
    let days = (copy / 86400000);
    copy -= 86400000 * days
    let weeks = (copy / 604800000);
    copy -= 604800000 * weeks
    let months = (copy / 2419200000);
    copy -= 2419200000 * months;

    if (millis != 0)
        return "Every " + (millis + seconds * 1000) + " milliseconds";

    else if (seconds != 0)
        return "Every " + (seconds + minutes * 60) + " seconds";

    else if (minutes != 0)
        return "Every " + (minutes + hours * 60) + " minutes";
    else if (hours != 0)
        return "Every " + (hours + days * 24) + " hours";
    else if (days != 0)
        return "Every " + (days + weeks * 7) + " days";
    else if (weeks != 0)
        return "Every " + (weeks + months * 4) + " weeks";
    else
        return "Every " + months + " months";
}