package com.chechin.messages;

/**
 * Created by chechin on 28.08.2016.
 */
public class MoveMessage {

    private final String message;
    private String[][] grid;

    public MoveMessage(String message, String[][] grid) {
        this.message = message;
        this.grid = grid;
    }

    public String getMessage() {
        return message;
    }

    public String[][] getGrid() {
        return grid;
    }

}
