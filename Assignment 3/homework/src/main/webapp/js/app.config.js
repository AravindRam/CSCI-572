
	$.navAsAjax = true;
	$.sound_path = "sound/";
	$.sound_on = true;
	var throttle_delay = 350,
	menu_speed = 235,
	enableJarvisWidgets = true,
	enableMobileWidgets = false,
	fastClick = false,
/*
 * These elements are ignored during DOM object deletion for ajax version 
 * It will delete all objects during page load with these exceptions:
 */
	ignore_key_elms = ["#header, #left-panel, #main, div.page-footer, #shortcut, #divSmallBoxes, #divMiniIcons, #divbigBoxes, #voiceModal, script"],
	voice_command = true,
	voice_command_auto = false,
	voice_command_lang = 'en-US',
	voice_localStorage = false;
 	/*if (voice_command) {
	 		
		var commands = {
					
			'show dashboard' : function() { window.location.hash = "dashboard" },
			'show inbox' : function() {  window.location.hash = "inbox" },
			'show graphs' : function() {  window.location.hash = "graphs/flot" },
			'show flotchart' : function() { window.location.hash = "graphs/flot" },
			'show morris chart' : function() { window.location.hash = "graphs/morris" },
			'show inline chart' : function() { window.location.hash = "graphs/inline-charts" },
			'show dygraphs' : function() { window.location.hash = "graphs/dygraphs" },
			'show tables' : function() { window.location.hash = "tables/table" },
			'show data table' : function() { window.location.hash = "tables/datatable" },
			'show jquery grid' : function() { window.location.hash = "tables/jqgrid" },
			'show form' : function() { window.location.hash = "forms/form-elements" },
			'show form layouts' : function() { window.location.hash = "forms/form-templates" },
			'show form validation' : function() { window.location.hash = "forms/validation" },
			'show form elements' : function() { window.location.hash = "forms/bootstrap-forms" },
			'show form plugins' : function() { window.location.hash = "forms/plugins" },
			'show form wizards' : function() { window.location.hash = "forms/wizards" },
			'show bootstrap editor' : function() { window.location.hash = "forms/other-editors" },
			'show dropzone' : function() { window.location.hash = "forms/dropzone" },
			'show image cropping' : function() { window.location.hash = "forms/image-editor" },
			'show general elements' : function() { window.location.hash = "ui/general-elements" },
			'show buttons' : function() { window.location.hash = "ui/buttons" },
			'show fontawesome' : function() { window.location.hash = "ui/icons/fa" },
			'show glyph icons' : function() { window.location.hash = "ui/icons/glyph" },
			'show flags' : function() { window.location.hash = "ui/icons/flags" },
			'show grid' : function() { window.location.hash = "ui/grid" },
			'show tree view' : function() { window.location.hash = "ui/treeview" },
			'show nestable lists' : function() { window.location.hash = "ui/nestable-list" },
			'show jquery U I' : function() { window.location.hash = "ui/jqui" },
			'show typography' : function() { window.location.hash = "ui/typography" },
			'show calendar' : function() { window.location.hash = "calendar" },
			'show widgets' : function() { window.location.hash = "widgets" },
			'show gallery' : function() { window.location.hash = "gallery" },
			'show maps' : function() { window.location.hash = "gmap-xml" },
			'go back' :  function() { history.back(1); }, 
			'scroll up' : function () { $('html, body').animate({ scrollTop: 0 }, 100); },
			'scroll down' : function () { $('html, body').animate({ scrollTop: $(document).height() }, 100);},
			'hide navigation' : function() { 
				if ($.root_.hasClass("container") && !$.root_.hasClass("menu-on-top")){
					$('span.minifyme').trigger("click");
				} else {
					$('#hide-menu > span > a').trigger("click"); 
				}
			},
			'show navigation' : function() { 
				if ($.root_.hasClass("container") && !$.root_.hasClass("menu-on-top")){
					$('span.minifyme').trigger("click");
				} else {
					$('#hide-menu > span > a').trigger("click"); 
				}
			},
			'mute' : function() {
				$.sound_on = false;
				$.smallBox({
					title : "MUTE",
					content : "All sounds have been muted!",
					color : "#a90329",
					timeout: 4000,
					icon : "fa fa-volume-off"
				});
			},
			'sound on' : function() {
				$.sound_on = true;
				$.speechApp.playConfirmation();
				$.smallBox({
					title : "UNMUTE",
					content : "All sounds have been turned on!",
					color : "#40ac2b",
					sound_file: 'voice_alert',
					timeout: 5000,
					icon : "fa fa-volume-up"
				});
			},
			'stop' : function() {
				smartSpeechRecognition.abort();
				$.root_.removeClass("voice-command-active");
				$.smallBox({
					title : "VOICE COMMAND OFF",
					content : "Your voice commands has been successfully turned off. Click on the <i class='fa fa-microphone fa-lg fa-fw'></i> icon to turn it back on.",
					color : "#40ac2b",
					sound_file: 'voice_off',
					timeout: 8000,
					icon : "fa fa-microphone-slash"
				});
				if ($('#speech-btn .popover').is(':visible')) {
					$('#speech-btn .popover').fadeOut(250);
				}
			},
			'help' : function() {
				$('#voiceModal').removeData('modal').modal( { remote: "ajax/modal-content/modal-voicecommand.html", show: true } );
				if ($('#speech-btn .popover').is(':visible')) {
					$('#speech-btn .popover').fadeOut(250);
				}
			},		
			'got it' : function() {
				$('#voiceModal').modal('hide');
			},	
			'logout' : function() {
				$.speechApp.stop();
				window.location = $('#logout > span > a').attr("href");
			}
		}; 
		
	};*/
/*
 * END APP.CONFIG
 */
 
 
 
 
 
 	