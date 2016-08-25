var webUiApp = angular.module('webUiApp', []);

webUiApp.controller('WebUiAppController', function WebUiAppController($scope) {
    var server;
    $scope.serverMessage;
    $scope.startGame = function (action) {
        try {
            var url;
            if (action == 'create'){
                url = 'ws://localhost:8080/connect-four/' + $scope.playerName + '/create';
            } else if (action == 'join'){
                url = 'ws://localhost:8080/connect-four/' + $scope.playerName + '/join?gameId=' + $scope.gameId;
            }
            server = new WebSocket(url);
        } catch (error) {
            alert("Sever connection error: " + error);
            return;
        }
        server.onmessage = function (event) {
            $scope.serverMessage = event.data;
            $scope.$apply();
        }
    }
    $scope.sendMessage = function () {
        server.send(JSON.stringify({column: $scope.column}));
    }
});