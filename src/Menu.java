
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by yuriyarabskyy on 26/12/15.
 */
public class Menu implements Runnable{

    private static String[] options = {"MENU", "CONTINUE", "DOCUMENTATION", "SAVE LEVEL", "LOAD LEVEL", "EXIT"};
    //width and height are later dynamically calculated from the window size
    private int width = 0, height = 0;

    private Coordinates startRect = null;

    private int chosenOption = 1;

    private Game game;
    //saved levels are loaded from a properties file into a list
    private List<String> savedLevels = new LinkedList<>();
    //if kill, then close the menu
    private boolean kill = false;

    public Menu(Game game) {
        this.game = game;
    }

    //draws the menu window
    public void draw() {

        Terminal terminal = game.getTerminal();

        synchronized (game.getTerminal()) {

            width = terminal.getTerminalSize().getColumns() - 2;
            height = terminal.getTerminalSize().getRows() - 5;

            startRect = new Coordinates((int) (width * 0.3), (int) (height * 0.25));

            width -= 2 * startRect.getX();
            height -= 2 * startRect.getY();

            drawFrame();
            clearMenu();

            //drawing the line with the menu
            terminal.applyBackgroundColor(Terminal.Color.BLUE);
            terminal.moveCursor(startRect.getX() + 1, startRect.getY() + 1);
            for (int i = startRect.getX() + 1; i < startRect.getX() + width; i++) {
                terminal.putCharacter(' ');
            }
            writeOut(options[0], 0);
            //end of the line

            drawOptions();

        }

    }

    public void drawOptions() {
        Terminal terminal = game.getTerminal();
        //different options for the menu (draw them only)
        terminal.applyBackgroundColor(Terminal.Color.WHITE);
        for (int i = 1; i < options.length; i++) writeOut(options[i], i);

        //draw highlited option
        highlight(options[chosenOption], chosenOption);
    }

    public void drawFrame() {
        Terminal terminal = game.getTerminal();
        terminal.applyBackgroundColor(Terminal.Color.WHITE);
        terminal.applyForegroundColor(Terminal.Color.BLACK);
        //drawing the frame
        terminal.moveCursor(startRect.getX(),startRect.getY());
        for (int i = startRect.getX(); i <= startRect.getX() + width; i++) terminal.putCharacter('X');
        terminal.moveCursor(startRect.getX(),startRect.getY()+height);
        for (int i = startRect.getX(); i <= startRect.getX() + width; i++) terminal.putCharacter('X');
        terminal.moveCursor(startRect.getX(),startRect.getY());
        for (int i = startRect.getY() + 1; i <= startRect.getY() + height; i++) {
            terminal.moveCursor(startRect.getX(),i);
            terminal.putCharacter('X');
        }
        for (int i = startRect.getY() + 1; i <= startRect.getY() + height; i++) {
            terminal.moveCursor(startRect.getX() + width,i);
            terminal.putCharacter('X');
        }
    }

    public void clearMenu() {

        Terminal terminal = game.getTerminal();

        terminal.applyBackgroundColor(Terminal.Color.WHITE);
        //clear inside the frame
        for (int i = startRect.getX() + 1; i < startRect.getX() + width; i++) {
            for (int j = startRect.getY() + 1; j < startRect.getY() + height; j++) {
                terminal.moveCursor(i,j);
                terminal.putCharacter(' ');
            }
        }
    }

    public void writeOut(String option, int row) {
        Terminal terminal = game.getTerminal();
        //because 'MENU' label is actually on the first row
        terminal.moveCursor(startRect.getX() + width/2 + 1 - option.length()/2,startRect.getY() + row*2 + 1);
        for (int i = 0; i < option.length(); i++) terminal.putCharacter(option.charAt(i));
    }

    public void highlight(String option, int row) {
        Terminal terminal = game.getTerminal();
        terminal.applyBackgroundColor(Terminal.Color.RED);
        terminal.applyForegroundColor(Terminal.Color.WHITE);
        writeOut(option, row);
        terminal.applyBackgroundColor(Terminal.Color.WHITE);
        terminal.applyForegroundColor(Terminal.Color.BLACK);
    }

