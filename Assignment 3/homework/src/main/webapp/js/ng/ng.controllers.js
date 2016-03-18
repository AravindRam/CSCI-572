angular.module('app.controllers', [])
	.controller('PageViewController', ['$scope', '$route', '$animate', function($scope, $route, $animate) {
	}])
	.controller('HomeworkAppController', ['$scope', function($scope) {
	}])

	.controller('Query1Controller', ['$scope', '$http', function($scope, $http) {
		$scope.today = function() {
			$scope.dt = new Date();
		};
		$scope.today();

		$scope.clear = function () {
			$scope.dt = null;
		};
		$scope.toggleMin = function() {
			$scope.minDate = $scope.minDate ? null : new Date('1970-1-1');
		};
		$scope.toggleMin();

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		$scope.dateOptions = {
			formatYear: 'yy',
			startingDay: 1
		};

		$scope.initDate = new Date();
		$scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
		$scope.format = $scope.formats[0];

		$scope.submit = function() {

			//Clear visualizations before loading them
			$('.timeseries').empty();
			$('.chorddiagram').empty();
			$('.donutdiagram').empty();
			$('#mainBubble').empty();

			// API call
			$http({
				method: 'GET',
				url: 'http://localhost:8080/homework/api/api/query1/getTimeSeries'
			}).then(function successCallback(response) {
				//console.log(JSON.stringify(response));
				//Time Series
				showTimeSeriesResults(response.data);
				//Chord Diagram
				showChordDiagram();
				//3D donut Diagram
				show3dDonutDiagram();
				//Bubble Menu
				var restUrl = "http://localhost:8080/homework/api/api/query1/getBubbleMenu";
				showBubbleMenu(restUrl);

			}, function errorCallback(response) {
				console.log(JSON.stringify(response));
			});

		};

		var showTimeSeriesResults = function(sdata) {
			var data = [];
			//alert(JSON.stringify(sdata));
			for(i=0; i < sdata.length; i++)	{
				data[i] = {'value': new Date(sdata[i]).getTime()}
			}
			var domEl = 'timeseries';
			var brushEnabled = true;
			timeseries(domEl, data, brushEnabled);
		}


		var showChordDiagram = function() {

			var width = 720,
				height = 720,
				outerRadius = Math.min(width, height) / 2 - 10,
				innerRadius = outerRadius - 24;

			var formatPercent = d3.format(".1%");

			var arc = d3.svg.arc()
				.innerRadius(innerRadius)
				.outerRadius(outerRadius);

			var layout = d3.layout.chord()
				.padding(.04)
				.sortSubgroups(d3.descending)
				.sortChords(d3.ascending);

			var path = d3.svg.chord()
				.radius(innerRadius);

			var svg = d3.select(".chorddiagram").append("svg")
				.attr("width", width)
				.attr("height", height)
				.append("g")
				.attr("id", "circle")
				.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

			svg.append("circle")
				.attr("r", outerRadius);

			queue()
				.defer(d3.csv, "cities.csv")
				.defer(d3.json, "matrix.json")
				.await(ready);

			function ready(error, cities, matrix) {
				if (error) throw error;

				// Compute the chord layout.
				layout.matrix(matrix);

				// Add a group per neighborhood.
				var group = svg.selectAll(".group")
					.data(layout.groups)
					.enter().append("g")
					.attr("class", "group")
					.on("mouseover", mouseover);

				// Add a mouseover title.
				group.append("title").text(function(d, i) {
					return cities[i].name + ": " + Math.floor(d.value);
				});

				// Add the group arc.
				var groupPath = group.append("path")
					.attr("id", function(d, i) { return "group" + i; })
					.attr("d", arc)
					.style("fill", function(d, i) { return cities[i].color; });

				// Add a text label.
				var groupText = group.append("text")
					.attr("x", 6)
					.attr("dy", 15);

				groupText.append("textPath")
					.attr("xlink:href", function(d, i) { return "#group" + i; })
					.text(function(d, i) { return cities[i].name; });

				// Remove the labels that don't fit. :(
				groupText.filter(function(d, i) { return groupPath[0][i].getTotalLength() / 2 - 16 < this.getComputedTextLength(); })
					.remove();

				// Add the chords.
				var chord = svg.selectAll(".chord")
					.data(layout.chords)
					.enter().append("path")
					.attr("class", "chord")
					.style("fill", function(d) { return cities[d.source.index].color; })
					.attr("d", path);

				// Add an elaborate mouseover title for each chord.
				chord.append("title").text(function(d) {
					return cities[d.source.index].name
						+ " ? " + cities[d.target.index].name
						+ ": " + d.source.value
						+ "\n" + cities[d.target.index].name
						+ " ? " + cities[d.source.index].name
						+ ": " + d.target.value;
				});

				function mouseover(d, i) {
					chord.classed("fade", function(p) {
						return p.source.index != i
							&& p.target.index != i;
					});
				}
			}

		}

		//3d donut
		var shotgunData = [
			{"label":"IN","value":345,"color":"#3366CC"},
			{"label":"WI","value":379,"color":"#DC3912"},
			{"label":"OH","value":666,"color":"#FF9900"},
			{"label":"NC","value":417,"color":"#109618"},
			{"label":"GA","value":185,"color":"#990099"},
			{"label":"FL","value":334,"color":"#FF357C"},
			{"label":"CO","value":185,"color":"#FA6102"},
			{"label":"CA","value":174,"color":"#39DD18"},
			{"label":"WA","value":232,"color":"#E89D51"},
			{"label":"TX","value":266,"color":"#A7D2D6"},
			{"label":"TN","value":328,"color":"#F5DF17"},
			{"label":"OK","value":486,"color":"#F44E24"},
			{"label":"KS","value":171,"color":"#D10006"},
			{"label":"MO","value":284,"color":"#D548D7"}
		];

		var riflesData = [
			{"label":"IN","value":1076,"color":"#3366CC"},
			{"label":"WI","value":900,"color":"#DC3912"},
			{"label":"OH","value":1880,"color":"#FF9900"},
			{"label":"NC","value":1498,"color":"#109618"},
			{"label":"GA","value":764,"color":"#990099"},
			{"label":"FL","value":1342,"color":"#FF357C"},
			{"label":"CO","value":1164,"color":"#FA6102"},
			{"label":"CA","value":683,"color":"#39DD18"},
			{"label":"WA","value":959,"color":"#E89D51"},
			{"label":"TX","value":1094,"color":"#A7D2D6"},
			{"label":"TN","value":1178,"color":"#F5DF17"},
			{"label":"OK","value":1467,"color":"#F44E24"},
			{"label":"KS","value":581,"color":"#D10006"},
			{"label":"MO","value":945,"color":"#D548D7"}
		];

		var handgunsData = [
			{"label":"IN","value":1941,"color":"#3366CC"},
			{"label":"WI","value":966,"color":"#DC3912"},
			{"label":"OH","value":3411,"color":"#FF9900"},
			{"label":"NC","value":2264,"color":"#109618"},
			{"label":"GA","value":975,"color":"#990099"},
			{"label":"FL","value":2268,"color":"#FF357C"},
			{"label":"CO","value":1812,"color":"#FA6102"},
			{"label":"CA","value":1009,"color":"#39DD18"},
			{"label":"WA","value":1026,"color":"#E89D51"},
			{"label":"TX","value":1600,"color":"#A7D2D6"},
			{"label":"TN","value":2037,"color":"#F5DF17"},
			{"label":"OK","value":2256,"color":"#F44E24"},
			{"label":"KS","value":852,"color":"#D10006"},
			{"label":"MO","value":1434,"color":"#D548D7"}
		];


		$scope.shotguns = function(){
			Donut3D.transition("gunsDonut", shotgunData, 300, 250, 40, 0.4);
		};

		$scope.rifles = function(){
			Donut3D.transition("gunsDonut", riflesData, 300, 250, 40, 0.4);
		};

		$scope.handguns = function() {
			Donut3D.transition("gunsDonut", handgunsData, 300, 250, 40, 0.4);
		};

		var show3dDonutDiagram = function() {
			var svg = d3.select(".donutdiagram").append("svg").attr("width",1200).attr("height",600);
			svg.append("g").attr("id","gunsDonut");
			Donut3D.draw("gunsDonut", shotgunData, 500, 275, 300, 250, 40, 0.4);
		};


		var showBubbleMenu = function(restUrl) {

			var w = window.innerWidth*0.68*0.95;
			var h = Math.ceil(w*0.5);
			var oR = 0;
			var nTop = 0;

			var svgContainer = d3.select("#mainBubble")
				.style("height", h+"px");

			var svg = d3.select("#mainBubble").append("svg")
				.attr("class", "mainBubbleSVG")
				.attr("width", w)
				.attr("height",h)
				.on("mouseleave", function() {return resetBubbles();});

			var mainNote = svg.append("text")
				.attr("id", "bubbleItemNote")
				.attr("x", 10)
				.attr("y", w/2-15)
				.attr("font-size", 12)
				.attr("dominant-baseline", "middle")
				.attr("alignment-baseline", "middle")
				.style("fill", "#888888")


			d3.json(restUrl, function(error, root) {
				console.log(error);

				var bubbleObj = svg.selectAll(".topBubble")
					.data(root.children)
					.enter().append("g")
					.attr("id", function(d,i) {return "topBubbleAndText_" + i});

				console.log(root);
				nTop = root.children.length;
				oR = w/(1+3*nTop);

				h = Math.ceil(w/nTop*2);
				svgContainer.style("height",h+"px");

				var colVals = d3.scale.category10();

				bubbleObj.append("circle")
					.attr("class", "topBubble")
					.attr("id", function(d,i) {return "topBubble" + i;})
					.attr("r", function(d) { return oR; })
					.attr("cx", function(d, i) {return oR*(3*(1+i)-1);})
					.attr("cy", (h+oR)/3)
					.style("fill", function(d,i) { return colVals(i); }) // #1f77b4
					.style("opacity",0.3)
					.on("mouseover", function(d,i) {return activateBubble(d,i);});


				bubbleObj.append("text")
					.attr("class", "topBubbleText")
					.attr("x", function(d, i) {return oR*(3*(1+i)-1);})
					.attr("y", (h+oR)/3)
					.style("fill", function(d,i) { return colVals(i); }) // #1f77b4
					.attr("font-size", 30)
					.attr("text-anchor", "middle")
					.attr("dominant-baseline", "middle")
					.attr("alignment-baseline", "middle")
					.text(function(d) {return d.name})
					.on("mouseover", function(d,i) {return activateBubble(d,i);});


				for(var iB = 0; iB < nTop; iB++)
				{
					var childBubbles = svg.selectAll(".childBubble" + iB)
						.data(root.children[iB].children)
						.enter().append("g");

					//var nSubBubble = Math.floor(root.children[iB].children.length/2.0);

					childBubbles.append("circle")
						.attr("class", "childBubble" + iB)
						.attr("id", function(d,i) {return "childBubble_" + iB + "sub_" + i;})
						.attr("r",  function(d) {return oR/3.0;})
						.attr("cx", function(d,i) {return (oR*(3*(iB+1)-1) + oR*1.5*Math.cos((i-1)*45/180*Math.PI));})
						.attr("cy", function(d,i) {return ((h+oR)/3 +        oR*1.5*Math.sin((i-1)*45/180*Math.PI));})
						.attr("cursor","pointer")
						.style("opacity",0.5)
						.style("fill", "#eee")
						.on("click", function(d,i) {
							window.open(d.address);
						})
						.on("mouseover", function(d,i) {
							//window.alert("say something");
							var noteText = "";
							if (d.note == null || d.note == "") {
								noteText = d.address;
							} else {
								noteText = d.note;
							}
							d3.select("#bubbleItemNote").text(noteText);
						})
						.append("svg:title")
						.text(function(d) { return d.address; });

					childBubbles.append("text")
						.attr("class", "childBubbleText" + iB)
						.attr("x", function(d,i) {return (oR*(3*(iB+1)-1) + oR*1.5*Math.cos((i-1)*45/180*Math.PI));})
						.attr("y", function(d,i) {return ((h+oR)/3 +        oR*1.5*Math.sin((i-1)*45/180*Math.PI));})
						.style("opacity",0.5)
						.attr("text-anchor", "middle")
						.style("fill", function(d,i) { return colVals(iB); }) // #1f77b4
						.attr("font-size", 6)
						.attr("cursor","pointer")
						.attr("dominant-baseline", "middle")
						.attr("alignment-baseline", "middle")
						.text(function(d) {return d.name})
						.on("click", function(d,i) {
							window.open(d.address);
						});

				}


			});

			resetBubbles = function () {
				w = window.innerWidth*0.68*0.95;
				oR = w/(1+3*nTop);

				h = Math.ceil(w/nTop*2);
				svgContainer.style("height",h+"px");

				mainNote.attr("y",h-15);

				svg.attr("width", w);
				svg.attr("height",h);


				var t = svg.transition()
					.duration(650);

				t.selectAll(".topBubble")
					.attr("r", function(d) { return oR; })
					.attr("cx", function(d, i) {return oR*(3*(1+i)-1);})
					.attr("cy", (h+oR)/3);

				t.selectAll(".topBubbleText")
					.attr("font-size", 30)
					.attr("x", function(d, i) {return oR*(3*(1+i)-1);})
					.attr("y", (h+oR)/3);

				for(var k = 0; k < nTop; k++)
				{
					t.selectAll(".childBubbleText" + k)
						.attr("x", function(d,i) {return (oR*(3*(k+1)-1) + oR*1.5*Math.cos((i-1)*45/180*Math.PI));})
						.attr("y", function(d,i) {return ((h+oR)/3 +        oR*1.5*Math.sin((i-1)*45/180*Math.PI));})
						.attr("font-size", 6)
						.style("opacity",0.5);

					t.selectAll(".childBubble" + k)
						.attr("r",  function(d) {return oR/3.0;})
						.style("opacity",0.5)
						.attr("cx", function(d,i) {return (oR*(3*(k+1)-1) + oR*1.5*Math.cos((i-1)*45/180*Math.PI));})
						.attr("cy", function(d,i) {return ((h+oR)/3 +        oR*1.5*Math.sin((i-1)*45/180*Math.PI));});

				}
			}


			function activateBubble(d,i) {
				// increase this bubble and decrease others
				var t = svg.transition()
					.duration(d3.event.altKey ? 7500 : 350);

				t.selectAll(".topBubble")
					.attr("cx", function(d,ii){
						if(i == ii) {
							// Nothing to change
							return oR*(3*(1+ii)-1) - 0.6*oR*(ii-1);
						} else {
							// Push away a little bit
							if(ii < i){
								// left side
								return oR*0.6*(3*(1+ii)-1);
							} else {
								// right side
								return oR*(nTop*3+1) - oR*0.6*(3*(nTop-ii)-1);
							}
						}
					})
					.attr("r", function(d, ii) {
						if(i == ii)
							return oR*1.8;
						else
							return oR*0.8;
					});

				t.selectAll(".topBubbleText")
					.attr("x", function(d,ii){
						if(i == ii) {
							// Nothing to change
							return oR*(3*(1+ii)-1) - 0.6*oR*(ii-1);
						} else {
							// Push away a little bit
							if(ii < i){
								// left side
								return oR*0.6*(3*(1+ii)-1);
							} else {
								// right side
								return oR*(nTop*3+1) - oR*0.6*(3*(nTop-ii)-1);
							}
						}
					})
					.attr("font-size", function(d,ii){
						if(i == ii)
							return 30*1.5;
						else
							return 30*0.6;
					});

				var signSide = -1;
				for(var k = 0; k < nTop; k++)
				{
					signSide = 1;
					if(k < nTop/2) signSide = 1;
					t.selectAll(".childBubbleText" + k)
						.attr("x", function(d,i) {return (oR*(3*(k+1)-1) - 0.6*oR*(k-1) + signSide*oR*2.5*Math.cos((i-1)*45/180*Math.PI));})
						.attr("y", function(d,i) {return ((h+oR)/3 + signSide*oR*2.5*Math.sin((i-1)*45/180*Math.PI));})
						.attr("font-size", function(){
							return (k==i)?12:6;
						})
						.style("opacity",function(){
							return (k==i)?1:0;
						});

					t.selectAll(".childBubble" + k)
						.attr("cx", function(d,i) {return (oR*(3*(k+1)-1) - 0.6*oR*(k-1) + signSide*oR*2.5*Math.cos((i-1)*45/180*Math.PI));})
						.attr("cy", function(d,i) {return ((h+oR)/3 + signSide*oR*2.5*Math.sin((i-1)*45/180*Math.PI));})
						.attr("r", function(){
							return (k==i)?(oR*0.55):(oR/3.0);
						})
						.style("opacity", function(){
							return (k==i)?1:0;
						});
				}
			}

			window.onresize = resetBubbles;

		};


	}])

	.controller('Query2Controller', ['$scope', '$http', function($scope, $http) {

		$scope.today = function() {
			$scope.dt = new Date();
		};
		$scope.today();

		$scope.clear = function () {
			$scope.dt = null;
		};
		$scope.toggleMin = function() {
			$scope.minDate = $scope.minDate ? null : new Date('1970-1-1');
		};
		$scope.toggleMin();

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		$scope.dateOptions = {
			formatYear: 'yy',
			startingDay: 1
		};

		$scope.initDate = '';//new Date();
		$scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
		$scope.format = $scope.formats[0];

		$scope.submit = function() {
			//Clear visualizations before loading them
			$('#tree-container').empty();

			// Dendogram API call
			var restUrl = "http://localhost:8080/homework/api/api/query2/getDendogram";
			showDendogram(restUrl);

			//Chord Diagram
			showChordDiagram();
		};

		var showDendogram = function(restUrl) {
			//alert(restUrl);

			// Get JSON data
			treeJSON = d3.json(restUrl, function(error, treeData) {

				// Calculate total nodes, max label length
				var totalNodes = 0;
				var maxLabelLength = 0;
				// variables for drag/drop
				var selectedNode = null;
				var draggingNode = null;
				// panning variables
				var panSpeed = 200;
				var panBoundary = 20; // Within 20px from edges will pan when dragging.
				// Misc. variables
				var i = 0;
				var duration = 750;
				var root;

				// size of the diagram
				var viewerWidth = $(document).width();
				var viewerHeight = $(document).height();

				var tree = d3.layout.tree()
					.size([viewerHeight, viewerWidth]);

				// define a d3 diagonal projection for use by the node paths later on.
				var diagonal = d3.svg.diagonal()
					.projection(function(d) {
						return [d.y, d.x];
					});

				// A recursive helper function for performing some setup by walking through all nodes

				function visit(parent, visitFn, childrenFn) {
					if (!parent) return;

					visitFn(parent);

					var children = childrenFn(parent);
					if (children) {
						var count = children.length;
						for (var i = 0; i < count; i++) {
							visit(children[i], visitFn, childrenFn);
						}
					}
				}

				// Call visit function to establish maxLabelLength
				visit(treeData, function(d) {
					totalNodes++;
					maxLabelLength = Math.max(d.name.length, maxLabelLength);

				}, function(d) {
					return d.children && d.children.length > 0 ? d.children : null;
				});


				// sort the tree according to the node names

				function sortTree() {
					tree.sort(function(a, b) {
						return b.name.toLowerCase() < a.name.toLowerCase() ? 1 : -1;
					});
				}
				// Sort the tree initially incase the JSON isn't in a sorted order.
				sortTree();

				// TODO: Pan function, can be better implemented.

				function pan(domNode, direction) {
					var speed = panSpeed;
					if (panTimer) {
						clearTimeout(panTimer);
						translateCoords = d3.transform(svgGroup.attr("transform"));
						if (direction == 'left' || direction == 'right') {
							translateX = direction == 'left' ? translateCoords.translate[0] + speed : translateCoords.translate[0] - speed;
							translateY = translateCoords.translate[1];
						} else if (direction == 'up' || direction == 'down') {
							translateX = translateCoords.translate[0];
							translateY = direction == 'up' ? translateCoords.translate[1] + speed : translateCoords.translate[1] - speed;
						}
						scaleX = translateCoords.scale[0];
						scaleY = translateCoords.scale[1];
						scale = zoomListener.scale();
						svgGroup.transition().attr("transform", "translate(" + translateX + "," + translateY + ")scale(" + scale + ")");
						d3.select(domNode).select('g.node').attr("transform", "translate(" + translateX + "," + translateY + ")");
						zoomListener.scale(zoomListener.scale());
						zoomListener.translate([translateX, translateY]);
						panTimer = setTimeout(function() {
							pan(domNode, speed, direction);
						}, 50);
					}
				}

				// Define the zoom function for the zoomable tree

				function zoom() {
					svgGroup.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
				}


				// define the zoomListener which calls the zoom function on the "zoom" event constrained within the scaleExtents
				var zoomListener = d3.behavior.zoom().scaleExtent([0.1, 3]).on("zoom", zoom);

				function initiateDrag(d, domNode) {
					draggingNode = d;
					d3.select(domNode).select('.ghostCircle').attr('pointer-events', 'none');
					d3.selectAll('.ghostCircle').attr('class', 'ghostCircle show');
					d3.select(domNode).attr('class', 'node activeDrag');

					svgGroup.selectAll("g.node").sort(function(a, b) { // select the parent and sort the path's
						if (a.id != draggingNode.id) return 1; // a is not the hovered element, send "a" to the back
						else return -1; // a is the hovered element, bring "a" to the front
					});
					// if nodes has children, remove the links and nodes
					if (nodes.length > 1) {
						// remove link paths
						links = tree.links(nodes);
						nodePaths = svgGroup.selectAll("path.link")
							.data(links, function(d) {
								return d.target.id;
							}).remove();
						// remove child nodes
						nodesExit = svgGroup.selectAll("g.node")
							.data(nodes, function(d) {
								return d.id;
							}).filter(function(d, i) {
								if (d.id == draggingNode.id) {
									return false;
								}
								return true;
							}).remove();
					}

					// remove parent link
					parentLink = tree.links(tree.nodes(draggingNode.parent));
					svgGroup.selectAll('path.link').filter(function(d, i) {
						if (d.target.id == draggingNode.id) {
							return true;
						}
						return false;
					}).remove();

					dragStarted = null;
				}

				// define the baseSvg, attaching a class for styling and the zoomListener
				var baseSvg = d3.select("#tree-container").append("svg")
					.attr("width", viewerWidth)
					.attr("height", viewerHeight)
					.attr("class", "overlay")
					.call(zoomListener);


				// Define the drag listeners for drag/drop behaviour of nodes.
				dragListener = d3.behavior.drag()
					.on("dragstart", function(d) {
						if (d == root) {
							return;
						}
						dragStarted = true;
						nodes = tree.nodes(d);
						d3.event.sourceEvent.stopPropagation();
						// it's important that we suppress the mouseover event on the node being dragged. Otherwise it will absorb the mouseover event and the underlying node will not detect it d3.select(this).attr('pointer-events', 'none');
					})
					.on("drag", function(d) {
						if (d == root) {
							return;
						}
						if (dragStarted) {
							domNode = this;
							initiateDrag(d, domNode);
						}

						// get coords of mouseEvent relative to svg container to allow for panning
						relCoords = d3.mouse($('svg').get(0));
						if (relCoords[0] < panBoundary) {
							panTimer = true;
							pan(this, 'left');
						} else if (relCoords[0] > ($('svg').width() - panBoundary)) {

							panTimer = true;
							pan(this, 'right');
						} else if (relCoords[1] < panBoundary) {
							panTimer = true;
							pan(this, 'up');
						} else if (relCoords[1] > ($('svg').height() - panBoundary)) {
							panTimer = true;
							pan(this, 'down');
						} else {
							try {
								clearTimeout(panTimer);
							} catch (e) {

							}
						}

						d.x0 += d3.event.dy;
						d.y0 += d3.event.dx;
						var node = d3.select(this);
						node.attr("transform", "translate(" + d.y0 + "," + d.x0 + ")");
						updateTempConnector();
					}).on("dragend", function(d) {
						if (d == root) {
							return;
						}
						domNode = this;
						if (selectedNode) {
							// now remove the element from the parent, and insert it into the new elements children
							var index = draggingNode.parent.children.indexOf(draggingNode);
							if (index > -1) {
								draggingNode.parent.children.splice(index, 1);
							}
							if (typeof selectedNode.children !== 'undefined' || typeof selectedNode._children !== 'undefined') {
								if (typeof selectedNode.children !== 'undefined') {
									selectedNode.children.push(draggingNode);
								} else {
									selectedNode._children.push(draggingNode);
								}
							} else {
								selectedNode.children = [];
								selectedNode.children.push(draggingNode);
							}
							// Make sure that the node being added to is expanded so user can see added node is correctly moved
							expand(selectedNode);
							sortTree();
							endDrag();
						} else {
							endDrag();
						}
					});

				function endDrag() {
					selectedNode = null;
					d3.selectAll('.ghostCircle').attr('class', 'ghostCircle');
					d3.select(domNode).attr('class', 'node');
					// now restore the mouseover event or we won't be able to drag a 2nd time
					d3.select(domNode).select('.ghostCircle').attr('pointer-events', '');
					updateTempConnector();
					if (draggingNode !== null) {
						update(root);
						centerNode(draggingNode);
						draggingNode = null;
					}
				}

				// Helper functions for collapsing and expanding nodes.

				function collapse(d) {
					if (d.children) {
						d._children = d.children;
						d._children.forEach(collapse);
						d.children = null;
					}
				}

				function expand(d) {
					if (d._children) {
						d.children = d._children;
						d.children.forEach(expand);
						d._children = null;
					}
				}

				var overCircle = function(d) {
					selectedNode = d;
					updateTempConnector();
				};
				var outCircle = function(d) {
					selectedNode = null;
					updateTempConnector();
				};

				// Function to update the temporary connector indicating dragging affiliation
				var updateTempConnector = function() {
					var data = [];
					if (draggingNode !== null && selectedNode !== null) {
						// have to flip the source coordinates since we did this for the existing connectors on the original tree
						data = [{
							source: {
								x: selectedNode.y0,
								y: selectedNode.x0
							},
							target: {
								x: draggingNode.y0,
								y: draggingNode.x0
							}
						}];
					}
					var link = svgGroup.selectAll(".templink").data(data);

					link.enter().append("path")
						.attr("class", "templink")
						.attr("d", d3.svg.diagonal())
						.attr('pointer-events', 'none');

					link.attr("d", d3.svg.diagonal());

					link.exit().remove();
				};

				// Function to center node when clicked/dropped so node doesn't get lost when collapsing/moving with large amount of children.

				function centerNode(source) {
					scale = zoomListener.scale();
					x = -source.y0;
					y = -source.x0;
					x = x * scale + viewerWidth / 2;
					y = y * scale + viewerHeight / 2;
					d3.select('g').transition()
						.duration(duration)
						.attr("transform", "translate(" + x + "," + y + ")scale(" + scale + ")");
					zoomListener.scale(scale);
					zoomListener.translate([x, y]);
				}

				// Toggle children function

				function toggleChildren(d) {
					if (d.children) {
						d._children = d.children;
						d.children = null;
					} else if (d._children) {
						d.children = d._children;
						d._children = null;
					}
					return d;
				}

				// Toggle children on click.

				function click(d) {
					if (d3.event.defaultPrevented) return; // click suppressed
					d = toggleChildren(d);
					update(d);
					centerNode(d);
				}

				function update(source) {
					// Compute the new height, function counts total children of root node and sets tree height accordingly.
					// This prevents the layout looking squashed when new nodes are made visible or looking sparse when nodes are removed
					// This makes the layout more consistent.
					var levelWidth = [1];
					var childCount = function(level, n) {

						if (n.children && n.children.length > 0) {
							if (levelWidth.length <= level + 1) levelWidth.push(0);

							levelWidth[level + 1] += n.children.length;
							n.children.forEach(function(d) {
								childCount(level + 1, d);
							});
						}
					};
					childCount(0, root);
					var newHeight = d3.max(levelWidth) * 25; // 25 pixels per line
					tree = tree.size([newHeight, viewerWidth]);

					// Compute the new tree layout.
					var nodes = tree.nodes(root).reverse(),
						links = tree.links(nodes);

					// Set widths between levels based on maxLabelLength.
					nodes.forEach(function(d) {
						d.y = (d.depth * (maxLabelLength * 1)); //maxLabelLength * 10px
						// alternatively to keep a fixed scale one can set a fixed depth per level
						// Normalize for fixed-depth by commenting out below line
						// d.y = (d.depth * 500); //500px per level.
					});

					// Update the nodes…
					node = svgGroup.selectAll("g.node")
						.data(nodes, function(d) {
							return d.id || (d.id = ++i);
						});

					// Enter any new nodes at the parent's previous position.
					var nodeEnter = node.enter().append("g")
						.call(dragListener)
						.attr("class", "node")
						.attr("transform", function(d) {
							return "translate(" + source.y0 + "," + source.x0 + ")";
						})
						.on('click', click);

					nodeEnter.append("circle")
						.attr('class', 'nodeCircle')
						.attr("r", 0)
						.style("fill", function(d) {
							return d._children ? "lightsteelblue" : "#fff";
						});

					nodeEnter.append("text")
						.attr("x", function(d) {
							return d.children || d._children ? -10 : 10;
						})
						.attr("dy", ".35em")
						.attr('class', 'nodeText')
						.attr("text-anchor", function(d) {
							return d.children || d._children ? "end" : "start";
						})
						.text(function(d) {
							return d.name;
						})
						.style("fill-opacity", 0);

					// phantom node to give us mouseover in a radius around it
					nodeEnter.append("circle")
						.attr('class', 'ghostCircle')
						.attr("r", 30)
						.attr("opacity", 0.2) // change this to zero to hide the target area
						.style("fill", "red")
						.attr('pointer-events', 'mouseover')
						.on("mouseover", function(node) {
							overCircle(node);
						})
						.on("mouseout", function(node) {
							outCircle(node);
						});

					// Update the text to reflect whether node has children or not.
					node.select('text')
						.attr("x", function(d) {
							return d.children || d._children ? -10 : 10;
						})
						.attr("text-anchor", function(d) {
							return d.children || d._children ? "end" : "start";
						})
						.text(function(d) {
							return d.name;
						});

					// Change the circle fill depending on whether it has children and is collapsed
					node.select("circle.nodeCircle")
						.attr("r", 4.5)
						.style("fill", function(d) {
							return d._children ? "lightsteelblue" : "#fff";
						});

					// Transition nodes to their new position.
					var nodeUpdate = node.transition()
						.duration(duration)
						.attr("transform", function(d) {
							return "translate(" + d.y + "," + d.x + ")";
						});

					// Fade the text in
					nodeUpdate.select("text")
						.style("fill-opacity", 1);

					// Transition exiting nodes to the parent's new position.
					var nodeExit = node.exit().transition()
						.duration(duration)
						.attr("transform", function(d) {
							return "translate(" + source.y + "," + source.x + ")";
						})
						.remove();

					nodeExit.select("circle")
						.attr("r", 0);

					nodeExit.select("text")
						.style("fill-opacity", 0);

					// Update the links…
					var link = svgGroup.selectAll("path.link")
						.data(links, function(d) {
							return d.target.id;
						});

					// Enter any new links at the parent's previous position.
					link.enter().insert("path", "g")
						.attr("class", "link")
						.attr("d", function(d) {
							var o = {
								x: source.x0,
								y: source.y0
							};
							return diagonal({
								source: o,
								target: o
							});
						});

					// Transition links to their new position.
					link.transition()
						.duration(duration)
						.attr("d", diagonal);

					// Transition exiting nodes to the parent's new position.
					link.exit().transition()
						.duration(duration)
						.attr("d", function(d) {
							var o = {
								x: source.x,
								y: source.y
							};
							return diagonal({
								source: o,
								target: o
							});
						})
						.remove();

					// Stash the old positions for transition.
					nodes.forEach(function(d) {
						d.x0 = d.x;
						d.y0 = d.y;
					});
				}

				// Append a group which holds all nodes and which the zoom Listener can act upon.
				var svgGroup = baseSvg.append("g");

				// Define the root
				root = treeData;
				root.x0 = viewerHeight / 2;
				root.y0 = 0;

				// Layout the tree initially and center on the root node.
				update(root);
				centerNode(root);
			});

		}//end showDendogram

		var showChordDiagram = function() {

			var width = 720,
				height = 720,
				outerRadius = Math.min(width, height) / 2 - 10,
				innerRadius = outerRadius - 24;

			var formatPercent = d3.format(".1%");

			var arc = d3.svg.arc()
				.innerRadius(innerRadius)
				.outerRadius(outerRadius);

			var layout = d3.layout.chord()
				.padding(.04)
				.sortSubgroups(d3.descending)
				.sortChords(d3.ascending);

			var path = d3.svg.chord()
				.radius(innerRadius);

			var svg = d3.select(".chorddiagram").append("svg")
				.attr("width", width)
				.attr("height", height)
				.append("g")
				.attr("id", "circle")
				.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

			svg.append("circle")
				.attr("r", outerRadius);

			queue()
				.defer(d3.csv, "cities.csv")
				.defer(d3.json, "matrix.json")
				.await(ready);

			function ready(error, cities, matrix) {
				if (error) throw error;

				// Compute the chord layout.
				layout.matrix(matrix);

				// Add a group per neighborhood.
				var group = svg.selectAll(".group")
					.data(layout.groups)
					.enter().append("g")
					.attr("class", "group")
					.on("mouseover", mouseover);

				// Add a mouseover title.
				group.append("title").text(function(d, i) {
					return cities[i].name + ": " + Math.floor(d.value);
				});

				// Add the group arc.
				var groupPath = group.append("path")
					.attr("id", function(d, i) { return "group" + i; })
					.attr("d", arc)
					.style("fill", function(d, i) { return cities[i].color; });

				// Add a text label.
				var groupText = group.append("text")
					.attr("x", 6)
					.attr("dy", 15);

				groupText.append("textPath")
					.attr("xlink:href", function(d, i) { return "#group" + i; })
					.text(function(d, i) { return cities[i].name; });

				// Remove the labels that don't fit. :(
				groupText.filter(function(d, i) { return groupPath[0][i].getTotalLength() / 2 - 16 < this.getComputedTextLength(); })
					.remove();

				// Add the chords.
				var chord = svg.selectAll(".chord")
					.data(layout.chords)
					.enter().append("path")
					.attr("class", "chord")
					.style("fill", function(d) { return cities[d.source.index].color; })
					.attr("d", path);

				// Add an elaborate mouseover title for each chord.
				chord.append("title").text(function(d) {
					return cities[d.source.index].name
						+ " ? " + cities[d.target.index].name
						+ ": " + d.source.value
						+ "\n" + cities[d.target.index].name
						+ " ? " + cities[d.source.index].name
						+ ": " + d.target.value;
				});

				function mouseover(d, i) {
					chord.classed("fade", function(p) {
						return p.source.index != i
							&& p.target.index != i;
					});
				}
			}

		}//end showChordDiagram

	}])

	.controller('Query3Controller', ['$scope', '$http', function($scope, $http) {

		$scope.today = function() {
			$scope.dt = new Date();
		};
		$scope.today();

		$scope.clear = function () {
			$scope.dt = null;
		};
		$scope.toggleMin = function() {
			$scope.minDate = $scope.minDate ? null : new Date('1970-1-1');
		};
		$scope.toggleMin();

		$scope.open = function($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		$scope.dateOptions = {
			formatYear: 'yy',
			startingDay: 1
		};

		$scope.initDate = new Date();
		$scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
		$scope.format = $scope.formats[0];

		$scope.submit = function() {
			$('.timeseries').empty();

			// API call
			$http({
				method: 'GET',
				url: 'http://localhost:8080/homework/api/api/query3/getTimeSeries'
			}).then(function successCallback(response) {
				//console.log(JSON.stringify(response));
				//Time Series
				showTimeSeriesResults(response.data);

			}, function errorCallback(response) {
				console.log(JSON.stringify(response));
			});

		};


		var showTimeSeriesResults = function(sdata) {
			var data = [];
			//alert(JSON.stringify(sdata));
			for(i=0; i < sdata.length; i++)	{
				data[i] = {'value': new Date(sdata[i]).getTime()}
			}
			var domEl = 'timeseries';
			var brushEnabled = true;
			timeseries(domEl, data, brushEnabled);
		}

	}])

	.controller('Query4Controller', ['$scope', '$http', function($scope, $http) {


		$scope.submit = function() {

			//Clear visualizations before loading them
			$('.bubblechart').empty();

			// Bubble Chart API call
			var restUrl = "http://localhost:8080/homework/api/api/query4/getBubbleChart";
			showBubbleChart(restUrl);
		};

		var showBubbleChart = function(restUrl) {
			var diameter = 960,
				format = d3.format(",d"),
				color = d3.scale.category20c();

			var bubble = d3.layout.pack()
				.sort(null)
				.size([diameter, diameter])
				.padding(1.5);

			var svg = d3.select('.bubblechart').append("svg")
				.attr("width", diameter)
				.attr("height", diameter)
				.attr("class", "bubble");

			d3.json(restUrl, function(error, root) {
				if (error) throw error;

				var node = svg.selectAll(".node")
					.data(bubble.nodes(classes(root))
						.filter(function(d) { return !d.children; }))
					.enter().append("g")
					.attr("class", "node")
					.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

				node.append("title")
					.text(function(d) { return d.className + ": " + format(d.value); });

				node.append("circle")
					.attr("r", function(d) { return d.r; })
					.style("fill", function(d) { return color(d.packageName); });

				node.append("text")
					.attr("dy", ".3em")
					.style("text-anchor", "middle")
					.text(function(d) { return d.className.substring(0, d.r / 3); });
			});

			// Returns a flattened hierarchy containing all leaf nodes under the root.
			function classes(root) {
				var classes = [];

				function recurse(name, node) {
					if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
					else classes.push({packageName: name, className: node.name, value: node.size});
				}

				recurse(null, root);
				return {children: classes};
			}

			d3.select(self.frameElement).style("height", diameter + "px");
		}//end showBubbleChart


	}])

	.controller('Query5Controller', ['$scope', '$http', function($scope, $http) {
		$scope.submit = function() {

			//Clear visualizations before loading them
			$('.bubblechart').empty();

			// Bubble Chart API call
			var restUrl = "http://localhost:8080/homework/api/api/query5/getBubbleChart";
			showBubbleChart(restUrl);
		};

		var showBubbleChart = function(restUrl) {
			var diameter = 960,
				format = d3.format(",d"),
				color = d3.scale.category20c();

			var bubble = d3.layout.pack()
				.sort(null)
				.size([diameter, diameter])
				.padding(1.5);

			var svg = d3.select('.bubblechart').append("svg")
				.attr("width", diameter)
				.attr("height", diameter)
				.attr("class", "bubble");

			d3.json(restUrl, function(error, root) {
				if (error) throw error;

				var node = svg.selectAll(".node")
					.data(bubble.nodes(classes(root))
						.filter(function(d) { return !d.children; }))
					.enter().append("g")
					.attr("class", "node")
					.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

				node.append("title")
					.text(function(d) { return d.className + ": " + format(d.value); });

				node.append("circle")
					.attr("r", function(d) { return d.r; })
					.style("fill", function(d) { return color(d.packageName); });

				node.append("text")
					.attr("dy", ".3em")
					.style("text-anchor", "middle")
					.text(function(d) { return d.className.substring(0, d.r / 3); });
			});

			// Returns a flattened hierarchy containing all leaf nodes under the root.
			function classes(root) {
				var classes = [];

				function recurse(name, node) {
					if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
					else classes.push({packageName: name, className: node.name, value: node.size});
				}

				recurse(null, root);
				return {children: classes};
			}

			d3.select(self.frameElement).style("height", diameter + "px");
		}//end showBubbleChart

	}]);


;
