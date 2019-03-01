function generateKeyValue(key, value) {
    return '<div style="margin-top: 1em;" class="card card-body">'
        + '<label id="arbitrary_item_key">' + key + '</label>'
        + '<input type="text"  class="holo" style="width: 100%; margin-top: 0.5em;" id="arbitrary_item_value" value="' + value + '" class="form-control">'
        + '<button id="btn-cancel" type="button" style="width: 100%; margin-top: 1em;" class="btn btn-danger">Delete</button>'
        + '</div>'
}