package com.chechin.game;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public boolean isOver() {
        return over;
    }

    public boolean isDraw() {
        return draw;
    }

    public Player getWinner() {
        return winner;
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

    public synchronized void move(Player player, int columnNumber) {

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

            if (isWinMove(player)) {
                this.over = true;
                this.winner = player;
            }

            if (isDrawMove()) {
                this.draw = true;
            }

            if (isOver()) {
                ConnectFourGame.activeGames.remove(this.id);
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

    private boolean isDrawMove() {
        Optional<String> nullElem = Arrays.stream(grid).flatMap(Arrays::stream).filter(elem -> elem == null).findAny();
        nullElem.isPresent();
        return !nullElem.isPresent();
    }
}
