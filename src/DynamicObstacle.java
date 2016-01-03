
import com.googlecode.lanterna.terminal.Terminal;

import java.util.*;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class DynamicObstacle extends Obstacle{

    private static final Coordinates[] directions = {new Coordinates(1,0),new Coordinates(0,1),new Coordinates(-1,0),new Coordinates(0,-1)};
    private static final Character[] directionsCh = {'r','d','l','u'};
    public static int    difficultyLevel = 50;

    private String pathToPlayer = "";

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

            if (onScreen(x, y))
            drawField(x, y, Terminal.Color.BLUE);

            getNextCoordinate(player.getCoordinates(), field);

            x = coordinates.getX() - offset.getX() + 1;
            y = coordinates.getY() - offset.getY() + 1;

            if (onScreen(x, y))
            drawField(x, y, Terminal.Color.YELLOW);

            tryHit(player);

        }

    }

    //uses breadth-first search in its core to find the next move
    private void getNextCoordinate(Coordinates playerCoordinates, Field field) {

        String pathBackUp = pathToPlayer;

        pathToPlayer = searchPath(playerCoordinates, field.getMap());

        if (pathToPlayer.isEmpty() && pathBackUp.isEmpty()) {

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
        } else if (pathToPlayer.isEmpty()) moveWith(pathBackUp);
            else {
            moveWith(pathToPlayer);
        }
    }

    private void moveWith(String path) {
        switch (path.charAt(0)) {
            case 'r' : coordinates.add(directions[0]); return;
            case 'd' : coordinates.add(directions[1]); return;
            case 'l' : coordinates.add(directions[2]); return;
            case 'u' : coordinates.add(directions[3]); return;
            default:   coordinates.add(directions[0]); return;
        }
    }

    private String searchPath(Coordinates playerCoordinates, Showable[][] map) {

        Queue<Pair> queue = new LinkedList<>();

        Set<Coordinates> visitedCoords = new HashSet<>();

        for (int i = 0; i < 4; i++) {

            Coordinates vect = coordinates.clone().add(directions[i]);

            if (vect.getX() > 0 && vect.getX() < map.length &&
                    vect.getY() > 0 && map[vect.getX()][vect.getY()] == null) {

                Pair pair = new Pair(vect, Character.toString(directionsCh[i]));
                queue.add(pair);
                visitedCoords.add(vect);

            }

        }

        if (queue.isEmpty()) return "";

        for (int i = 0; i < difficultyLevel*200; i++) {

            if (queue.isEmpty()) return "";

            Pair next = queue.remove();

            if (next.coordinates.equals(playerCoordinates)) return next.path;

            for (int j = 0; j < 4; j++) {

                Coordinates vect = next.coordinates.clone().add(directions[j]);

                if (vect.getX() > 0 && vect.getX() < map.length && !visitedCoords.contains(vect) &&
                        vect.getY() > 0 && vect.getY() < map[0].length && map[vect.getX()][vect.getY()] == null) {

                    Pair pair = new Pair(vect, next.path + directionsCh[j]);
                    queue.add(pair);
                    visitedCoords.add(vect);

                }

            }


        }

        return "";

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

    public class Pair {

        public Coordinates coordinates;

        public String path;

        public Pair(Coordinates coordinates, String path) {
            this.coordinates = coordinates;
            this.path = path;
        }
    }

}
