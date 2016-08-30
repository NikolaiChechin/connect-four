Connect-four game web service. It allows to run multiple game sessions for two players.<br />
The game server uses WebSocket protocol:<br />
```ws://localhost:8080/connect-four/{player1Name}/create``` - url to create a new game<br />
```ws://localhost:8080/connect-four/{player2Name}/join?gameId={gameId}``` - url to join a game<br />
to make a move it's needed to send a message through open WebSocket connection in JSON format: {gameId: gameId_value, column: column_value}<br />
Simple web ui (webUi.html) is provided for test purposes.