    //int direction means if > 0, the highlited area went down and vice versa
    public void rehighlight(String[] options, int index, int direction) {

        highlight(options[index], index);

        if (index == 1 && direction == 1) {
            writeOut(options[options.length-1], options.length-1);
            return;
        }

        if (index == options.length-1 && direction == -1) {
            writeOut(options[1],1);
            return;
        }

        writeOut(options[index - direction], index - direction);
    }

    //this method is used for the load menu option
    public void rehighlightForLoad(String[] options, int index, int direction, int elementNumber) {

        int index2 = index % elementNumber;

        if (index2 == 0) index2 = elementNumber;

        highlight(options[index], index2);

        if (index == 1 && direction == 1) {
            writeOut(options[elementNumber], elementNumber);
            return;
        }

        if (index == options.length-1 && direction == -1) {
            writeOut(options[options.length - elementNumber + 2],1);
            return;
        }

        writeOut(options[index - direction], index2 - direction);
    }

    ///after clicking enter and choosing an option
    public void choose(int index) {
        Terminal terminal = game.getTerminal();
        //Documentation
        if (index == 2) {
            clearMenu();
            writeOut("Green smiley - player", 0);
            writeOut("Star - key", 1);
            writeOut("Red - static obstacle", 2);
            writeOut("Yellow - dynamic obstacle", 3);
            writeOut("A - exit", 4);
            highlight("Back", 5);

            Key key = terminal.readInput();

            while (key == null || key.getKind() != Key.Kind.Enter) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) { e.printStackTrace(); }
                key = terminal.readInput();
            }

            draw();

