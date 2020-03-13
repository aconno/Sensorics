function generateKeyValue(key, value) {
    return '<div style="margin-top: 1em;" class="card card-body" >'
        + '<label id="arbitrary_item_key">' + key + '</label>'
        + '<input type="text"  data-key="'+key+'"  class="holo arbitrary-item-value" style="width: 100%; margin-top: 0.5em;" id="arbitrary-item-value-'+key+'" value="' + value + '" class="form-control">'
        + '<button id="btn-cancel" type="button" style="width: 100%; margin-top: 1em;" class="btn btn-danger">Delete</button>'
        + '</div>'
}
