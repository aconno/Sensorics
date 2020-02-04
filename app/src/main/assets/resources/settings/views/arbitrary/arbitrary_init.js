let beacon;

$(document).ready(function() {
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

        $('#container_arbitrary').append(
            generateKeyValue(key, value)
        );

        //TriggerUpdate
        updatedArbitraryDatas()
        native.onDataChanged(JSON.stringify(beacon));
        $('#arbitrary_modal').modal('toggle');
        $('#modal_key').val("");
        $('#modal_value').val("");
    });
});

function init(beaconInfo) {
    beacon = JSON.parse(beaconInfo);

    Object.keys(beacon.arbitraryData).forEach((value, key) => {
        $('#container_arbitrary').append(
            generateKeyValue(value, key)
        );
    });
}

function removeArbitraryDataView(btn) {
    ((btn.parentNode).parentNode).removeChild(btn.parentNode);

    //Trigger Update
    updatedArbitraryDatas()
    native.onDataChanged(JSON.stringify(beacon));
}

function updatedArbitraryDatas() {
    let arbitraryDatas = new Map();

    let container = $('#container_arbitrary');
    for (let index = 0; index < container.children().length; index++) {
        let child = container.children().eq(index);
        let key = child.find("#arbitrary_item_key").text().trim();
        let value = child.find("#arbitrary_item_value").val();
        arbitraryDatas[key] = value;
    }
    beacon.arbitraryData = arbitraryDatas;
}