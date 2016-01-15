
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

/**
 * main class, which controls the whole flow of the game
 * implements a listener, so when the window is resized
 * the game gets automatically adjusted to the new size
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
    //a thread which is activated every time a menu is called
    private Thread menuController = null;
    //a list of dynamic obstacles
    private List<DynamicObstacle> dynobList = new LinkedList<DynamicObstacle>();
    //if the menu is opened, pause is activated
    private boolean pause = false;
    //if the game is about to be closed
    private boolean closeGame = false;
    //to stop the cheat
    private boolean killCheat = false;

    private Thread CheatThread = null;


    //getters
    public Terminal   getTerminal() {
        return terminal;
    }
    public Properties getProperties() {
        return properties;
    }
    public Menu       getMenu() {
        return menu;
    }
    public List<DynamicObstacle> getDynobList() {
        return dynobList;
    }
    public Coordinates getOffset() { return startingPoint; }
    public boolean getPause() { return pause; }
    public Field getField() {
        return field;
    }
    public Player getPlayer() { return player; }
    public Stats getStats() {
        return stats;
    }
    public boolean killCheat() { return killCheat; }
    public Thread getCheatThread() {
        return CheatThread;
    }

    //setters
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    public void setCloseGame(boolean closeGame) { this.closeGame = closeGame; }
    public void setField(Field field) {
        this.field = field;
    }
    public void setStartingPoint(Coordinates startingPoint) {
        this.startingPoint = startingPoint;
    }
    public void setDynobList(List<DynamicObstacle> dynobList) { this.dynobList = dynobList; }
    public void setPlayer(Player player) { this.player = player; }
    public void setPause(boolean pause) { this.pause = pause; }
    public void setKillCheat(boolean killCheat) {
        if (CheatThread != null && CheatThread.isAlive())
        this.killCheat = killCheat;
    }

    //that's where the logic of the game is situated at
    public void go() {
        //initializing all variables
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

        Thread dController = new Thread(new DynamicObstacleController(this));
        dController.start();
        //end of the initialization

        //focusing the window on the player and drawing everything
        focusScreen(terminal.getTerminalSize(), player, startingPoint, field);
        field.drawBorder();
        field.redraw();
        player.redraw();
        stats.redraw();

        //game loop
        while (!closeGame) {

            //time till the next possible move
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //if player won
            if (player.isWon() && isMenuClosed()) {

                Key key = terminal.readInput();

                if (key != null) {
                    menuController = new Thread(menu);
                    menuController.start();
                }

            }

            //if player lost
            if (player.getHealth() <= 0 && isMenuClosed()) {
                Key key = terminal.readInput();

                if (key != null) {
                    menuController = new Thread(menu);
                    menuController.start();
                }

            }

            //if the game is still on and menu is closed
            if (player.getHealth() > 0 && !player.isWon() && isMenuClosed()) {
                //read next key
                Key key = terminal.readInput();

                //for the dynamic objects
                pause = false;

                //make a move if a respective key was pressed
                //and focus the screen if the player has reached the border of the window
                if (checkMakeMove(player, key)) {
                    focusScreen(terminal.getTerminalSize(), player, startingPoint, field);
                }

                if (key != null && key.getKind() == Key.Kind.NormalKey) {

                    if (key.getCharacter() == 'c' && (CheatThread == null || !CheatThread.isAlive())) {

                        CheatThread = new Thread(new Cheat(this));
                        CheatThread.start();

                    }

                    if (key.getCharacter() == 'v') {
                         setKillCheat(true);
                    }

                }

                //open the menu on escape
                if (key != null && key.getKind() == Key.Kind.Escape) {

                    pause = true;
                    menuController = new Thread(menu);
                    menuController.start();

                }

            }
            //else if menu is open or the end of the game, pause all the moving objects
            else pause = true;

        }

        killCheat = true;

        terminal.exitPrivateMode();

    }

    public boolean isMenuClosed() { return menuController == null || !menuController.isAlive(); }

    public boolean isCloseGame() { return closeGame; }

    //check and make a move
    private static boolean checkMakeMove(Player player, Key key) {

        if (key != null) {
            if (key.getKind() == Key.Kind.ArrowRight) player.move(RIGHT);
            if (key.getKind() == Key.Kind.ArrowLeft)  player.move(LEFT);
            if (key.getKind() == Key.Kind.ArrowUp)    player.move(UP);
            if (key.getKind() == Key.Kind.ArrowDown)  player.move(DOWN);
            return true;
        }

        return false;
    }

    //if resized, redraw all the objects
    public void onResized(TerminalSize newSize) {

        terminal.clearScreen();

        field.drawBorder();

        field.redraw();

        player.redraw();

        stats.redraw();

        menu.calculateFrame();

        if (!isMenuClosed()) {
            if (menu.isOptionChosen())
            switch (menu.getChosenOption()) {
                case 2: menu.drawDocumentation(); break;
                case 3: menu.drawSaveMenu(); break;
                case 4: menu.drawLoadMenu(); break;
            }
            else menu.draw();
        }

        focusScreen(newSize, player, startingPoint, field);

    }

    //verifying if the player is in the borders of the window
    //and changing the offset of the screen if necessary
    public static void focusScreen(TerminalSize terminalSize, Player player, Coordinates startingPoint, Field field) {

        boolean changed = false;

        int maxRight     = field.getMap().length - startingPoint.getX();
        int maxBottom    = field.getMap()[0].length - startingPoint.getY();
        int windowWidth  = terminalSize.getColumns() - 4;
        int windowHeight = terminalSize.getRows() - 7;
        Coordinates vector = new Coordinates(player.getCoordinates().getX() - startingPoint.getX(), player.getCoordinates().getY() - startingPoint.getY());

        //right
        if (vector.getX() > windowWidth - 1 && maxRight > windowWidth) {
            if (field.getMap().length - 3 < player.getCoordinates().getX())
                startingPoint.setX(player.getCoordinates().getX() - windowWidth);
            else
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
            if (field.getMap()[0].length - 3 < player.getCoordinates().getY())
                startingPoint.setY(player.getCoordinates().getY() - windowHeight);
            else
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

    //starts up the game
    public static void main (String[] args) {
        Game game = new Game();
        game.go();
    }

}
