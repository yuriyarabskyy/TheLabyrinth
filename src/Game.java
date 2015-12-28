import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Game implements Terminal.ResizeListener {

    private static final Coordinates UP    = new Coordinates(0,-1);
    private static final Coordinates DOWN  = new Coordinates(0, 1);
    private static final Coordinates LEFT  = new Coordinates(-1,0);
    private static final Coordinates RIGHT = new Coordinates(1, 0);

    public static String loadFile = "src/level.properties";

    private Properties properties = null;

    private Player player = null;

    private Stats stats = null;

    private Menu menu = null;

    private Terminal terminal = null;

    private Field field = null;

    //where we start drawing the map
    public Coordinates startingPoint = new Coordinates(0,0);

    private Thread menuController = null;

    private List<DynamicObstacle> dynobList = new LinkedList<DynamicObstacle>();


    public Terminal getTerminal() {
        return terminal;
    }

    public Properties getProperties() {
        return properties;
    }

    public Menu getMenu() {
        return menu;
    }

    public List<DynamicObstacle> getDynobList() {
        return dynobList;
    }

    public Coordinates getOffset() {

        return startingPoint;
    }

    public Field getField() {
        return field;
    }

    public Player getPlayer() {

        return player;
    }

    public Stats getStats() {
        return stats;
    }


    public static void main(String[] args) {
        Game game = new Game();
        game.go();
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void go() {

        properties = new Properties();

        try {
        properties.load(new FileInputStream(loadFile));
        }
        catch (IOException e) { e.printStackTrace(); }

        terminal = TerminalFacade.createSwingTerminal();

        terminal.setCursorVisible(false);

        terminal.addResizeListener(this);

        terminal.enterPrivateMode();

        field = new Field(this);
        //for the garbage collector to free space
        properties = null;

        player = new Player(this, field.getEntrance(), 3);

        stats = new Stats(this);

        menu = new Menu(this);

        Pauser pauser = new Pauser();

        Thread dController = new Thread(new DynamicObstacleController(this, pauser));
        dController.start();

        focusScreen(terminal.getTerminalSize(), player, startingPoint, field);

        field.drawBorder();

        field.redraw();

        player.redraw();

        stats.redraw();

        while (Integer.parseInt(player.getHealth()) > 0) {

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (menuController == null || !menuController.isAlive()) {

                Key key = terminal.readInput();

                //for the dynamic objects
                pauser.stop = false;

                if (checkMove(player, key, startingPoint)) {
                    stats.redraw();
                    focusScreen(terminal.getTerminalSize(), player, startingPoint, field);
                }

                if (key != null && key.getKind() == Key.Kind.Escape) {

                    menuController = new Thread(menu);
                    menuController.start();

                }
            }
            else pauser.stop = true;

        }

        terminal.exitPrivateMode();

    }

    private static boolean checkMove(Player player, Key key, Coordinates offset) {

        if (key != null) {
            if (key.getKind() == Key.Kind.ArrowRight) player.move(RIGHT,offset);
            if (key.getKind() == Key.Kind.ArrowLeft)  player.move(LEFT, offset);
            if (key.getKind() == Key.Kind.ArrowUp)    player.move(UP,   offset);
            if (key.getKind() == Key.Kind.ArrowDown)  player.move(DOWN, offset);
            return true;
        }
        return false;
    }

    public void onResized(TerminalSize newSize) {

        field.drawBorder();

        field.redraw();

        player.redraw();

        stats.redraw();

        if (menuController != null && menuController.isAlive())
            menu.draw();

        focusScreen(newSize, player, startingPoint, field);

    }

    public static void focusScreen(TerminalSize terminalSize, Player player, Coordinates startingPoint, Field field) {

        boolean changed = false;

        int maxRight     = field.getMap().length - startingPoint.getX();
        int maxBottom    = field.getMap()[0].length - startingPoint.getY();
        int windowWidth  = terminalSize.getColumns() - 4;
        int windowHeight = terminalSize.getRows() - 7;
        Coordinates vector = new Coordinates(player.getCoordinates().getX() - startingPoint.getX(), player.getCoordinates().getY() - startingPoint.getY());

        //right
        if (vector.getX() > windowWidth - 1 && maxRight > windowWidth) {
            startingPoint.setX(player.getCoordinates().getX() - 2);
            changed = true;
        }

        //left
        if (vector.getX() < 2 && startingPoint.getX() > 0) {
            startingPoint.setX(player.getCoordinates().getX() - windowWidth + 1);
            if (startingPoint.getX() < 0) startingPoint.setX(0);
            changed = true;
        }

        //down
        if (vector.getY() > windowHeight && maxBottom > windowHeight) {
            startingPoint.setY(player.getCoordinates().getY() - 1);
            changed = true;
        }

        //up
        if (vector.getY() < 1 && startingPoint.getY() > 0) {
            startingPoint.setY(player.getCoordinates().getY() - windowHeight);
            if (startingPoint.getY() < 0) startingPoint.setY(0);
            changed = true;
        }

        if (changed) {
            field.drawBorder();
            field.redraw();
            player.redraw();
        }
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setStartingPoint(Coordinates startingPoint) {
        this.startingPoint = startingPoint;
    }

    public void setDynobList(List<DynamicObstacle> dynobList) {

        this.dynobList = dynobList;
    }

    public void setPlayer(Player player) {

        this.player = player;
    }

}
