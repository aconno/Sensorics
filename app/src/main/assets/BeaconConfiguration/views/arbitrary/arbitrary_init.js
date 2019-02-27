$(document).ready(function () {
    init("a");
});

function init(arbiraryDatas) {
    for (let index = 0; index < 10; index++) {
        $('#container_arbitrary').append(
            generateKeyValue("HAHA" + index, "HOHO")
        );
    }

    $(".btn-danger").click(function () {
        removeArbitraryDataView(this);
    });

    $('#modal_okay_btn').click(function () {     
        let key = $('#modal_key').val().trim();
        let value = $('#modal_value').val().trim();
        $('#container_arbitrary').append(
            generateKeyValue(key, value)
        );
    });
}

function removeArbitraryDataView(btn) {
    ((btn.parentNode).parentNode).removeChild(btn.parentNode);
}