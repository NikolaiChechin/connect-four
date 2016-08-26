package com.chechin;

import com.chechin.game.ConnectFourGame;

import javax.websocket.Session;

/**
 * Created by chechin on 23.08.2016.
 */
public class GameWrapper {

    private long gameId;

    private Session player1;

    private Session player2;

    private ConnectFourGame game;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Session getPlayer1() {
        return player1;
    }

    public void setPlayer1(Session player1) {
        this.player1 = player1;
    }

    public Session getPlayer2() {
        return player2;
    }

    public void setPlayer2(Session player2) {
        this.player2 = player2;
    }

    public ConnectFourGame getGame() {
        return game;
    }

    public void setGame(ConnectFourGame game) {
        this.game = game;
    }
}
