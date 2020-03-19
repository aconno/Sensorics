function generateUIDContent(namespaceId, instanceId,slotIndex) {
    return '<div class="row">' +
        '<div class="col-2">' +
        '<label>Namespace Id</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input type="text" style="width: 100%;" id="uid-namespace-id-'+slotIndex+'" value="' + namespaceId + '" class="holo">' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        ' <div class="col-3">' +
        ' <label>Instance Id</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input type="text" style="width: 100%;" id="uid-instance-id-'+slotIndex+'" value="' + instanceId + '" class="holo">' +
        '</div>' +
        '</div>';
}

function generateTexInput(id, label,slotIndex) {
    return '<div class="row">' +
        '<div class="col-3">' +
        '<label>' + label + '</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="text" id="' + id + '-' + slotIndex + '" class="holo">' +
        '</div>' +
        '</div>';
}

function generateURLContent(url,slotIndex) {
    return '<div class="row">' +
        '<div class="col-3">' +
        '<label>URL</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="text" id="url-'+slotIndex+'" value="' + url + '" class="holo">' +
        '</div>' +
        '</div>';
}

function generateIBeaconContent(uuid, major, minor,slotIndex) {
    return '<div class="row">' +
        '<div class="col-3">' +
        '<label>UUID</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" oninput="onUuidInput(this)" type="text" id="ibeacon-uuid-'+slotIndex+'" value="' + uuid + '" class="holo">' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        '<div class="col-3">' +
        '<label>Major</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="number" min="0" id="ibeacon-major-'+slotIndex+'" value="' + major + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">' +
        '</div>' +
        '</div>' +
        '<div class="row" style="margin-top: 1em;">' +
        '<div class="col-3">' +
        '<label>Minor</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<input style="width: 100%;" type="number" min="0" id="ibeacon-minor-'+slotIndex+'" value="' + minor + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">' +
        '</div>' +
        '</div>';
}

function generateCustomContent(customValue, isHexModeOn,slotIndex) {
    return '<div class="row" style="margin-top: 1em;">' +
        '<div class="col-3">' +
        '<label>Custom Value</label>' +
        '</div>' +
        '<div class="col-9">' +
        '<textarea style="width: 100%"; id="custom-value-'+slotIndex+'" class="holo" wrap="soft" rows="4">' + customValue + '</textarea>' +
        '</div>' +
        '</div>';
}

function generateDefaultContent(value,slotIndex) {
    return '<div class="panel-body">' +
        '<div class="panel-body">' +
        '<input type="text" value="' + value + '" id="default-advertising-content-'+slotIndex+'" disabled style="width: 100%"/>' +
        '</div>' +
        '</div>';
}

function generateBaseParameter() {
    return '<div class="row">' +
        '<div class="col-12">' +
        '<label>Base Parameter</label>' +
        '</div>' +
        '</div>';
}

function generateEnums(elements, id, name, value, index, slotIndex) {
    let body = generateEnumsBody(id, elements, index, slotIndex);
    let html = '<div class="panel-body">';
    html += '<div class="form-group">';
    if (name.trim()) {
        // Not empty
        html += '<label for="ddl-lbl-' + id + '-' + slotIndex + '">' + name + '</label>';
    }
    html += '<div id="ddl-' + id + '-' + slotIndex + '" class="dropdown">';
    html += '<button class="btn btn-secondary dropdown-toggle" type="button" id="ddl-menu-button-' + id + '-' + slotIndex + '"  data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">';
    html += value;
    html += "</button>";
    html += body;
    html += '</div>';
    html += '</div>';
    html += '</div>';

    return html;
}

function generateEnumsBody(id, elements, index,slotIndex) {
    var html = '<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">';
    elements.forEach((element, position) => {
        html += generateSingleEnum(id, element, position, index,slotIndex);
    });
    html += '</div>';
    return html
}

function generateSingleEnum(id, element, position, index,slotIndex) {
    return '<a onclick="dropDownChanged(\'' + id + '\', \'' + element + '\', ' + position + ' ,' + index + ',' + slotIndex + ')" class="dropdown-item" >' + element + '</a>';
}

