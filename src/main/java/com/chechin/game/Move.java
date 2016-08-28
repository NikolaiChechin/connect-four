package com.chechin.game;

/**
 * Created by chechin on 25.08.2016.
 */
public class Move {

    private Long gameId;

    private int column;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
