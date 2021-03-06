"use strict";
// based on d3 force layout example:
// http://bl.ocks.org/mbostock/1153292
(function (self){

    var states, // nodes bound to program states
        links, // links bound to state transitions
        varsChosen = [], // variables to be shown NOT USED YET
        funcsChosen = [], // functions to be shown NOT USED YET
        svg, // the svg element to draw automata on
        currentState, // state hovering over
        text, //text for all links
        boundingDiv, // div that bounds the svg
		node;// reference to nodes to change mouse behaviours

    // force layout
    var force = d3.layout.force()
        .charge(-8000)
        //.linkDistance(150)
        .gravity(1)
        .friction(0.5)
        .linkStrength(0.5);

    var colour = d3.scale.category20(),
        circleRad = 10;

    // for testing
    self.getLinks = function() { return links; }

    // initialise the layout with data
    // boundingdiv is a jquery selection
    self.init = function (dataStr, _boundingDiv){
        var data = JSON.parse(dataStr);

        states = data.states;
        links = data.links;
        boundingDiv = _boundingDiv;
        currentState = data.states[0];

        // creates GUI elements like sliders to change layout properties
        makeGUI(boundingDiv, force);

		//create svg & size
       	var width = boundingDiv.width();
		var height = screen.availHeight -300; //TODO make visualisation scale better

        svg = d3.select("#" + boundingDiv.attr("id")).append("svg")
            .attr("width", width)
            .attr("height", height);

    	force.nodes(states);
        force.links(links);
        force.size([width, height]);

			node = svg.selectAll(".state")
            .data(states)
             .enter().append("g")
            .attr("class", "state")
            .attr("id", function (d, i){
                return "state-" + i;
            })
            .on("mouseenter", selectState)
            .on("mouseout", deselectState)

        node.append("circle")
            .attr("class", "state-circle")
            .attr("id", function (d, i){
                return "state-circle-" + i;
            })
            .attr("r", circleRad)
            .style("fill", function(d,i){
                    if(states[i].startState){
                       return "red";
                    }
                    return "blue";
            })
            // .style("fill", function (d, i){
            //     return colour(i);
            // })

        // build the arrow.
        svg.append("defs")
             .append("marker")
            .attr("id", "end")
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", circleRad + 10)
            .attr("refY", 0)
            .attr("markerWidth", 6)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .attr("stroke","red")
            .attr("fill","red")
             .append("path")
            .attr("d", "M0,-5L10,0L0,5");

        var link = svg.selectAll(".link")
            .data(links)
             .enter().insert("g", ":first-child")
             .attr("class", "link");

        link.append("path")
            .attr("class", "line")
            .attr("id", function (d, i) { return "link-" + d.source + "-" + d.target; })
            .attr("marker-end", "url(#end)")
            .on("end");

        self.showMethodNames();

        force.on("tick", function (){

			node.attr("cx", function(d) { return d.x = Math.max(15, Math.min(width - 15, d.x)); })
    			.attr("cy", function(d) { return d.y = Math.max(15, Math.min(height - 15, d.y)); });

            node.attr("transform", transform);

            // update curved links
            link.select(".line").attr("d", linkArc);
        });

        // updates a curved link
        function linkArc(d) {
            var dx = d.target.x - d.source.x,
                dy = d.target.y - d.source.y,
                dr = Math.sqrt(dx * dx + dy * dy);
                 if ( dx === 0 && dy === 0 ){
                    var xRotation = 0;

                    // Make drx and dry different to get an ellipse instead of a circle.
                    var drx = 30;
                    var dry = 20;

                    return "M" + d.source.x + "," + d.source.y + "A" + drx + "," + dry + " " + xRotation + "," + 1 + "," + 0 + " " + (d.target.x + 1) + "," + (d.target.y + 1);
                }
                return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
        }

        function transform(d) {
            return "translate(" + d.x + "," + d.y + ")";
        }


        node.call(force.drag);
        force.start();
    }

    // adds all func and var names to funcsChosen and varsChosen respectively
    function setChosenNames(){
        links.forEach(function (d){
            funcsChosen.push(d.methodName);
        });

        // adds all variable names (that appear in states) to varsChosen
        states[0].fields.forEach(function (d){
            varsChosen.push(methodName);
        });
    }

    // update the layout data
    self.updateData = function (dataStr) {
        var data = JSON.parse(dataStr);
        states = data.states;
        links = data.links;

        force.nodes(states);
        force.links(links);

        // should start?
        force.start();
    };

     self.showMethodNames = function(){
       text= svg.selectAll(".link")
             .append("text")
            .style("text-anchor", "middle")
             .attr("dy", -5)
             .append("textPath")
            .attr("xlink:href", function(d, i) { return "#link-" + d.source + "-" + d.target; })
            .attr("class", "label")
            .attr("startOffset", "50%")
            .text(function (d) {
                if(showAllLinkText){
                    return d.methodName;
                }
                else{
                    if(currentState.id === d.source || currentState.id === d.target)
                        return d.methodName;
                    else
                        return "";
                }
            });
    };

    self.hideFunctionNames = function (){
        svg.selectAll(".label").remove();
    };

    // return state info as a string
    function stateInfo(state){
        var str = "";
        state.fields.forEach(function (field) {
            str += field.name + ": " + field.value;
            str += "<br>"
        });
        return str;
    }

    function selectState(d){
        if (d === null){
            d = force.nodes()[i];
        }
        // pop out
        d3.select(this).select("circle").transition()
            .attr("r", circleRad*2)
            .ease("cubic-out")
            .duration(200);

        currentState = d;

        d3.select("#state-info")
            .attr("visibility", "visible")
            .html(function() { return stateInfo(d); });

        //changes the text on hover
        text.text(function (d) {
            if(showAllLinkText){
                return d.methodName;
            }
            else{
                if(currentState.id === d.source.id || currentState.id === d.target.id)
                    return d.methodName;
                else
                    return "";
            }
        });
    }

    function deselectState(d){
        if (d === null){
            d = force.nodes()[i];
        }

        d3.select(this).select("circle").transition()
            .attr("r", circleRad)
            .ease("cubic-out")
            .duration(200);

        d3.select("state-info").attr("visibiliy", "hidden");
    }

    //Updates the method names when the button Toggle Link Text
    self.UpdateMethodNames = function(){
        text.text(function (d) {
            if(showAllLinkText){
                return d.methodName;
            }
            else{
                if(currentState.id === d.source || currentState.id === d.target)
                    return d.methodName;
                else
                    return "";
            }
        });
    }

    // return state info as a string
    function stateInfo(state){
        var str = "";
        state.fields.forEach(function (field) {
            str += field.name + ": " + field.value;
            str += "<br>"
        });
    }

    /*
        functions are called if in "both" layout
    */
    self.addMouseEnterListener = function(listener){
        node.on("mouseenter.extra", function(){
            listener(this);
        });
    }

    self.addMouseOutListener = function(listener){
        node.on("mouseout.extra", function(){
            listener(this);
        });
    }

})(viz.automata = viz.automata || {});
