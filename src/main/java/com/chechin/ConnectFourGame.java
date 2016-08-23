package com.chechin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by chechin on 23.08.2016.
 */
public class ConnectFourGame {

    private static AtomicLong gameIdSequence = new AtomicLong(1);
    private static final HashMap<Long, String> pendingGames = new HashMap<>();
    private static final Map<Long, ConnectFourGame> activeGames = new HashMap<>();
    private final Long id;
    private final String player1;
    private final String player2;

    private ConnectFourGame(Long id, String player1, String player2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Long getId() {
        return id;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public static Map<Long, String> getPendingGames() {
        return (Map<Long, String>) ConnectFourGame.pendingGames.clone();
    }

    public static Long queueGame(String player1) {
        Long id = ConnectFourGame.gameIdSequence.getAndIncrement();
        ConnectFourGame.pendingGames.put(id, player1);
        return id;
    }

    public static void removeQueuedGame(Integer id) {
        ConnectFourGame.pendingGames.remove(id);
    }

    public static ConnectFourGame startGame(Long id, String player2) {
        String player1 = ConnectFourGame.pendingGames.remove(id);
        ConnectFourGame game = new ConnectFourGame(id, player1, player2);
        ConnectFourGame.activeGames.put(id, game);
        return game;
    }

    public static ConnectFourGame getActiveGame(Long gameId) {
        return ConnectFourGame.activeGames.get(gameId);
    }
}
