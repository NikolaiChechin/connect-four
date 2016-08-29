package com.chechin.game;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by chechin on 23.08.2016.
 */
public class ConnectFourGame {

    private final Long id;
    private final String player1;
    private final String player2;
    private Player nextMove = Player.PLAYER_1;
    private int lastColumn;
    private int lastRow;
    private boolean over;
    private boolean draw;
    private Player winner;
    /**
     * Standard connect-four grid is 7x6
     */
    private static final int COLUMNS_NUMBER = 7;
    private static final int ROWS_NUMBER = 6;
    private String[][] grid = new String[COLUMNS_NUMBER][ROWS_NUMBER];

    public ConnectFourGame(Long id, String player1, String player2) {
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

    public int getLastRow() {
        return lastRow;
    }

    public Player getNextMove() {
        return nextMove;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public boolean isOver() {
        return over;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    public synchronized void move(Player player, int columnNumber) {
        if (this.over){
            throw new IllegalStateException("The game is over. No move can be done");
        }
        if (player != this.nextMove) {
            throw new IllegalArgumentException("It is not your turn!");
        }
        if (!(0 <= columnNumber && columnNumber < COLUMNS_NUMBER))
            throw new IllegalArgumentException("Column must be between 0 and 6");

        String column[] = grid[columnNumber];
        OptionalInt emptyRowNum = IntStream.range(0, column.length).filter(i -> column[i] == null).findFirst();
        if (emptyRowNum.isPresent()) {
            column[emptyRowNum.getAsInt()] = player.getIndex();
            this.lastColumn = columnNumber;
            this.lastRow = emptyRowNum.getAsInt();
            this.nextMove = this.nextMove == Player.PLAYER_1 ? Player.PLAYER_2 : Player.PLAYER_1;

            boolean winMove = isWinMove(player);
            boolean lastMove = isLastMove();
            if (winMove) {
                this.over = true;
                this.winner = player;
            } else if (lastMove) {
                this.over = true;
                this.draw = true;
            }
        } else {
            throw new IllegalArgumentException("Column " + columnNumber + " already filled. Move can't be done.");
        }
    }

    private boolean isWinMove(Player player) {
        return (getHorizontalLine().contains(player.getWinCombo())) || (getVerticalLine().contains(player.getWinCombo())) ||
                (getSlashDiagonalLine().contains(player.getWinCombo())) || (backslashDiagonalLine().contains(player.getWinCombo()));
    }

    private String getHorizontalLine() {
        StringBuilder sb = new StringBuilder(COLUMNS_NUMBER);
        for (int colNum = 0; colNum < COLUMNS_NUMBER; colNum++) {
            sb.append(this.grid[colNum][this.lastRow]);
        }
        return sb.toString();
    }

    private String getVerticalLine() {
        return Arrays.stream(this.grid[this.lastColumn]).collect(Collectors.joining(""));
    }

    private String getSlashDiagonalLine() {
        StringBuilder sb = new StringBuilder(ROWS_NUMBER);
        for (int rowNum = 0; rowNum < ROWS_NUMBER; rowNum++) {
            int w = this.lastColumn + this.lastRow - rowNum;
            if (0 <= w && w < COLUMNS_NUMBER) {
                sb.append(this.grid[w][rowNum]);
            }
        }
        return sb.toString();
    }

    private String backslashDiagonalLine() {
        StringBuilder sb = new StringBuilder(ROWS_NUMBER);
        for (int rowNum = 0; rowNum < ROWS_NUMBER; rowNum++) {
            int w = this.lastColumn - this.lastRow + rowNum;
            if (0 <= w && w < COLUMNS_NUMBER) {
                sb.append(this.grid[w][rowNum]);
            }
        }
        return sb.toString();
    }

    private boolean isLastMove() {
        for (int i = 0; i < COLUMNS_NUMBER; i++) {
            for (int j = 0; j < ROWS_NUMBER; j++) {
                if (grid[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
