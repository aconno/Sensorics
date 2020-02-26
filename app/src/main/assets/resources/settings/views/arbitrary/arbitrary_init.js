//let beacon;


function onArbitraryDataDocumentLoaded() {
    $('#container_arbitrary').on("click", ".btn-danger", function() {
        removeArbitraryDataView(this);
    });

    $('#modal_okay_btn').click(function() {
        let key = $('#modal_key').val().trim();
        let value = $('#modal_value').val().trim();

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
        $('#arbitrary_modal').modal('toggle');
        $('#modal_key').val("");
        $('#modal_value').val("");

    });

    $('#arbitrary_modal').on('shown.bs.modal', function () {
      $('#modal_key').focus()
    })

}

function initArbitraryData(beaconInfo) {
    beacon = JSON.parse(beaconInfo);

    $('#arbitrary-bytes-abailable').text(beacon.arbitraryData.available)
    $('#container_arbitrary').empty();

    for (let [key, value] of Object.entries(beacon.arbitraryData.arbitraryDataEntries)) {
        $('#container_arbitrary').append(
            generateKeyValue(key, value)
        );

    }
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

    let container = $('#container_arbitrary');
    for (let index = 0; index < container.children().length; index++) {
        let child = container.children().eq(index);
        let key = child.find("#arbitrary_item_key").text().trim();
        let value = child.find("#arbitrary_item_value").val();
        arbitraryDataEntries[key] = value;
    }
    beacon.arbitraryData.arbitraryDataEntries = arbitraryDataEntries;
}