            return;
        }
        //Save level
        if (index == 3) {
            //organizing the view
            clearMenu();

            writeOut("Name your saved level", 1);

            String blank = "                      ";
            terminal.applyBackgroundColor(Terminal.Color.CYAN);
            writeOut(blank, 2);

            writeOut("SAVE", 3);

            int x = startRect.getX() + width/2 + 1 - blank.length()/2;
            int y = startRect.getY() + 5;
            terminal.moveCursor(x, y);
            terminal.setCursorVisible(true);
            //end of the organization

            String file = "";

            Key key = terminal.readInput();

            while (file.isEmpty() || key == null || key.getKind() != Key.Kind.Enter) {

                try {
                    Thread.sleep(50);
                } catch (Exception e) { e.printStackTrace(); }

                key = terminal.readInput();

                if (key != null && key.getKind() == Key.Kind.NormalKey) {

                    if (file.length() == blank.length() - 1
                            || !Character.toString(key.getCharacter()).matches("([a-z])|([A-Z])|\\d")) continue;

                    if (file.isEmpty()) {
                        highlight("SAVE", 3);
                    }

                    terminal.moveCursor(x,y);
                    terminal.applyBackgroundColor(Terminal.Color.CYAN);
                    terminal.putCharacter(key.getCharacter());
                    x++;

                    file += key.getCharacter();
                }

                if (key != null && key.getKind() == Key.Kind.Backspace) {

                    if (file.isEmpty()) continue;

                    if (file.length() == 1) {
                        terminal.applyBackgroundColor(Terminal.Color.WHITE);
                        writeOut("SAVE", 3);
                    }

                    x--;
                    file = file.substring(0, file.length() - 1);

                    terminal.applyBackgroundColor(Terminal.Color.CYAN);
                    terminal.moveCursor(x, y);
                    terminal.putCharacter(' ');
                    terminal.moveCursor(x, y);

                }

                if (key != null && key.getKind() == Key.Kind.Escape) {
                    terminal.setCursorVisible(false);
                    draw();
                    return;
                }

            }

            //adding file to the saved ones
            if (!savedLevels.contains(file)) savedLevels.add(file);

            Properties propsForStore = new Properties();
            storeProperties(propsForStore);

            //saving the levels details
            //saved levels list is stored in levels.properties file
            //if the file doesn't exist, it gets created
            try {

                propsForStore.store(new FileOutputStream(new File("src/" + file + ".properties")), "Saved Level");

                //saving the level in the level list properties
                File levels = new File("src/levels.properties");

                if (levels.exists() && !levels.isDirectory()) {

                    //loading default properties
                    Properties propsDef = new Properties();

                    propsDef.load(new FileInputStream(levels));

                    //loading updated properties
                    String nameKey = "";
                    for (Object obj : propsDef.keySet()) {
                        if (propsDef.getProperty((String)obj).equals(file)) {
                            nameKey = (String)obj;
                            break;
                        }
                    }
                    if (nameKey.isEmpty()) {
                        int n = Integer.parseInt(propsDef.getProperty("Count")) + 1;
                        propsDef.setProperty("Count", Integer.toString(n));
                        propsDef.setProperty(Integer.toString(n), file);
                    }
                    else propsDef.setProperty(nameKey, file);

                    propsDef.store(new FileOutputStream(levels), "Updated levels");
                }
                //default levels don't exist, we create them
                else {

                    Properties propsDef = new Properties();

                    propsDef.setProperty("Count", "1");
                    propsDef.setProperty("1", file);

                    propsDef.store(new FileOutputStream(levels), "Updated levels");

                }

            } catch (Exception e) { e.printStackTrace(); }


            terminal.setCursorVisible(false);
            draw();
            return;
        }


        //Load level
        if (index == 4) {

            clearMenu();

            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(new File("src/levels.properties")));
            } catch (Exception e) { properties = null; }

            if (properties == null) {

                highlight("No saved levels available", 1);

                while (true) {

                    try {
                        Thread.sleep(50);
                    } catch (Exception e) { e.printStackTrace(); }

                    Key key = terminal.readInput();

                    if (key != null) {
                        draw();
                        return;
                    }

                }

            }
            //the number of saved levels
            int n = Integer.parseInt(properties.getProperty("Count"));
            //the number of pages needed
            int pageCount = (n * 2) / (height - 4) + 1;
            //number of elements on one of the pages
            int elementNumber = (height - 4) / 2;

            String[] options = new String[n + 1];

            for (int i = 1; i < options.length; i++) {

                String level = properties.getProperty(Integer.toString(i));

                options[i] = level;

            }

            int pageNumber = 1;

            printLoadPage(pageNumber, elementNumber, options, pageCount);

            highlight(options[1], 1);

            int chosenOption = 1;

            Key key = terminal.readInput();

            while (key == null || key.getKind() != Key.Kind.Escape) {

                try {
                    Thread.sleep(50);
                } catch (Exception e) { e.printStackTrace(); }

                key = terminal.readInput();

                if (key != null && key.getKind() == Key.Kind.ArrowDown) {
                    if (options.length == 2) continue;
                    chosenOption++;
                    if (chosenOption >= options.length) {
                        chosenOption = 1;
                        pageNumber = 1;
                        printLoadPage(pageNumber, elementNumber, options, pageCount);
                        highlight(options[chosenOption], 1);
                        continue;
                    }
                    if (chosenOption > pageNumber*elementNumber) {
                        pageNumber++;
                        printLoadPage(pageNumber, elementNumber, options, pageCount);
                        highlight(options[chosenOption], 1);
                        continue;
                    }
                    rehighlightForLoad(options, chosenOption, 1, elementNumber);
                }

                if (key != null && key.getKind() == Key.Kind.ArrowUp) {
                    if (options.length == 2) continue;
                    chosenOption--;
                    if (chosenOption <= 0) {
                        chosenOption = options.length - 1;
                        pageNumber = pageCount;
                        printLoadPage(pageNumber, elementNumber, options, pageCount);
                        highlight(options[chosenOption], options.length - (pageCount - 1)*elementNumber - 1);
                        continue;
                    }
                    if (chosenOption <= (pageNumber-1)*elementNumber) {
                        pageNumber--;
                        printLoadPage(pageNumber, elementNumber, options, pageCount);
                        highlight(options[chosenOption], elementNumber);
                        continue;
                    }
                    rehighlightForLoad(options, chosenOption, -1, elementNumber);
                }

                if (key != null && key.getKind() == Key.Kind.Enter) {

                    String chosenLevel = options[chosenOption];

                    Properties newLevelProp = new Properties();

                    try {
                        newLevelProp.load(new FileInputStream(new File("src/" + chosenLevel + ".properties")));
                    } catch (Exception e) { e.printStackTrace(); }

                    loadLevel(newLevelProp);

                    kill = true;

                    return;

                }

            }

            draw();

        }
    }

    //used for load option window, prints out the pageNumber page of the saved levels list
    private void printLoadPage(int pageNumber, int elementNumber, String[] options, int pageCount) {

        writeOut("Press Escape to get back | Page " + pageNumber + '\\' + pageCount +  " \u21c4", 0);

        for (int i = (pageNumber - 1)*elementNumber + 1; i <= pageNumber*elementNumber; i++) {

            int sub = (pageNumber - 1)*elementNumber;

            writeOut("                       ", i - sub);

            if (i < options.length) {

                writeOut(options[i], i - sub);

            }
        }

    }

    //load level from a chosen properties object
    public void loadLevel(Properties properties) {

        int health = 3;

        if (properties.containsKey("Health")) {
            health = Integer.parseInt(properties.getProperty("Health"));
            properties.remove("Health");
        }

        Coordinates playerCoord = null;

        if (properties.containsKey("PlayerCoordinates")) {
            playerCoord = new Coordinates(properties.getProperty("PlayerCoordinates"));
            properties.remove("PlayerCoordinates");
        }

        Coordinates offset = new Coordinates(0,0);

        if (properties.containsKey("Offset")) {
            offset = new Coordinates(properties.getProperty("Offset"));
            properties.remove("Offset");
        }

        List<DynamicObstacle> dynamicObstacleList = new LinkedList<>();

        game.setDynobList(dynamicObstacleList);
        game.setStartingPoint(offset);
        game.setProperties(properties);

        Field newField = new Field(game);
        newField.drawBorder();

        if (playerCoord == null) playerCoord = newField.getEntrance();

        Player newPlayer = new Player(game, playerCoord, health);

        game.setField(newField);
        game.setPlayer(newPlayer);

    }

    //save the current level and its properties into a properties object
    public void storeProperties(Properties properties) {

        properties.setProperty("Health", Integer.toString(game.getPlayer().getHealth()));
        properties.setProperty("PlayerCoordinates", game.getPlayer().getCoordinates().toString());
        properties.setProperty("Offset", game.getOffset().toString());


        Showable[][] map = game.getField().getMap();

        properties.setProperty("Height", Integer.toString(map[0].length));
        properties.setProperty("Width", Integer.toString(map.length));

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != null) {
                    String key = new Coordinates(i,j).toString();
                    if (map[i][j] instanceof Wall)           properties.setProperty(key,"0"); else
                    if (map[i][j] instanceof Entrance)       properties.setProperty(key,"1"); else
                    if (map[i][j] instanceof Exit)           properties.setProperty(key,"2"); else
                    if (map[i][j] instanceof StaticObstacle) properties.setProperty(key,"3");
                    else properties.setProperty(key,"5");
                }
            }
        }

        for (DynamicObstacle obj : game.getDynobList()) {
            properties.setProperty(obj.getCoordinates().toString(),"4");
        }

    }

    //get's called when the respective thread is started
    public void run() {

        Terminal terminal = game.getTerminal();

        chosenOption = 1;

        draw();

        kill = false;

        while (true) {

            try {
                Thread.sleep(50);
            } catch (Exception e) { e.printStackTrace(); }

            Key key = terminal.readInput();

            if (key != null && key.getKind() == Key.Kind.ArrowDown) {
                chosenOption++;
                if (chosenOption == options.length) chosenOption = 1;
                rehighlight(options, chosenOption, 1);
            }

            if (key != null && key.getKind() == Key.Kind.ArrowUp) {
                chosenOption--;
                if (chosenOption == 0) chosenOption = options.length - 1;
                rehighlight(options, chosenOption, -1);
            }

            if (key != null && key.getKind() == Key.Kind.Enter) {
                if (chosenOption == 1) {
                    if (game.getPlayer().getHealth() == 0) continue;
                    break;
                }
                //exit the game
                if (chosenOption == options.length - 1) {
                    game.setCloseGame(true);
                    break;
                }
                choose(chosenOption);
            }

            if (key != null && key.getKind() == Key.Kind.Escape) break;

            if (kill) break;

        }

        game.getField().drawBorder();
        game.getField().redraw();
        game.getPlayer().redraw();
        game.getStats().redraw();
        Game.focusScreen(game.getTerminal().getTerminalSize(),game.getPlayer(),game.getOffset(),game.getField());

    }

}
