var canvas;
var ctx;
var imageData;
var data;
var width;
var height;

var payload = {
                    "camera": {
                      "origin": [
                        0,
                        0,
                        8
                      ],
                      "direction": [
                        0.1,
                        0,
                        -1
                      ],
                      "fov": 45
                    },
                    "ambientLight": [
                      0.1,
                      0.1,
                      0.1
                    ],
                    "backgroundColour": [
                      0.05,
                      0.05,
                      0.05
                    ],
                    "lights": [
                      {
                        "colour": [
                          1,
                          1,
                          1
                        ],
                        "position": [
                          4,
                          10,
                          8
                        ]
                      }
                    ],
                    "spheres": [
                      {
                        "position": [
                          0,
                          -1.72,
                          0
                        ],
                        "radius": 0.5,
                        "material": {
                          "diffuseColour": [
                            1,
                            0,
                            0
                          ],
                          "shininess": 1
                        }
                      },
                      {
                        "position": [
                          1.1,
                          0,
                          0
                        ],
                        "radius": 0.5,
                        "material": {
                          "diffuseColour": [
                            0,
                            1,
                            0
                          ],
                          "shininess": 1
                        }
                      },
                      {
                        "position": [
                          -1.1,
                          0,
                          0
                        ],
                        "radius": 0.5,
                        "material": {
                          "diffuseColour": [
                            0,
                            0,
                            1
                          ],
                          "shininess": 1
                        }
                      },
                      {
                        "position": [
                          0,
                          -103,
                          0
                        ],
                        "radius": 100,
                        "material": {
                          "diffuseColour": [
                            1,
                            1,
                            1
                          ],
                          "shininess": 1
                        }
                      }
                    ],
                    "planes": []
                  };

function fill() {
    ctx.fillStyle = "rgba(1.0, 1.0, 1.0, 1.0)";
    ctx.fillRect(0, 0, width, height);
}

function render() {
    $('#renderButton').prop("disabled", true);

    var antialiasing = $('#antialiasing').val().substring(0, 1);

    $.ajax({
        url: "api/render?width=" + width + "&height=" + height + "&antialiasing=" + antialiasing,
        type: "POST",
        data: $('#source').val(),
        contentType: "application/json",
        dataType: "json",
        success: function(input) {
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    var sourceIndex = x + (width * y);
                    var current = input[sourceIndex];
                    var r = Math.round(current[0] * 255);
                    var g = Math.round(current[1] * 255);
                    var b = Math.round(current[2] * 255);
                    var a = 255;

                    var targetIndex = sourceIndex * 4;
                    data[targetIndex    ] = r;
                    data[targetIndex + 1] = g;
                    data[targetIndex + 2] = b;
                    data[targetIndex + 3] = a;
                }
            }
            ctx.putImageData(imageData, 0, 0);

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

function onLoad() {
    $('#source').val(JSON.stringify(payload, null, '  '));

    canvas = $('#myCanvas').get(0);

    var container = $(canvas).parent();
    width = container.width();
    height = container.height();
    canvas.setAttribute('width', width);
    canvas.setAttribute('height', height);

    ctx = canvas.getContext("2d");
    imageData = ctx.getImageData(0, 0, width, height);
    data = imageData.data;
    fill();
}