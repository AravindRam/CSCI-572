var homeworkApp = angular.module('homeworkApp', [
  	'ngRoute',
  	'ui.bootstrap',
  	'app.controllers',
  	'app.main',
  	'app.navigation',
  	'app.localize',
  	'app.activity',
  	'app.smartui'
]);

homeworkApp.config(['$routeProvider', '$provide', function($routeProvider, $provide) {
	$routeProvider
		.when('/', {
			redirectTo: '/home'
		})
		/* We are loading our views dynamically by passing arguments to the location url */
		.when('/:page', {
			templateUrl: function($routeParams) {
				return 'views/'+ $routeParams.page +'.html';
			},
			controller: 'PageViewController'
		})
		.when('/:page/:child*', {
			templateUrl: function($routeParams) {
				return 'views/'+ $routeParams.page + '/' + $routeParams.child + '.html';
			},
			controller: 'PageViewController'
		})
		.otherwise({
			redirectTo: '/home'
		});

	// with this, you can use $log('Message') same as $log.info('Message');
	$provide.decorator('$log', ['$delegate',
	function($delegate) {
		// create a new function to be returned below as the $log service (instead of the $delegate)
		function logger() {
			// if $log fn is called directly, default to "info" message
			logger.info.apply(logger, arguments);
		}

		// add all the $log props into our new logger fn
		angular.extend(logger, $delegate);
		return logger;
	}]); 

}]);