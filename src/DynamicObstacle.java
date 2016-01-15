
import com.googlecode.lanterna.terminal.Terminal;

import java.util.*;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class DynamicObstacle extends Obstacle{

    public static Set<Coordinates> coordinatesList;

    static {
        coordinatesList = new HashSet<>();
    }

    private static final Coordinates[] directions = {new Coordinates(1,0),new Coordinates(0,1),new Coordinates(-1,0),new Coordinates(0,-1)};
    public static int    difficultyLevel = 11;


    private int pathIndex = 0;
    private String pathToPlayer = "";

    public DynamicObstacle(Terminal terminal, Coordinates coordinates) {

        super(terminal, coordinates);

        coordinatesList.add(coordinates);

    }

    public void show() {

        terminal.moveCursor(coordinates.getX(), coordinates.getY());

        terminal.applyBackgroundColor(Terminal.Color.YELLOW);

        terminal.putCharacter(' ');

    }

    public void move(Field field, Coordinates offset, Player player) {

            //calculating the relative x and y for the screen
            int x = coordinates.getX() - offset.getX() + 1;
            int y = coordinates.getY() - offset.getY() + 1;

            boolean onScreen = onScreen(x, y);

            if (!onScreen && pathToPlayer.isEmpty()) return;

            coordinatesList.remove(coordinates);

            synchronized (terminal) {
                if (onScreen) {
                    if (player.getCoordinates().equals(coordinates))
                        player.redraw();
                    else
                        field.redraw(coordinates);
                }
            }
            getNextCoordinate(player.getCoordinates(), field);

            x = coordinates.getX() - offset.getX() + 1;
            y = coordinates.getY() - offset.getY() + 1;

            coordinatesList.add(coordinates);

            synchronized (terminal) {
                if (onScreen(x, y))
                    drawField(x, y, Terminal.Color.YELLOW);
            }

            tryHit(player);

    }

    //uses breadth-first search in its core to find the next move
    private void getNextCoordinate(Coordinates playerCoordinates, Field field) {

            String pathNew = "";

            if (pathIndex == pathToPlayer.length() / 2 || pathIndex == pathToPlayer.length() * 0.25 || pathToPlayer.isEmpty()
                    || pathIndex == pathToPlayer.length() * 0.75 || pathIndex >= pathToPlayer.length())
                pathNew = Finder.searchPath(coordinates, playerCoordinates, field.getMap(), difficultyLevel);

            if (!pathNew.isEmpty()) {
                pathToPlayer = pathNew;
                pathIndex = 0;
            }

            if (!pathToPlayer.isEmpty() && pathIndex < pathToPlayer.length()) {
                switch (pathToPlayer.charAt(pathIndex)) {
                    case 'r':
                        coordinates.add(directions[0]);
                        break;
                    case 'd':
                        coordinates.add(directions[1]);
                        break;
                    case 'l':
                        coordinates.add(directions[2]);
                        break;
                    case 'u':
                        coordinates.add(directions[3]);
                        break;
                    default:
                        coordinates.add(directions[0]);
                }

                pathIndex++;
            }

        else {

            double minDist = Double.MAX_VALUE;
            int minDir = 0;

            for (int i = 0; i < 4; i++) {

                Coordinates vect = coordinates.clone().add(directions[i]);

                if (vect.getX() > 0 && vect.getX() < field.getMap().length &&
                        vect.getY() > 0 && field.getMap()[vect.getX()][vect.getY()] == null
                        && minDist > vect.distTo(playerCoordinates)) {
                    minDist = vect.distTo(playerCoordinates);
                    minDir = i;
                }

            }

            coordinates.add(directions[minDir]);
        }
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
