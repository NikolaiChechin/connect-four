var webUiApp = angular.module('webUiApp', []);

webUiApp.controller('WebUiAppController', function WebUiAppController($scope) {
    var server;
    $scope.serverMessage;
    $scope.startGame = function (action) {
        try {
            server = new WebSocket('ws://localhost:8080/connect-four/' + $scope.gameId + '/' + $scope.playerName + '/' + action);
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