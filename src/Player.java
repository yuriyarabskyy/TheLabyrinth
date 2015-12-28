import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Player implements Damagable {

    private int health;

    private Coordinates coordinates;

    private Game game;

    public Player(Game game, Coordinates coordinates, int health) {

        this.game = game;

        this.coordinates = coordinates;

        this.health = health;

    }

    public Player(Game game, Coordinates coordinates) {
        this(game, coordinates, 1);
    }

    public void redraw() {

        Terminal terminal = game.getTerminal();

        synchronized (terminal) {

            int x = coordinates.getX() - game.getOffset().getX() + 1;
            int y = coordinates.getY() - game.getOffset().getY() + 1;

            terminal.moveCursor(x, y);

            terminal.applyForegroundColor(Terminal.Color.GREEN);

            terminal.applyBackgroundColor(Terminal.Color.BLUE);

            terminal.putCharacter('\u263b');

        }

    }

    public void move(Coordinates vector, Coordinates offset) {

        Terminal terminal = game.getTerminal();

        synchronized (terminal) {

            Coordinates oldCoord = coordinates.clone();

            coordinates.add(vector);

            Showable[][] map = game.getField().getMap();

            int x = coordinates.getX();
            int y = coordinates.getY();

            if (x >= 0 && y >= 0 && map[x][y] != null) {

                if (map[x][y] instanceof Wall
                        || map[x][y] instanceof Entrance
                        || (map[x][y] instanceof Exit && game.getField().getKeysLeft() > 0)) {

                    coordinates = oldCoord;
                    return;

                }

                if (map[x][y] instanceof Key) {

                    map[x][y] = null;
                    game.getField().pickUpKey();

                }

                if (map[x][y] instanceof Exit && game.getField().getKeysLeft() == 0) {

                    //TODO

                }

                if (map[x][y] instanceof Obstacle) {

                    damage();

                }

            }

            if (x < 0 || y < 0) {
                coordinates = oldCoord;
                return;
            }

            //redrawing
            game.getField().redraw(oldCoord);

            redraw();
        }

    }

    public String getHealth() {
        return Integer.toString(health);
    }

    public String getKeysLeft() {
        return Integer.toString(game.getField().getKeysLeft());
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void damage() {
        health--;
    }

}
