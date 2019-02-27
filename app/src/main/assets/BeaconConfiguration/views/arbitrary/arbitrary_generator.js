function generateKeyValue(key, value) {
    return '<div id="container_arbitraty" style="margin-top: 1em;" class="card card-body">'
        + '<label>' + key + '</label>'
        + '<input type="text" style="width: 100%; margin-top: 1em;" id="uid_instance_id" value="' + value + '" class="form-control">'
        + '<button type="button" style="width: 100%; margin-top: 1em;" class="btn btn-danger">Delete</button>'
        + '</div>'
}