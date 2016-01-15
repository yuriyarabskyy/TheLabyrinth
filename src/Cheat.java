import com.googlecode.lanterna.terminal.Terminal;
import org.omg.PortableServer.THREAD_POLICY_ID;

/**
 * Created by yuriyarabskyy on 14/01/16.
 */
public class Cheat implements Runnable {

    private Game game;

    private Coordinates coordinates = null;

    private String route = "";

    Cheat(Game game) { this.game = game; }


    private void giveRoute() {

        Terminal terminal = game.getTerminal();

        Showable[] arrows = {new Arrow(terminal, 0), new Arrow(terminal, 1), new Arrow(terminal, 2), new Arrow(terminal, 3)};

        coordinates = game.getPlayer().getCoordinates().clone();

        route = Finder.searchKey(game.getPlayer().getCoordinates().clone(), game.getField().getMap(), game.getField().getKeysLeft());

        int x = coordinates.getX();
        int y = coordinates.getY();

        int screenX = screenX(x);
        int screenY = screenY(y);

        int columns = game.getTerminal().getTerminalSize().getColumns();
        int rows = game.getTerminal().getTerminalSize().getRows();

        Showable[][] map = game.getField().getMap();

        for (int i = 0; i < route.length() - 1; i++) {

            int which = 0;

            switch (route.charAt(i)) {

                case 'l':
                    x--;
                    screenX--;
                    if (x%3==0 && map[x][y] == null) {
                        map[x][y] = arrows[0];
                        which = 1;
                    }
                    break;
                case 'r':
                    x++;
                    screenX++;
                    if (x%3==0 && map[x][y] == null) {
                        map[x][y] = arrows[1];
                        which = 1;
                    }
                    break;
                case 'd':
                    y++;
                    screenY++;
                    if (y%2==0) {
                        map[x][y] = arrows[2];
                        which = 2;
                    }
                    break;
                case 'u':
                    y--;
                    screenY--;
                    if (y%2==0) {
                        map[x][y] = arrows[3];
                        which = 2;
                    }
                    break;
            }

            if ((which == 1 && x%3 == 0)||(which == 2 && y%2 == 0))
                if (onScreen(columns, rows, screenX,screenY)
                        && !DynamicObstacle.coordinatesList.contains(new Coordinates(x, y))) {
                    synchronized (terminal) {
                        terminal.moveCursor(screenX, screenY);
                        map[x][y].show();
                    }
                }

        }

    }

    private void clearOldRoute() {

        int x = coordinates.getX();
        int y = coordinates.getY();

        int screenX = screenX(x);
        int screenY = screenY(y);

        int columns = game.getTerminal().getTerminalSize().getColumns();
        int rows = game.getTerminal().getTerminalSize().getRows();

        Terminal terminal = game.getTerminal();

        Showable[][] map = game.getField().getMap();

        for (int i = 0; i < route.length() - 1; i++) {

            int which = 0;

            switch (route.charAt(i)) {

                case 'l':
                    x--;
                    screenX--;
                    which = 1;
                    break;
                case 'r':
                    x++;
                    screenX++;
                    which = 1;
                    break;
                case 'd':
                    y++;
                    screenY++;
                    which = 2;
                    break;
                case 'u':
                    y--;
                    screenY--;
                    which = 2;
                    break;
            }

            if ((which == 1 && x%3 == 0)||(which == 2 && y%2 == 0)) {
                map[x][y] = null;
                if (onScreen(columns, rows, screenX, screenY)
                        && !game.getPlayer().getCoordinates().equals(new Coordinates(x, y))
                        && !DynamicObstacle.coordinatesList.contains(new Coordinates(x, y))) {
                    synchronized (terminal) {
                        terminal.moveCursor(screenX, screenY);
                        terminal.applyBackgroundColor(Terminal.Color.BLUE);
                        terminal.putCharacter(' ');
                    }
                }
            }

        }

        route = "";

    }

    public int screenX(int x) { return x - game.getOffset().getX() + 1; }
    public int screenY(int y) { return y - game.getOffset().getY() + 1; }

    private boolean onScreen(int columns, int rows, int x, int y) {
        if (x > columns - 2 || y > rows - 5 || x <= 0 || y <= 0) return false;
        return true;
    }

    @Override
    public void run() {

        int counter = -1;

        while (!game.killCheat()) {

            counter = (counter + 1) % 5;

            if (counter == 0) {
                if (!route.isEmpty()) clearOldRoute();
                giveRoute();
            }


            try {
                Thread.sleep(250);
            } catch (Exception e) { e.printStackTrace(); }

        }

        clearOldRoute();

        game.setKillCheat(false);

    }

}
