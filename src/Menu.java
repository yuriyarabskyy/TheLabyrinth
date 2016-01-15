
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
    private boolean isOptionChosen = false;

    private Game game;
    //saved levels are loaded from a properties file into a list
    private List<String> savedLevels = new LinkedList<>();
    //if kill, then close the menu
    private boolean kill = false;
    private final String blank = "                      ";

    private int loadLevelsCount = 0;
    private int loadLevelChosenOption = 1;
    private int loadLevelPageNumber = 1;
    private String[] loadLevelOptions;

    public Menu(Game game) {
        this.game = game;
        calculateFrame();
    }

    public int getChosenOption() { return chosenOption; }


    public void calculateFrame() {
        width = game.getTerminal().getTerminalSize().getColumns() - 2;
        height = game.getTerminal().getTerminalSize().getRows() - 5;

        startRect = new Coordinates((int) (width * 0.3), (int) (height * 0.25));

        width -= 2 * startRect.getX();
        height -= 2 * startRect.getY();
    }

    public boolean isOptionChosen() { return isOptionChosen; }

    //draws the menu window
    public void draw() {

        Terminal terminal = game.getTerminal();

        synchronized (game.getTerminal()) {

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
        synchronized (game.getTerminal()) {
            terminal.applyBackgroundColor(Terminal.Color.WHITE);
            terminal.applyForegroundColor(Terminal.Color.BLACK);
            //drawing the frame
            terminal.moveCursor(startRect.getX(), startRect.getY());
            for (int i = startRect.getX(); i <= startRect.getX() + width; i++) terminal.putCharacter('X');
            terminal.moveCursor(startRect.getX(), startRect.getY() + height);
            for (int i = startRect.getX(); i <= startRect.getX() + width; i++) terminal.putCharacter('X');
            terminal.moveCursor(startRect.getX(), startRect.getY());
            for (int i = startRect.getY() + 1; i <= startRect.getY() + height; i++) {
                terminal.moveCursor(startRect.getX(), i);
                terminal.putCharacter('X');
            }
            for (int i = startRect.getY() + 1; i <= startRect.getY() + height; i++) {
                terminal.moveCursor(startRect.getX() + width, i);
                terminal.putCharacter('X');
            }
        }
    }

    public void clearMenu() {

        Terminal terminal = game.getTerminal();

        synchronized (game.getTerminal()) {
            terminal.applyBackgroundColor(Terminal.Color.WHITE);
            //clear inside the frame
            for (int i = startRect.getX() + 1; i < startRect.getX() + width; i++) {
                for (int j = startRect.getY() + 1; j < startRect.getY() + height; j++) {
                    terminal.moveCursor(i, j);
                    terminal.putCharacter(' ');
                }
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

    public void drawDocumentation() {

        clearMenu();
        drawFrame();
        writeOut("White - player", 0);
        writeOut("K - key", 1);
        writeOut("Cyan - static obstacle", 2);
        writeOut("Yellow - dynamic obstacle", 3);
        writeOut("Green - exit", 4);
        highlight("Back", 5);

    }

    private void controlDocumentation() {

        Terminal terminal = game.getTerminal();

        Key key = terminal.readInput();

        while (key == null || key.getKind() != Key.Kind.Enter) {
            try {
                Thread.sleep(50);
            } catch (Exception e) { e.printStackTrace(); }
            key = terminal.readInput();
        }

        draw();

    }

    public void drawSaveMenu() {

        Terminal terminal = game.getTerminal();

        //organizing the view
        clearMenu();
        drawFrame();

        writeOut("Name your saved level", 1);

        terminal.applyBackgroundColor(Terminal.Color.CYAN);
        writeOut(blank, 2);

        writeOut("SAVE", 3);

        terminal.moveCursor(calculateX(), calculateY());
        terminal.setCursorVisible(true);
        //end of the organization

    }

    //for the save menu
    private int calculateX() { return startRect.getX() + width/2 + 1 - blank.length()/2; }
    private int calculateY() { return startRect.getY() + 5; }

    private void controlSavedMenu() {

        Terminal terminal = game.getTerminal();

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

                terminal.moveCursor(calculateX() + file.length(), calculateY());
                terminal.applyBackgroundColor(Terminal.Color.CYAN);
                terminal.putCharacter(key.getCharacter());

                file += key.getCharacter();
            }

            if (key != null && key.getKind() == Key.Kind.Backspace) {

                if (file.isEmpty()) continue;

                if (file.length() == 1) {
                    terminal.applyBackgroundColor(Terminal.Color.WHITE);
                    writeOut("SAVE", 3);
                }

                file = file.substring(0, file.length() - 1);

                terminal.applyBackgroundColor(Terminal.Color.CYAN);
                terminal.moveCursor(calculateX() + file.length(), calculateY());
                terminal.putCharacter(' ');
                terminal.moveCursor(calculateX() + file.length(), calculateY());

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
    }

    public void drawLoadMenu() {

        Terminal terminal = game.getTerminal();

        clearMenu();
        drawFrame();

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
        loadLevelsCount = Integer.parseInt(properties.getProperty("Count"));

        loadLevelOptions = new String[loadLevelsCount + 1];

        for (int i = 1; i < loadLevelOptions.length; i++) {

            String level = properties.getProperty(Integer.toString(i));

            loadLevelOptions[i] = level;

        }

        setloadLevelChosenOption(1);
        setLoadLevelPageNumber(1);

        printLoadPage(1, elementNumber(), loadLevelOptions, pageCount());

        highlight(loadLevelOptions[1], 1);

    }

    //number of elements on one of the pages
    private int elementNumber() { return (height - 4) / 2; }

    //the number of pages needed
    private int pageCount() { return (loadLevelsCount * 2) / (height - 4) + 1; }

    private void setloadLevelChosenOption(int x) { loadLevelChosenOption = x; }

    public void setLoadLevelPageNumber(int loadLevelPageNumber) {
        this.loadLevelPageNumber = loadLevelPageNumber;
    }

    private void controlLoadMenu() {

        Terminal terminal = game.getTerminal();

        Key key = terminal.readInput();

        while (key == null || key.getKind() != Key.Kind.Escape) {

            try {
                Thread.sleep(50);
            } catch (Exception e) { e.printStackTrace(); }

            key = terminal.readInput();

            if (key != null && key.getKind() == Key.Kind.ArrowDown) {
                if (loadLevelOptions.length == 2) continue;
                loadLevelChosenOption++;
                if (loadLevelChosenOption >= loadLevelOptions.length) {
                    loadLevelChosenOption = 1;
                    setLoadLevelPageNumber(1);
                    printLoadPage(loadLevelPageNumber, elementNumber(), loadLevelOptions, pageCount());
                    highlight(loadLevelOptions[loadLevelChosenOption], 1);
                    continue;
                }
                if (loadLevelChosenOption > loadLevelPageNumber*elementNumber()) {
                    loadLevelPageNumber++;
                    printLoadPage(loadLevelPageNumber, elementNumber(), loadLevelOptions, pageCount());
                    highlight(loadLevelOptions[loadLevelChosenOption], 1);
                    continue;
                }
                rehighlightForLoad(loadLevelOptions, loadLevelChosenOption, 1, elementNumber());
            }

            if (key != null && key.getKind() == Key.Kind.ArrowUp) {
                if (loadLevelOptions.length == 2) continue;
                loadLevelChosenOption--;
                if (loadLevelChosenOption <= 0) {
                    loadLevelChosenOption = loadLevelOptions.length - 1;
                    loadLevelPageNumber = pageCount();
                    printLoadPage(loadLevelPageNumber, elementNumber(), loadLevelOptions, pageCount());
                    highlight(loadLevelOptions[loadLevelChosenOption], loadLevelOptions.length - (pageCount() - 1)*elementNumber() - 1);
                    continue;
                }
                if (loadLevelChosenOption <= (loadLevelPageNumber-1)*elementNumber()) {
                    loadLevelPageNumber--;
                    printLoadPage(loadLevelPageNumber, elementNumber(), loadLevelOptions, pageCount());
                    highlight(loadLevelOptions[loadLevelChosenOption], elementNumber());
                    continue;
                }
                rehighlightForLoad(loadLevelOptions, loadLevelChosenOption, -1, elementNumber());
            }

            if (key != null && key.getKind() == Key.Kind.Enter) {

                String chosenLevel = loadLevelOptions[loadLevelChosenOption];

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

    ///after clicking enter and choosing an option
    public void choose(int index) {

        switch (index) {
            case 2:
                drawDocumentation();
                controlDocumentation();
                return;
            case 3:
                drawSaveMenu();
                controlSavedMenu();
                return;
            case 4:
                drawLoadMenu();
                controlLoadMenu();
                return;
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

        game.setKillCheat(true);

        DynamicObstacle.coordinatesList.clear();

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

        game.getTerminal().clearScreen();
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

    //get's called when the responding thread get's started
    public void run() {

        Terminal terminal = game.getTerminal();

        game.setKillCheat(true);

        try {
            int waitTime = 50;
            if (game.getCheatThread() != null && game.getCheatThread().isAlive())
                waitTime = 260;
            Thread.sleep(waitTime);
        } catch (Exception e) { e.printStackTrace(); }

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
                isOptionChosen = true;
                choose(chosenOption);
            }

            isOptionChosen = false;

            if (key != null && key.getKind() == Key.Kind.Escape) break;

            if (kill) break;

        }

        //restarting options
        chosenOption = 1;

        game.getField().drawBorder();
        game.getField().redraw();
        game.getPlayer().redraw();
        Game.focusScreen(game.getTerminal().getTerminalSize(),game.getPlayer(),game.getOffset(),game.getField());
        game.getStats().redraw();

    }

}