function generateAdvertisingIntervalRange(name, advertisingInterval, value, maxValue,slotIndex) {

    return '<div class="panel-body">' +
        '<label>' + name + ' </label> </div>' +
        '<div class="panel-body">' +
        '<label id="advertising-interval-time-'+slotIndex+'">' + advertisingInterval + '</label> <br>' +
        '<input type="range" style="width:90%" value=' + value + ' id="advertising-interval-time-range-'+slotIndex+'" max ="' + maxValue + '"/>' +
        '</div>' +
        '</div>';
}

function generateAdvertisingIntervalInfo(interval,slotIndex) {
    return '<div class="row">'
        + '<div class="col-12">'
        + '<span id = "advertising-interval-info-'+slotIndex+'">' + timeToHighestOrder(interval)+ '</span>'
        + '</div>'
        + '</div>';
}

function generatePacketCountRange(name, value, id, maxValue,slotIndex) {
    // Range starting position is 1
    let rangeValue = value - 1;

    return '<div class="panel-body">' +
        '<label>' + name + ' </label> <label for="range-' + id + '-' + slotIndex + '">' + value + '</label></div>' +
        '<div class="panel-body">' +
        '<input style="width:90%" type="range" value="' + rangeValue + '" id="range-' + id + '-' + slotIndex + '" max="' + maxValue + '"/>' +
        '</div>' +
        '</div>';
}

function generateSwitchContent(disabled, isChecked, name, id,slotIndex) {
    let checked = isChecked ? " checked" : "";

    let checkbox = '<input type="checkbox" class="custom-control-input" id="toggle-' + id + '-' + slotIndex + '"' + checked + ' ';
    if (disabled) {
        checkbox += "disabled";
    }
    checkbox += ' >';

    return '<div class="row">' +
        '<div class="col-12">' +
        '<div class="form-group">' +
        '<div class="custom-control custom-switch" >' +
        checkbox +
        '<label class="custom-control-label" for="toggle-' + id + '-' + slotIndex + '">' + name + '</label>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';
}

function generateFrameTypeMenuItem(name,slotIndex) {
    return '<li><a class="dropdown-item" id="frame-type-' + name.toLowerCase() + '-' + slotIndex + '" href="#">' + name + '</a></li>'
}

function onlyPositiveNumberFilter(event) {
    return (event.charCode >= 48 && event.charCode <= 57) || event.charCode === 0
}

function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
    }
    return true;
}

function generateEventableParams(eventableParams, defaultEventParam, sings, defaultSing, valueName,slotIndex) {
    return '<div id="eventable-params-body-' + slotIndex +'">' +
        generateEnums(eventableParams, "eventable-params", "", defaultEventParam, 0,slotIndex) +
        generateEnums(sings, "eventable-params-sings", "", defaultSing, 0,slotIndex) +
        generateTexInput("eventable-params-value", valueName,slotIndex) + '</br>';
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
    });

    return intArray.indexOf(chosen);
}


function timeToHighestOrder(copy) {

    let millis = (copy % 1000);
    copy -= millis;
    let seconds = ((copy / 1000) % 60);
    copy -= 1000 * seconds;
    let minutes = ((copy / 60000) % 60);
    copy -= 60000 * seconds;
    let hours = (copy / 3600000);
    copy -= 3600000 * hours;
    let days = (copy / 86400000);
    copy -= 86400000 * days;
    let weeks = (copy / 604800000);
    copy -= 604800000 * weeks;
    let months = (copy / 2419200000);
    copy -= 2419200000 * months;

    if (millis !== 0) {
        return "Every " + (millis + seconds * 1000) + " milliseconds";
    } else if (seconds !== 0) {
        return "Every " + (seconds + minutes * 60) + " seconds";
    } else if (minutes !== 0) {
        return "Every " + (minutes + hours * 60) + " minutes";
    } else if (hours !== 0) {
        return "Every " + (hours + days * 24) + " hours";
    } else if (days !== 0) {
        return "Every " + (days + weeks * 7) + " days";
    } else if (weeks !== 0) {
        return "Every " + (weeks + months * 4) + " weeks";
    } else {
        return "Every " + months + " months";
    }
}