
import com.googlecode.lanterna.terminal.Terminal;

import java.util.Timer;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Player {

    private int health;

    private Coordinates coordinates;

    private Game game;

    private boolean won = false;

    //if turned on for the next 2s after got hit
    //I am using the timer to count the time
    //the player turns red, when it's hit and during this time it's immortal
    private boolean gotHit = false;

    private Timer timer = new Timer(true);

    public Player(Game game, Coordinates coordinates, int health) {

        this.game = game;

        this.coordinates = coordinates;

        this.health = health;

    }

    //tells us if the player has won
    public boolean isWon() {
        return won;
    }

    //if the health is not given, starts the game with one life
    public Player(Game game, Coordinates coordinates) {
        this(game, coordinates, 1);
    }

    public void redraw() {

        Terminal terminal = game.getTerminal();

        synchronized (terminal) {

            int x = coordinates.getX() - game.getOffset().getX() + 1;
            int y = coordinates.getY() - game.getOffset().getY() + 1;

            terminal.moveCursor(x, y);

            if (!gotHit) terminal.applyBackgroundColor(Terminal.Color.WHITE);
            else terminal.applyBackgroundColor(Terminal.Color.RED);

            terminal.applyForegroundColor(Terminal.Color.BLACK);
            terminal.putCharacter(' ');

        }
    }

    //makes a move after checking if it's possible
    public void move(Coordinates vector) {

        //other threads have to wait till the player has finished moving
        //to be able to draw their objects
        synchronized (game.getTerminal()) {

            Coordinates oldCoord = coordinates.clone();

            coordinates.add(vector);

            Showable[][] map = game.getField().getMap();

            int x = coordinates.getX();
            int y = coordinates.getY();

            if (x >= 0 && y >= 0 && x < map.length && y < map[0].length) {

                if (map[x][y] != null && map[x][y] instanceof Wall
                        || map[x][y] instanceof Entrance
                        || (map[x][y] instanceof Exit && game.getField().getKeysLeft() > 0)) {

                    coordinates = oldCoord;
                    return;

                }

                if (map[x][y] != null && map[x][y] instanceof Key) {

                    map[x][y] = null;
                    game.getField().pickUpKey();

                }

                if (map[x][y] != null && map[x][y] instanceof Exit && game.getField().getKeysLeft() == 0) {

                    if (game.getCheatThread() != null && game.getCheatThread().isAlive()) {
                        game.setKillCheat(true);
                        try {
                            Thread.sleep(260);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    game.setPause(true);
                    game.getMenu().drawFrame();
                    game.getMenu().clearMenu();
                    game.getMenu().writeOut("You just won the game!", 2);
                    won = true;

                }

                if (((map[x][y] != null && map[x][y] instanceof Obstacle) || DynamicObstacle.coordinatesList.contains(coordinates)) && !gotHit) {

                    damage();

                }

                //redrawing
                game.getField().redraw(oldCoord);

                redraw();

            } else coordinates = oldCoord;

        }

    }

    public int getHealth() {
        return health;
    }

    public int getKeysLeft() {
        return game.getField().getKeysLeft();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setGotHit(boolean gotHit) { this.gotHit = gotHit; }

    public void damage() {

        if (health > 0 && !gotHit) {

            health--;

            gotHit = true;

            timer.schedule(new DamagableSetter(this), 2000);

            game.getStats().redraw();
            redraw();

            if (health == 0) {
                if (game.getCheatThread() != null && game.getCheatThread().isAlive()) {
                    game.setKillCheat(true);
                    try {
                        Thread.sleep(260);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                game.setPause(true);
                game.getMenu().drawFrame();
                game.getMenu().clearMenu();
                game.getMenu().writeOut("You just lost..", 2);
            }

        }

    }

}
