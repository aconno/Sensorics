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

    static generateParameterEditText(id, name, value, writable,maxTextSize, index) {
        let disabledText = "";
        if (!writable) disabledText = "disabled=disabled";

        return '<div class="form-group">' +
            '<label for="txt-' + id + '">' + name + '</label>' +
            '<input type="text" onkeyup="HtmlActions.onTextKeyUp(\'' + id + '\', this.value, ' + index + ',this,'+maxTextSize+')" value="' + value + '" id="txt-' + id + '" class="holo" ' + disabledText + ' />' +
            '</div>'
    }

    static generateNumberEditText(id, name, value, unit, min, max, writable, enableFloatInput, index) {
        let disabledText = "";
        if (!writable) disabledText = "disabled";
        let unitText = unit.length > 0 ? " ["+unit+"]" : "";
        let inputStep = enableFloatInput ? "any" : "1"
        let rangeInputStep = enableFloatInput ? "0.01" : "1"
        let onKeyUpFunctionName = enableFloatInput ? "keyUpFloatText" : "keyUpNumText";
        return '<div class="form-group">' +
            '<label for="txt-num-' + id + '">' + name + unitText + '</label>' +
            '<input type="number" min="' + min + '" max="' + max + '" step="'+inputStep+'" value="' + value + '" id="txt-num-' + id + '" onkeyup="HtmlActions.'+onKeyUpFunctionName+'(\'' + id + '\', this.value, ' + index + ')" class="holo" ' + disabledText + ' />' +
            '<input type="range" min="' + min + '" max="' + max + '" step="'+rangeInputStep+'" value="' + value + '" id="rng-' + id + '" onchange="HtmlActions.changedNumRange(\'' + id + '\',this.value, ' + index + ')" class="custom-range" name="range" ' + disabledText + ' >' +
            '</div>'
    }

    static generateEnums(elements, id, name, value, index) {

        let html = '<div class="form-group">'+
               '<label>' + name + '</label>' +
                '<br><select id="selector-'+id+'">';
        for(var i in elements) {
            html += '<option>'+elements[i]+'</option>';
        }
        html += "</select></div>";
        return html;
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

    static keyUpFloatText(id, currentVal, index) {
        if (currentVal == "") {
            return;
        }

        if (currentVal[0] == "0" && currentVal[1] != "." && currentVal[1]!="," && currentVal.length > 1) {
            currentVal = currentVal.substr(1);
            $(`#txt-num-${id}`).val(currentVal);
        }

        $(`#rng-${id}`).val(currentVal);
    }

    static dropDownChanged(id, text, position, index) {
        $(`#ddl-menu-button-${id}`).html(text)
        //native.setDropDown(id, text, position, index)
    }

    static onTextKeyUp(id, text, index,inputField,maxTextSize) {

        let textSizeBytes = (new TextEncoder().encode(text)).length;
        let caretPos = inputField.selectionStart;
        if(textSizeBytes > maxTextSize) {
            let previousText = text.substring(0,caretPos-1) + text.substring(caretPos);
            inputField.value = previousText;
            inputField.selectionStart = caretPos-1;
            inputField.selectionEnd = caretPos-1;
        }
       // native.setTextEdit(id, text, index)
    }

    static onSwitchChanged(id, index, value) {
        console.log(value);
        //native.onSwitchChanged(id, index, value);
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
    static TYPE_PARAMETER_FLOAT = 5;

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
        if(type > 0 && type < 7) return this.TYPE_PARAMETER_NUMBER;
        if(type == 7) return this.TYPE_PARAMETER_FLOAT;
        if(type == 8) return this.TYPE_PARAMETER_ENUM;
        return this.TYPE_PARAMETER_TEXT;
    }

    static insertParametersIntoContainer(parameters,containerId,maxTextSize) {
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
                let param = HtmlGenerator.generateParameterEditText(id, parameter.name, parameter.value, parameter.writable,maxTextSize, index);
                console.log("HTML PARAM FOR TEXT: "+param);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }
            if(type == this.TYPE_PARAMETER_NUMBER){
                let param = HtmlGenerator.generateNumberEditText(id, parameter.name, parameter.value, parameter.unit, parameter.min, parameter.max, parameter.writable, false, index);
                console.log("HTML PARAM FOR NUMBER: "+param);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }
            if(type == this.TYPE_PARAMETER_FLOAT){
                let param = HtmlGenerator.generateNumberEditText(id, parameter.name, parameter.value, parameter.unit, parameter.min, parameter.max, parameter.writable, true, index);
                console.log("HTML PARAM FOR FLOAT: "+param);
                $(container).append(param);
                $(container).append("<br/>");
                continue;
            }
            if(type == this.TYPE_PARAMETER_ENUM){
                let param = HtmlGenerator.generateEnums(parameter.choices, id, parameter.name, parameter.value, index);
                console.log("HTML PARAM FOR ENUM: "+param);
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

    static sendParametersToBackend(oldBeaconInfo) {
        let beacon = JSON.parse(oldBeaconInfo);
        let params = beacon.parameters.parameters

        //TODO: load params from UI to params list

        native.saveChanges(JSON.stringify(beacon))
    }
}