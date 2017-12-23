function render() {
    $('#renderButton').prop("disabled", true);

    var width = $('#widthInput').val();
    var height = $('#heightInput').val();
    var antialiasing = $('#antialiasingInput').val().substring(0, 1);

    $.ajax({
        url: "api/render?width=" + width + "&height=" + height + "&antialiasing=" + antialiasing,
        type: "POST",
        data: $('#source').val(),
        contentType: "application/json",
        dataType: "text",
        success: function(input) {
            var pngData = "data:image/png;base64," + input;
            $('#renderImage').attr("src", pngData);
            $('#saveButton').attr("href", input);
            $('#renderButton').prop("disabled", false);
        },
        error: function(e) {
            console.log(e);
            $('#renderButton').prop("disabled", false);
        }
    });

}

function save() {
    window.open(canvas.toDataURL("image/png"));
}