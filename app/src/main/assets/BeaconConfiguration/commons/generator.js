class HtmlGenerator {
    static generateParameterInteger(id, name, value) {
        return '<div class="form-group">' +
            '<label for="formControlRange">' + name + '</label>' +
            '<input type="range" class="form-control-range" id="formControlRange">' +
            '</div>'
    }

    static generateParameterBool(id, name, value) {
        return '<div class="form-group">' +
            '<label>' + name + '</label>' +
            '<div class="custom-control custom-switch">' +
            '<input type="checkbox" class="custom-control-input" id="toggle-' + id + '">' +
            '<label class="custom-control-label" for="toggle-' + id + '">' + name + '</label>' +
            '</div>' +
            '</div>'
    }

    static generateParamenterEditText(id, name, value) {
        return '<div class="form-group">' +
            '<label for="txt-' + id + '">' + name + '</label>' +
            '<input type="text" value="' + value + '" id="txt-' + id + '" class="holo" />' +
            '</div>'
    }

    static generateNumberEditText(id, name, value) {
        return '<div class="form-group">' +
            '<label for="txt-' + id + '">' + name + '</label>' +
            '<input type="number" value="' + value + '" id="txt-' + id + '" class="holo" />' +
            '</div>'
    }

    static generateEnums(elements, id, labelName) {
        let body = HtmlGenerator.generateEnumsBody(elements)
        let html = '<div class="form-group">'
        html += '<label for="ddl-' + id + '">' + labelName + '</label>'
        html += '<div id="ddl-' + id + '" class="dropdown">';
        html += '<button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">';
        html += "Drop Down element";
        html += "</button>";
        html += body;
        html += '</div>';
        html += '</div>';
        return html;
    }

    static generateEnumsBody(elements) {
        var html = '<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">';
        elements.forEach(element => {
            html += HtmlGenerator.generateSingleEnum(element);
        });
        html += '</div>';
        return html
    }

    static generateSingleEnum(element) {
        let id = element.id;
        let name = element.name;
        let value = element.value;
        return '<a class="dropdown-item" href="#">' + name + '</a>';
    }

    
}

function getElementArray() {
    let elements =  Array();
    let first = {
        id: 1,
        name: "first",
        value: 1
    }
    let second = {
        id: 2,
        name: "second",
        value: 2
    }
    let third = {
        id: 3,
        name: "third",
        value: 3
    }
    elements.push(first, second, third);
    return elements;
}

$(document).ready(function () {
    $(".container").append(HtmlGenerator.generateParameterBool("2", "Title Bool ", ""));
    $(".container").append("<br/>");
    $(".container").append(HtmlGenerator.generateParamenterEditText("2", "Title Simple Text", ""));
    $(".container").append("<br/>");
    $(".container").append(HtmlGenerator.generateNumberEditText("2", "Title Numeric Text", ""));
    $(".container").append("<br/>");
    $(".container").append(HtmlGenerator.generateEnums(getElementArray(), "1", "Title Dropdown"));
    $(".container").append("<br/>");
    $(".container").append(HtmlGenerator.generateParameterInteger("2", "Title of integer", ""));
    $(".container").append("<br/>");
});