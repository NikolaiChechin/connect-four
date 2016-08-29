package com.chechin.game;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Chechin on 29.08.2016.
 */
public class GameHelper {

    private static AtomicLong gameIdSequence = new AtomicLong(1);
    private static final HashMap<Long, String> pendingGames = new HashMap<>();
    private static final Map<Long, ConnectFourGame> activeGames = new HashMap<>();

    public static Long queueGame(String player1) {
        Long id = GameHelper.gameIdSequence.getAndIncrement();
        GameHelper.pendingGames.put(id, player1);
        return id;
    }

    public static void removeQueuedGame(Long id) {
        GameHelper.pendingGames.remove(id);
    }

    public static ConnectFourGame startGame(Long id, String player2) {
        String player1 = GameHelper.pendingGames.remove(id);
        ConnectFourGame game = new ConnectFourGame(id, player1, player2);
        GameHelper.activeGames.put(id, game);
        return game;
    }

    public static ConnectFourGame getActiveGame(Long gameId) {
        return GameHelper.activeGames.get(gameId);
    }

    public static void removeActiveGame(Long gameId) {
        GameHelper.activeGames.remove(gameId);
    }

    public static synchronized void forfeit(Long gameId, Player player) {
        ConnectFourGame game = GameHelper.activeGames.remove(gameId);
        game.setWinner(player == Player.PLAYER_1 ? Player.PLAYER_2 : Player.PLAYER_1);
        game.setOver(true);
    }
}
