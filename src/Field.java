
/**
 * Created by yuriyarabskyy on 25/12/15.
 */

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * map and methods for displaying it in the terminal
 */
public class Field {

    private Game game;

    private Showable[][] map;

    private int keysLeft = 0;

    private List<Coordinates> entrances = new LinkedList<>();

    //list of all the static objects
    private static Showable[] objects = new Showable[6];
    //dynamic obstacles have to be kept in a separate list

    public Field(Game game) {

        this.game = game;

        objects[0] = new Wall(game.getTerminal());
        objects[1] = new Entrance(game.getTerminal());
        objects[2] = new Exit(game.getTerminal());
        objects[3] = new StaticObstacle(game.getTerminal());
        objects[5] = new Key(game.getTerminal());

        //instantiating the map
        int height = Integer.parseInt(game.getProperties().getProperty("Height"));
        int width  = Integer.parseInt(game.getProperties().getProperty("Width"));

        game.getProperties().remove("Height");
        game.getProperties().remove("Width");

        map = new Showable[width][height];
        //filling up the map with objects from the properties
        for (Object key : game.getProperties().keySet()) {

            Coordinates coord;

            if (((String)key).matches("\\d+,\\d+")) {

                coord = new Coordinates(key);

            } else continue;

            int ind = Integer.parseInt(game.getProperties().getProperty(coord.toString()));

            Showable obj = null;

            if (ind == 1) {
                entrances.add(coord);
            }

            if (ind == 5) keysLeft++;

            if (ind != 4)
                obj = objects[ind];
            else
                game.getDynobList().add(new DynamicObstacle(game.getTerminal(), coord));

            map[coord.getX()][coord.getY()] = obj;

        }
        //if the field is null, it means there's nothing on it

    }

    public int getKeysLeft() {
        return keysLeft;
    }

    public void pickUpKey() {
        keysLeft--;
        game.getStats().redraw();
    }

    public Showable[][] getMap() { return map; }

    public Coordinates getEntrance() {
        return entrances.get((int)(Math.random()*entrances.size()));
    }

    public void drawBorder() {

        Terminal terminal = game.getTerminal();

        terminal.applyBackgroundColor(Terminal.Color.RED);

        terminal.moveCursor(0,0);
        for (int i = 0; i < terminal.getTerminalSize().getColumns(); i++)
            terminal.putCharacter(' ');

        terminal.moveCursor(0, terminal.getTerminalSize().getRows() - 4);
        for (int i = 0; i < terminal.getTerminalSize().getColumns(); i++)
            terminal.putCharacter(' ');

        for (int i = 1; i < terminal.getTerminalSize().getRows() - 4; i++) {
            terminal.moveCursor(0,i);
            terminal.putCharacter(' ');
            terminal.moveCursor(terminal.getTerminalSize().getColumns() - 1, i);
            terminal.putCharacter(' ');
        }

        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);

    }

    //redraws the map in the terminal beginning at the starting point
    public void redraw() {
        Terminal terminal = game.getTerminal();
        Coordinates startingPoint = game.getOffset();
        for (int i = 0; i < terminal.getTerminalSize().getColumns() - 2; i++) {
            //if (i + startingPoint.getX() >= map.length) continue;
            for (int j = 0; j < terminal.getTerminalSize().getRows() - 5; j++) {
                //if (j + startingPoint.getY() >= map[0].length) break;
                terminal.moveCursor(i + 1, j + 1);
                if (!(i + startingPoint.getX() >= map.length)
                        && i + startingPoint.getX() < map.length
                        && i >= -startingPoint.getX()
                        && !(j + startingPoint.getY() >= map[0].length)
                        && j >= -startingPoint.getY()
                        && j + startingPoint.getY() < map[0].length
                        && map[i + startingPoint.getX()][j + startingPoint.getY()] != null)
                            map[i + startingPoint.getX()][j + startingPoint.getY()].show();
                else {
                    terminal.applyBackgroundColor(Terminal.Color.BLUE);
                    terminal.putCharacter(' ');
                    terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                }
            }
        }
    }

    //redraw a specific field
    public void redraw(Coordinates field) {
        Terminal terminal = game.getTerminal();
        Coordinates startingPoint = game.getOffset();
        int x = field.getX();
        int y = field.getY();
        terminal.moveCursor(x - startingPoint.getX() + 1,y - startingPoint.getY() + 1);
        if (x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[x][y] != null) map[x][y].show();
        else {
            terminal.applyBackgroundColor(Terminal.Color.BLUE);
            terminal.putCharacter(' ');
            terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
        }
    }

}
