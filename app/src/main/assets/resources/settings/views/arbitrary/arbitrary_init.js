//let beacon;


function onArbitraryDataDocumentLoaded() {
    $('#container-arbitrary').on("click", ".btn-danger", function() {
        removeArbitraryDataView(this);
    });

    $('#modal-okay-btn').click(function() {
        let key = $('#modal-key').val().trim();
        let value = $('#modal-value').val().trim();

        if (!key) {
            alert("Please Enter a Key");
            return;
        }

        if (!value) {
            alert("Please Enter a Value");
            return;
        }

        beacon.arbitraryData.arbitraryDataEntries[key]=value;
        native.onDataChanged(JSON.stringify(beacon));
        $('#arbitrary-modal').modal('toggle');
        $('#modal-key').val("");
        $('#modal-value').val("");

    });

    $('#arbitrary-modal').on('shown.bs.modal', function () {
      $('#modal-key').focus()
    })

}

function initArbitraryData(beaconInfo) {
    beacon = JSON.parse(beaconInfo);

    $('#arbitrary-bytes-abailable').text(beacon.arbitraryData.available)
    $('#container-arbitrary').empty();

    for (let [key, value] of Object.entries(beacon.arbitraryData.arbitraryDataEntries)) {
        $('#container-arbitrary').append(
            generateKeyValue(key, value)
        );

    }

    $('.arbitrary-item-value').on('change', function () {
        let key = $(this).data("key");
        onValueUpdate(key,$(this).val());
    });
}

function onValueUpdate(key,value,source) {
     beacon.arbitraryData.arbitraryDataEntries[key]=value;
     native.onDataChanged(JSON.stringify(beacon));
}

function removeArbitraryDataView(btn) {
    ((btn.parentNode).parentNode).removeChild(btn.parentNode);

    //Trigger Update
    updatedArbitraryDatas()
    native.onDataChanged(JSON.stringify(beacon));
}

function updatedArbitraryDatas() {
    let arbitraryDataEntries = new Map();

    let container = $('#container-arbitrary');
    for (let index = 0; index < container.children().length; index++) {
        let child = container.children().eq(index);
        let key = child.find("#arbitrary-item-key").text().trim();
        let value = child.find("#arbitrary-item-value-"+key).val();
        arbitraryDataEntries[key] = value;
    }
    beacon.arbitraryData.arbitraryDataEntries = arbitraryDataEntries;
}
