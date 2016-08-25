package com.chechin;

/**
 * Created by chechin on 23.08.2016.
 */

import com.chechin.messages.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.CloseReason;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/connect-four/{gameId}/{userId}/{action}")
public class ConnectFourServer {

    private static Map<Long, GameWrapper> games = new HashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("gameId") long gameId,
                       @PathParam("userId") String username, @PathParam("action") String action) {
        System.out.println("Session is established for user: " + username);
        try {
            ConnectFourGame connectFourGame = ConnectFourGame.getActiveGame(gameId);
            if (connectFourGame != null) {
                session.close(new CloseReason(
                        CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                        "This game has already started."
                ));
            }
            if ("create".equalsIgnoreCase(action)) {
                gameId = ConnectFourGame.queueGame(username);
                GameWrapper gameWrapper = new GameWrapper();
                gameWrapper.setGameId(gameId);
                gameWrapper.setPlayer1(session);
                ConnectFourServer.games.put(gameId, gameWrapper);
                System.out.println("game scheduled; game id =" + gameId + ", waiting for the second player");
                MessageSender.sendJsonMessage(session, gameWrapper, "game scheduled; game id =" + gameId + ", waiting for the second player");
            } else if ("join".equalsIgnoreCase(action)) {
                GameWrapper gameWrapper = ConnectFourServer.games.get(gameId);
                gameWrapper.setPlayer2(session);
                gameWrapper.setGame(ConnectFourGame.startGame(gameId, username));
                System.out.println("second player has joined. gameStarted");
                MessageSender.sendJsonMessage(gameWrapper.getPlayer1(), gameWrapper, "second player has joined. gameStarted");
                MessageSender.sendJsonMessage(gameWrapper.getPlayer2(), gameWrapper, "gameStarted");
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                session.close(new CloseReason(
                        CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.toString()
                ));
            } catch (IOException ignore) {
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message,
                          @PathParam("gameId") long gameId) {
        System.out.println(message);
        Move move = mapper.readValue(message, Move.class);
        GameWrapper gameWrapper = ConnectFourServer.games.get(gameId);
        boolean isPlayer1 = session == gameWrapper.getPlayer1();
        gameWrapper.getGame().move(isPlayer1 ? Player.PLAYER_1 :
                Player.PLAYER_2, move.getColumn());
        MessageSender.sendJsonMessage(isPlayer1 ? gameWrapper.getPlayer2() :
                gameWrapper.getPlayer1(), gameWrapper, "Opponent has made a move");
    }
}
