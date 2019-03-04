function generateUIDContent(namespaceId, instanceId) {
    return '<div class="row">'
        + '<div class="col-2">'
        + '<label>Namespace Id</label>'
        + '</div>'
        + '<div class="col-9">'
        + '<input type="text" style="width: 100%;" id="uid_namespace_id" value="' + namespaceId + '" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + ' <div class="col-3">'
        + ' <label>Instance Id</label>'
        + '</div>'
        + '<div class="col-9">'
        + '<input type="text" style="width: 100%;" id="uid_instance_id" value="' + instanceId + '" class="holo">'
        + '</div>'
        + '</div>';
}

function generateURLContent(url) {
    return '<div class="row">'
        + '<div class="col-3">'
        + '<label>URL</label>'
        + '</div>'
        + '<div class="col-9">'
        + '<input style="width: 100%;" type="text" id="url" value="' + url + '" class="holo">'
        + '</div>'
        + '</div>';
}

function generateIBeaconContent(uuid, major, minor) {
    return '<div class="row">'
        + '<div class="col-3">'
        + '<label>UUID</label>'
        + '</div>'
        + '<div class="col-9">'
        + '<input style="width: 100%;" type="text" id="ibeacon_uuid" value="' + uuid + '" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-3">'
        + '<label>Major</label>'
        + '</div>'
        + '<div class="col-9">'
        + '<input style="width: 100%;" type="number" min="0" id="ibeacon_major" value="' + major + '" onkeypress="return onlyPositiveNumberFilter(event);" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-3">'
        + '<label>Minor</label>'
        + '</div>'
        + '<div class="col-9">'
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
        + '<div class="col-3">'
        + '<label>Custom Value</label>'
        + '</div>'
        + '<div class="col-9">'
        + '<input style="width: 100%;" type="text" id="custom_value" value="' + customValue + '" class="holo">'
        + '</div>'
        + '</div>'
        + '<div class="row" style="margin-top: 1em;">'
        + '<div class="col-3">'
        + '<label>Hex Mode</label>'
        + '</div>'
        + '<div align="right" class="col-9">'
        + '<div class="custom-control custom-switch">'
        + '<input id="custom_hex_enabled" ' + (isHexModeOn ? 'checked' : '') + ' class="custom-control-input" type="checkbox">'
        + '<label class="custom-control-label" for="custom_hex_enabled"></label>'
        + '</div>'
        + '</div>'
        + '</div>';
}

function generateFrameTypeMenuItem(name) {
    return '<li><a class="dropdown-item" id="frame_type_' + name.toLowerCase() + '" href="#">' + name + '</a></li>'
}

function onlyPositiveNumberFilter(event) {
    return (event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0
}