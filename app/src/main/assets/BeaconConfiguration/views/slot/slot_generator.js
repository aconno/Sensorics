function generateUIDContent(namespaceId, instanceId) {
    return '<div class="row">'
        + '<div class="col-2">'
        + '<label>Namespace Id</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input type="text" id="uid_namespace_id" value="' + namespaceId + '" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + ' <div class="col-2">'
        + ' <label>Instance Id</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input type="text" id="uid_instance_id" value="' + instanceId + '" class="holo">'
        + '</div>'
        + '</div>';
}

function generateURLContent(url) {
    return '<div class="row">'
        + '<div class="col-2">'
        + '<label>URL</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input type="text" id="url" value="' + url + '" class="holo">'
        + '</div>'
        + '</div>';
}

function generateIBeaconContent(uuid, major, minor) {
    return '<div class="row">'
        + '<div class="col-2">'
        + '<label>UUID</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input style="width: 100%;" type="text" id="ibeacon_uuid" value="' + uuid + '" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-2">'
        + '<label>Major</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input style="width: 100%;" type="number" min="0" id="ibeacon_major" value="' + major + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-2">'
        + '<label>UUID</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input style="width: 100%;" type="number" min="0" id="ibeacon_minor" value="' + minor + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">'
        + '</div>'
        + '</div>';
}

function generateCustomContent(customValue, isHexModeOn) {
    return '<div class="row">'
        + '<div class="col-12">'
        + '<label>Custom</label>'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-2">'
        + '<label>Custom Value</label>'
        + '</div>'
        + '<div class="col-10">'
        + '<input type="text" id="custom_value" value="' + customValue + '" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-2">'
        + '<label>Hex Mode</label>'
        + '</div>'
        + '<div align="right" class="col-10">'
        + '<input id="custom_hex_enabled" type="checkbox" data-style="ios" ' + (isHexModeOn ? 'checked' : '') + ' data-toggle="toggle">'
        + '</div>'
        + '</div>';
}

function generateFrameTypeMenuItem(name) {
    return '<li><a class="dropdown-item" id="frame_type_' + name.toLowerCase() + '" href="#">' + name + '</a></li>'
}

function onlyPositiveNumberFilter(event) {
    return (event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0
}