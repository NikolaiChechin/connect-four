package com.chechin;

/**
 * Created by chechin on 23.08.2016.
 */

import com.chechin.game.ConnectFourGame;
import com.chechin.game.Move;
import com.chechin.game.Player;
import com.chechin.messages.MessageSender;
import com.chechin.messages.MoveMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chechin.ExceptionHandler.handleException;
import static com.chechin.messages.MessageSender.sendJsonMessage;

@ServerEndpoint("/connect-four/{userId}/{action}")
public class ConnectFourServer {

    private static Map<Long, GameWrapper> games = new HashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("userId") String playerName, @PathParam("action") String action) {
        System.out.println("Session is established for user: " + playerName);
        try {
            if ("create".equalsIgnoreCase(action)) {
                long gameId = ConnectFourGame.queueGame(playerName);
                GameWrapper gameWrapper = new GameWrapper();
                gameWrapper.setGameId(gameId);
                gameWrapper.setPlayer1(session);
                ConnectFourServer.games.put(gameId, gameWrapper);
                System.out.println("game scheduled; game id =" + gameId + ", waiting for the second player");
                sendJsonMessage(session, gameWrapper, "game scheduled; game id =" + gameId + ", waiting for the second player");
            } else if ("join".equalsIgnoreCase(action)) {
                long gameId;
                List<String> gameIds = session.getRequestParameterMap().get("gameId");
                if (gameIds != null) {
                    System.out.println("game id is: " + gameIds.get(0));
                    try {
                        gameId = Long.parseLong(gameIds.get(0));
                        ConnectFourGame connectFourGame = ConnectFourGame.getActiveGame(gameId);
                        if (connectFourGame != null) {
                            session.close(new CloseReason(
                                    CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                                    "This game has already started. Closing session"
                            ));
                        }
                        GameWrapper gameWrapper = ConnectFourServer.games.get(gameId);
                        if (gameWrapper == null) {
                            session.close(new CloseReason(
                                    CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                                    "There is not queued game for id: " + gameId + ". Closing session"
                            ));
                        } else {
                            gameWrapper.setPlayer2(session);
                            gameWrapper.setGame(ConnectFourGame.startGame(gameId, playerName));
                            System.out.println("second player has joined. gameStarted");
                            sendJsonMessage(gameWrapper.getPlayer1(), gameWrapper, playerName + " has joined the game. gameStarted");
                            sendJsonMessage(gameWrapper.getPlayer2(), gameWrapper, "gameStarted");
                        }
                    } catch (NumberFormatException e) {
                        session.close(new CloseReason(
                                CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                                "Wrong format for game id: " + gameIds.get(0)
                        ));
                    }
                } else {
                    session.close(new CloseReason(
                            CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                            "Game id must be provided to join a game. Closing session"
                    ));
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

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("Incoming message: " + message);
        try {
            Move move = mapper.readValue(message, Move.class);
            GameWrapper gameWrapper = ConnectFourServer.games.get(move.getGameId());
            if (gameWrapper != null) {
                boolean isPlayer1 = session == gameWrapper.getPlayer1();
                ConnectFourGame game = gameWrapper.getGame();
                game.move(isPlayer1 ? Player.PLAYER_1 :
                        Player.PLAYER_2, move.getColumn());
                sendJsonMessage(session, gameWrapper, new MoveMessage("You have made a move.", game.getGrid()));
                sendJsonMessage(isPlayer1 ? gameWrapper.getPlayer2() :
                        gameWrapper.getPlayer1(), gameWrapper, new MoveMessage("Opponent has made a move.", game.getGrid()));

                if (game.isOver()) {
                    if (game.isDraw()){
                        sendJsonMessage(gameWrapper.getPlayer1(), gameWrapper, "Game is drawn!");
                        sendJsonMessage(gameWrapper.getPlayer2(), gameWrapper, "Game is drawn!");
                    } else {
                        if (game.getWinner() == Player.PLAYER_1) {
                            sendJsonMessage(gameWrapper.getPlayer1(), gameWrapper, "You win! Game is over!");
                            sendJsonMessage(gameWrapper.getPlayer2(), gameWrapper, "Opponent win! Game is over!");
                        } else {
                            sendJsonMessage(gameWrapper.getPlayer1(), gameWrapper, "Opponent win! Game is over!");
                            sendJsonMessage(gameWrapper.getPlayer2(), gameWrapper, "You win! Game is over!");
                        }
                    }
//                    gameWrapper.getPlayer1().close();
//                    gameWrapper.getPlayer2().close();
                }
            } else {
                sendJsonMessage(session, gameWrapper, "Game with id: " + move.getGameId()
                        + " is not found. Please, create a new one");
            }
        } catch (IOException e) {
            handleException(e, session);
        }
    }


    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
        sendJsonMessage(session, t.getMessage());
    }

    @OnClose
    public void onClose(Session session, @PathParam("gameId") long gameId) {
        GameWrapper gameWrapper = ConnectFourServer.games.get(gameId);
        if (gameWrapper == null)
            return;
        boolean isPlayer1 = session == gameWrapper.getPlayer1();
        if (gameWrapper.getGame() == null) {
            ConnectFourGame.removeQueuedGame(gameWrapper.getGameId());
        } else if (!gameWrapper.getGame().isOver()) {
            gameWrapper.getGame().forfeit(isPlayer1 ? Player.PLAYER_1 :
                    Player.PLAYER_2);
            Session opponent = (isPlayer1 ? gameWrapper.getPlayer2() : gameWrapper.getPlayer1());
            sendJsonMessage(opponent, gameWrapper, "Game forfeited");
            try {
                opponent.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
