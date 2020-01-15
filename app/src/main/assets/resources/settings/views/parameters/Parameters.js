class HtmlGenerator {
    static generateParameterInteger(id, name, value, index) {
        return '<div class="form-group">' +
            '<label for="formControlRange">' + name + '</label>' +
            '<input type="range" class="form-control-range" id="formControlRange">' +
            '</div>'
    }

    static generateParameterBool(id, name, value, writable, index) {
        let boolValue = JSON.parse(value);
        let checked = boolValue ? " checked" : "";

        return '<div class="form-group">' +
            '<label>' + name + '</label>' +
            '<div class="custom-control custom-switch">' +
            '<input type="checkbox" class="custom-control-input" id="toggle-' + id + '"' + checked + ' onClick="HtmlActions.onSwitchChanged(\''+ id+ '\', ' + index + ', this.checked)" '+ (writable?'':'disabled') + '>' +
            '<label class="custom-control-label" for="toggle-' + id + '">' + name + '</label>' +
            '</div>' +
            '</div>'
    }

    static generateParameterEditText(id, name, value, writable,maxTextLength, index) {
        let disabledText = "";
        if (!writable) disabledText = "disabled=disabled";

        return '<div class="form-group">' +
            '<label for="txt-' + id + '">' + name + '</label>' +
            '<input type="text" maxlength="'+maxTextLength+'"  onkeyup="HtmlActions.onTextKeyUp(\'' + id + '\', this.value, ' + index + ')" value="' + value + '" id="txt-' + id + '" class="holo" ' + disabledText + ' />' +
            '</div>'
    }

    static generateNumberEditText(id, name, value, unit, min, max, writable, index) {
        let disabledText = "";
        if (!writable) disabledText = "disabled";
        let unitText = unit.length > 0 ? " ["+unit+"]" : "";
        return '<div class="form-group">' +
            '<label for="txt-num-' + id + '">' + name + unitText + '</label>' +
            '<input type="number" min="' + min + '" max="' + max + '" value="' + value + '" id="txt-num-' + id + '" onkeyup="HtmlActions.keyUpNumText(\'' + id + '\', this.value, ' + index + ')" class="holo" ' + disabledText + ' />' +
            '<input type="range" min="' + min + '" max="' + max + '" value="' + value + '" id="rng-' + id + '" onchange="HtmlActions.changedNumRange(\'' + id + '\',this.value, ' + index + ')" class="custom-range" name="range" ' + disabledText + ' >' +
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



class ParametersLoader {


    static convertNameToIdFormat(name) {
        let parts = name.split(" ");
        let result = ""
        for(var i in parts) {
            if(i>0) {
                result += "-";
            }
            var part = parts[i].toLowerCase();
            for (var j = 0; j < part.length; j++) {
                let ch = part.charAt(j);
                if(ch >= 'a' && ch <= 'z') {
                    result += ch;
                }
            }
        }
        console.log("CONVERTING NAME TO ID FORMAT: "+name+"->"+result);
        return result;
    }

    static TYPE_PARAMETER_BOOLEAN = 1;
    static TYPE_PARAMETER_NUMBER = 2;
    static TYPE_PARAMETER_ENUM = 3;
    static TYPE_PARAMETER_TEXT = 4;

    /*
    Beacon parameter types and their identifiers:
    0 -> BOOLEAN
    1 -> UINT8
    2 -> UINT16
    3 -> UINT32
    4 -> SINT8
    5 -> SINT16
    6 -> SINT32
    7 -> FLOAT
    8 -> ENUM
    9 -> UTF8STRING
    */
    static convertParameterTypeToTypeGroup(type) {
        if(type == 0) return this.TYPE_PARAMETER_BOOLEAN;
        if(type > 0 && type < 8) return this.TYPE_PARAMETER_NUMBER;
        if(type == 8) return this.TYPE_PARAMETER_ENUM;
        return this.TYPE_PARAMETER_TEXT;
    }

    static insertParametersIntoContainer(parameters,containerId,maxTextLength) {
        let container = "#"+containerId;
        $(container).append("<br/>");
        for(let index = 0;index<parameters.length;index++){
            let parameter = parameters[index];
            let type = this.convertParameterTypeToTypeGroup(parameter.type);
            let id = this.convertNameToIdFormat(parameter.name);

            if(type == this.TYPE_PARAMETER_BOOLEAN){
                let param = HtmlGenerator.generateParameterBool(id, parameter.name, parameter.value, parameter.writable, index);
                console.log("HTML PARAM FOR BOOLEAN: "+param);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }
            if(type == this.TYPE_PARAMETER_TEXT){
                let param = HtmlGenerator.generateParameterEditText(id, parameter.name, parameter.value, parameter.writable,maxTextLength, index);
                console.log("HTML PARAM FOR TEXT: "+param);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }
            if(type == this.TYPE_PARAMETER_NUMBER){
                let param = HtmlGenerator.generateNumberEditText(id, parameter.name, parameter.value, parameter.unit, parameter.min, parameter.max, parameter.writable, index);
                console.log("HTML PARAM FOR NUMBER: "+param);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }
            if(type == this.TYPE_PARAMETER_ENUM){
                // Need to know what choices are for enum
                let param = HtmlGenerator.generateParameterEditText(id, parameter.name, parameter.value, parameter.writable, index);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }

        }


    }

    static setBeaconParameters(beaconString) {
        let beacon = JSON.parse(beaconString);

        let maxValueSize = beacon.parameters.config.maxValueSize;

        $("#parameters-container").empty();
        for(var paramGroup in beacon.parameters.parameters) {
            let containerId = this.convertNameToIdFormat(paramGroup);
            $("#parameters-container").append(
                '<br/>'+
                '<div class="card-view" id="'+containerId+'">'+
                '<h5>'+paramGroup+'</h5>'+
                '</div>'
            );

            this.insertParametersIntoContainer(beacon.parameters.parameters[paramGroup],containerId,maxValueSize);
        }


    }

}
