
import com.googlecode.lanterna.terminal.Terminal;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class DynamicObstacle extends Obstacle{

    private static final int RIGHT = 0;
    private static final int DOWN  = 1;
    private static final int LEFT  = 2;
    private static final int UP    = 3;

    private static final Coordinates[] directions = {new Coordinates(1,0),new Coordinates(0,1),new Coordinates(-1,0),new Coordinates(0,-1)};

    public DynamicObstacle(Terminal terminal, Coordinates coordinates) {
        super(terminal, coordinates);
    }

    public void show() {

        terminal.moveCursor(coordinates.getX(), coordinates.getY());

        terminal.applyForegroundColor(Terminal.Color.MAGENTA);

        terminal.applyBackgroundColor(Terminal.Color.BLUE);

        terminal.putCharacter('D');

    }

    public void move(Field field, Coordinates offset, Player player, Stats stats) {

        synchronized (terminal) {

            Coordinates coord;

            //calculating the relative x and y for the screen
            int x = coordinates.getX() - offset.getX() + 1;
            int y = coordinates.getY() - offset.getY() + 1;

            if (!onScreen(x, y)) return;

            drawField(x, y, Terminal.Color.BLUE);

            coordinates = getNextCoordinate(player.getCoordinates(), field);

            /*
            do {

                int rand = (int) (Math.random() * 4);

                coord = coordinates.clone().add(directions[rand]);

            } while (field.getMap()[coord.getX()][coord.getY()] != null);

            coordinates = coord;
            */

            x = coordinates.getX() - offset.getX() + 1;
            y = coordinates.getY() - offset.getY() + 1;

            if (!onScreen(x, y)) return;

            drawField(x, y, Terminal.Color.YELLOW);

            tryHit(player);

        }

    }

    //uses breadth-first search in its core to find the next move
    private Coordinates getNextCoordinate(Coordinates playerCoordinates, Field field) {

        Queue<Integer> queue = new LinkedList<>();

        return new Coordinates(0,0);

    }

    private boolean onScreen(int x, int y) {
        if (x > terminal.getTerminalSize().getColumns() - 2 ||
                y > terminal.getTerminalSize().getRows() - 5 || x <= 0 || y <= 0) return false;
        return true;
    }

    private void drawField(int x, int y, Terminal.Color color) {

        terminal.applyBackgroundColor(color);

        terminal.moveCursor(x, y);

        terminal.putCharacter(' ');
    }

}
