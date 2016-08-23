package com.chechin;

/**
 * Created by chechin on 23.08.2016.
 */

import com.chechin.messages.MessageSender;

import javax.websocket.CloseReason;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint("/connect-four/{gameId}/{userId}")
public class ConnectFourServer {

    private static Map<Long, GameWrapper> games = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("gameId") long gameId,
                       @PathParam("username") String username) {
        try {
            ConnectFourGame connectFourGame = ConnectFourGame.getActiveGame(gameId);
            if (connectFourGame != null) {
                session.close(new CloseReason(
                        CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                        "This game has already started."
                ));
            }

            List<String> actions = session.getRequestParameterMap().get("action");
            if (actions != null && actions.size() == 1) {
                String action = actions.get(0);
                if ("start".equalsIgnoreCase(action)) {
                    GameWrapper gameWrapper = new GameWrapper();
                    gameWrapper.setGameId(gameId);
                    gameWrapper.setPlayer1(session);
                    ConnectFourServer.games.put(gameId, gameWrapper);
                } else if ("join".equalsIgnoreCase(action)) {
                    GameWrapper gameWrapper = ConnectFourServer.games.get(gameId);
                    gameWrapper.setPlayer2(session);
                    gameWrapper.setGame(ConnectFourGame.startGame(gameId, username));
                    MessageSender.sendJsonMessage(gameWrapper.getPlayer1(), gameWrapper, "gameStarted");
                    MessageSender.sendJsonMessage(gameWrapper.getPlayer2(), gameWrapper, "gameStarted");
                }
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
}
