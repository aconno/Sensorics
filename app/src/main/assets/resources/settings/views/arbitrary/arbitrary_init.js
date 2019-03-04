$(document).ready(function () {
    $('#container_arbitrary').on("click", ".btn-danger", function() {
        removeArbitraryDataView(this);
    });

    $('#modal_okay_btn').click(function () {     
        let key = $('#modal_key').val().trim();
        let value = $('#modal_value').val().trim();

        if(!key){
            alert("Please Enter a Key");
            return;
        }

        if(!value){
            alert("Please Enter a Value");
            return;
        }

        $('#container_arbitrary').append(
            generateKeyValue(key, value)
        );

        //TriggerUpdate
        Android.onDataChanged(getUpdatedArbitraryDatas());
        $('#arbitrary_modal').modal('toggle');
        $('#modal_key').val("");
        $('#modal_value').val("");
    });
});

function init(arbiraryDataJsonArray) {
    let arbitraryDatas = JSON.parse(arbiraryDataJsonArray);

    arbitraryDatas.forEach(element => {
        $('#container_arbitrary').append(
            generateKeyValue(element.key, element.value)
        );
    });
}

function removeArbitraryDataView(btn) {
    ((btn.parentNode).parentNode).removeChild(btn.parentNode);

    //Trigger Update
    Android.onDataChanged(getUpdatedArbitraryDatas());
}

function getUpdatedArbitraryDatas() {
    let arbitraryDatas = new Array();

    let container = $('#container_arbitrary');
    for (let index = 0; index < container.children().length; index++) {
        let child = container.children().eq(index);
        let element = new ArbitraryData();
        element.key = child.find("#arbitrary_item_key").text().trim();
        element.value = child.find("#arbitrary_item_value").val();
        arbitraryDatas.push(element);
    }

    return JSON.stringify(arbitraryDatas);
}