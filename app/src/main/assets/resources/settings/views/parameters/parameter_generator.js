class HtmlGenerator {
    static generateParameterInteger(id, name, value, index) {
        return '<div class="form-group">' +
            '<label for="formControlRange">' + name + '</label>' +
            '<input type="range" class="form-control-range" id="formControlRange">' +
            '</div>'
    }

    static generateParameterBool(id, name, value, index) {
        let boolValue = JSON.parse(value);
        let checked = boolValue ? " checked" : "";

        return '<div class="form-group">' +
            '<label>' + name + '</label>' +
            '<div class="custom-control custom-switch">' +
            '<input type="checkbox" class="custom-control-input" id="toggle-' + id + '"' + checked + ' onClick="HtmlActions.onSwitchChanged('+ id+ ', ' + index + ', this.checked)">' +
            '<label class="custom-control-label" for="toggle-' + id + '">' + name + '</label>' +
            '</div>' +
            '</div>'
    }

    static generateParameterEditText(id, name, value, writable, index) {
        let disabledText = "";
        if (!writable) disabledText = "disabled=disabled";

        return '<div class="form-group">' +
            '<label for="txt-' + id + '">' + name + '</label>' +
            '<input type="text" onkeyup="HtmlActions.onTextKeyUp(' + id + ', this.value, ' + index + ')" value="' + value + '" id="txt-' + id + '" class="holo" ' + disabledText + ' />' +
            '</div>'
    }

    static generateNumberEditText(id, name, value, min, max, writable, index) {
        let disabledText = "";
        if (!writable) disabledText = "disabled=disabled";

        return '<div class="form-group">' +
            '<label for="txt-num-' + id + '">' + name + '</label>' +
            '<input type="number" min="' + min + '" max="' + max + '" value="' + value + '" id="txt-num-' + id + '" onkeyup="HtmlActions.keyUpNumText(' + id + ', this.value, ' + index + ')" class="holo" ' + disabledText + ' />' +
            '<input type="range" min="' + min + '" max="' + max + '" value="' + value + '" id="rng-' + id + '" onchange="HtmlActions.changedNumRange(' + id + ',this.value, ' + index + ')" class="custom-range" name="range" ' + disabledText + ' >' +
            '</div>'
    }

    static generateEnums(elements, id, name, value, index) {
        let body = HtmlGenerator.generateEnumsBody(id, elements, index)
        let html = '<div class="form-group">'
        html += '<label for="ddl-lbl-' + id + '">' + name + '</label>'
        html += '<div id="ddl-' + id + '" class="dropdown">';
        html += '<button class="btn btn-secondary dropdown-toggle" type="button" id="ddl-menu-button-' + id + '"  data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">';
        html += elements[value];
        html += "</button>";
        html += body;
        html += '</div>';
        html += '</div>';
        return html;
    }

    static generateEnumsBody(id, elements, index) {
        var html = '<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">';
        elements.forEach((element, position) => {
            console.log(position);
            html += HtmlGenerator.generateSingleEnum(id, element, position, index);
        });
        html += '</div>';
        return html
    }

    static generateSingleEnum(id, element, position, index) {
        return '<a onclick="HtmlActions.dropDownChanged(' + id + ', \'' + element + '\', ' + position + ' ,' + index + ')" class="dropdown-item" >' + element + '</a>';
    }
}

class HtmlActions {

    static changedNumRange(id, currentVal, index) {
        $(`#txt-num-${id}`).val(currentVal);
        native.setTextNumber(id, parseInt(currentVal), index)
    }

    static keyUpNumText(id, currentVal, index) {
        if (currentVal == "") {
            $(`#txt-num-${id}`).val(0);
            $(`#rng-${id}`).val(0);
            return;
        }

        if (currentVal[0] == "0") {
            currentVal = currentVal.substr(1);
            $(`#txt-num-${id}`).val(currentVal);
        }

        $(`#rng-${id}`).val(currentVal);
        native.setTextNumber(id, parseInt(currentVal), index)
    }

    static dropDownChanged(id, text, position, index) {
        $(`#ddl-menu-button-${id}`).html(text)
        native.setDropDown(id, text, position, index)
    }

    static onTextKeyUp(id, text, index) {
        console.log(text);
        native.setTextEdit(id, text, index)
    }

    static onSwitchChanged(id, index, value) {
        console.log(value);
        native.onSwitchChanged(id, index, value);
    }

}

