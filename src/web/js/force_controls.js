var showAllLinkText;

// makes sliders to control force layout properties
function makeGUI(boundingDiv, force){
    var properties = [ "charge", "linkStrength", "gravity", "friction"];
    var guiDiv = $("<div>").addClass("petri-controls");
    boundingDiv.append(guiDiv);
    properties.forEach(function (property){
        var id = property;
        var control = $("<div>").addClass("control");
        guiDiv.append(control);

        var slider = $("<input>")
            .attr("type", "range")
            .attr("id", id)
            .attr("name", id)
            .change(function (event){
                changeForceAttr(event, this, force);
            })
            .attr("min", function(){
                if (this.name === "charge"){
                    return -10000;
                } else {
                    return 0;
                }
            })
            .attr("max", function (){
                if (this.name === "charge"){
                    return 0;
                } else {
                    return 1;
                }
            })
            .attr("step", function(){
                if (this.name === "charge"){
                    return 1;
                } else {
                    return 0.05;
                }
            });

        // set default values
        slider.attr("value", function(){
            if (this.name === "charge"){
                // invert value
                return $(this).attr("min") - force.charge();
            } else if (this.name === "linkStrength"){
                return force.linkStrength();
            } else if (this.name === "gravity"){
                return force.gravity();
            } else if (this.name === "friction"){
                return $(this).attr("min") - force.friction();
            }
        });

        var label = $("<label>")
            .attr("for", id)
            .html(property);

        control.append(slider);
        control.append(label);
    });


    //var guiB = $("<div>").addClass("option");
    var con = $("<div>").addClass("option");
    guiDiv.append(con);

    var button = $("<input>")
        .attr("type", "button")
        .attr("name", "updateButton")
        .attr("value", "Update")
        .attr("onclick","updateData()")
    con.append(button);   
}

function updateData(){
    showAllLinkText = !showAllLinkText;
    console.log(showAllLinkText);
}

function changeForceAttr(event, elem, force){
    var name = $(elem).attr("name");
    var val = $(elem).val();

    // link slider values to force layout properties
    // some values are inverted for intuitiveness
    if (name === "charge"){
        // invert value
        force.charge($(elem).attr("min") - val);
    } else if (name == "linkStrength"){
        force.linkStrength(val);
    } else if (name == "gravity"){
        force.gravity(val);
    } else if (name == "friction"){
        force.friction($(elem).attr("max") - val);
    }
    force.start();
}

// var toggle;

// var canvas = d3.select("body")
//                 .append("svg")
//                 .attr("width",1050)
//                 .attr("height",700);
// var backText= canvas.append("text")
//     .attr("width",100)
//     .attr("height",100)
//     .attr("x", 0)
//     .attr("y",200)
//     .attr("fill", "black")
//     .text("Toggle")
//     .on("click", function() {
//         toggle = !toggle;
//     });
