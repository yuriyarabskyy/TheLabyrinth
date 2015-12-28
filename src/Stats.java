import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 26/12/15.
 */
public class Stats {

    private Game game;

    public Stats(Game game) {
        this.game = game;
    }

    public void redraw() {

        Terminal terminal = game.getTerminal();

        synchronized (terminal) {

            int x = (int)(terminal.getTerminalSize().getColumns()*0.5);
            int y = terminal.getTerminalSize().getRows() - 2;
            terminal.applyForegroundColor(Terminal.Color.RED);
            terminal.applyBackgroundColor(Terminal.Color.BLACK);
            //drawing the players health
            String lives = "HEALTH: " + game.getPlayer().getHealth();
            x -= lives.length();
            terminal.moveCursor(x, y);

            for (int i = 0; i < lives.length(); i++)
                terminal.putCharacter(lives.charAt(i));

            //drawing the number of left keys
            x = (int) (terminal.getTerminalSize().getColumns() * 0.5) + 2;
            terminal.moveCursor(x + 5, y);
            for (int i = 0; i < 4; i++) terminal.putCharacter(' ');
            terminal.moveCursor(x, y);
            String keys = "KEYS: " + game.getPlayer().getKeysLeft();

            for (int i = 0; i < keys.length(); i++)
                terminal.putCharacter(keys.charAt(i));
        }

    }

}
