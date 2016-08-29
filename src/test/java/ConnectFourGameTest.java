import com.chechin.game.ConnectFourGame;
import com.chechin.game.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Chechin on 29.08.2016.
 */
public class ConnectFourGameTest {

    private ConnectFourGame game;

    @Before
    public void beforeEachTest() {
        game = new ConnectFourGame(1L, "Bill", "John");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalColumn() {
        game.move(Player.PLAYER_1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongTurn() {
        game.move(Player.PLAYER_1, 1);
        game.move(Player.PLAYER_1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilledColumn() {
        for (int i = 0; i < 4; i++) {
            game.move(Player.PLAYER_1, 1);
            game.move(Player.PLAYER_2, 1);
        }
    }

    @Test
    public void testLastRow(){
        game.move(Player.PLAYER_1, 1);
        game.move(Player.PLAYER_2, 1);
        assertEquals(1, game.getLastRow());
    }

    @Test
    public void testFirstMove(){
        assertEquals(game.getNextMove(), Player.PLAYER_1);
    }

    @Test
    public void testPlayerSwitch(){
        game.move(Player.PLAYER_1, 1);
        assertEquals(game.getNextMove(), Player.PLAYER_2);
    }

    @Test(expected = IllegalStateException.class)
    public void testExcessMove() {
        for (int i = 0; i < 4; i++) {
            game.move(Player.PLAYER_1, 1);
            game.move(Player.PLAYER_2, 2);
        }
    }

    @Test
    public void testVerticalWin(){
        for (int i = 0; i < 3; i++) {
            game.move(Player.PLAYER_1, 1);
            game.move(Player.PLAYER_2, 2);
        }
        game.move(Player.PLAYER_1, 1);
        assertTrue(game.isOver());
        assertEquals(game.getWinner(), Player.PLAYER_1);
    }

    @Test
    public void testHorizontalWin(){
        for (int i = 0; i < 3; i++) {
            game.move(Player.PLAYER_1, i);
            game.move(Player.PLAYER_2, 6);
        }
        game.move(Player.PLAYER_1, 3);
        assertTrue(game.isOver());
        assertEquals(game.getWinner(), Player.PLAYER_1);
    }

    @Test
    public void testDiagonalWin(){
        game.move(Player.PLAYER_1, 0);
        game.move(Player.PLAYER_2, 0);
        game.move(Player.PLAYER_1, 0);
        game.move(Player.PLAYER_2, 0);
        game.move(Player.PLAYER_1, 0);
        game.move(Player.PLAYER_2, 1);
        game.move(Player.PLAYER_1, 1);
        game.move(Player.PLAYER_2, 1);
        game.move(Player.PLAYER_1, 2);
        game.move(Player.PLAYER_2, 2);
        game.move(Player.PLAYER_1, 2);
        game.move(Player.PLAYER_2, 3);
        assertTrue(game.isOver());
        assertEquals(game.getWinner(), Player.PLAYER_2);
    }
}
