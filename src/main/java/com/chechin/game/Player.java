package com.chechin.game;

/**
 * Created by chechin on 25.08.2016.
 */
public enum Player {
    PLAYER_1('1', "1111"),
    PLAYER_2('2', "2222");

    private char index;
    private String winCombo;

    Player(char index, String winCombo) {
        this.index = index;
        this.winCombo = winCombo;
    }

    public char getIndex(){
        return index;
    }

    public String getWinCombo() {
        return winCombo;
    }
}
