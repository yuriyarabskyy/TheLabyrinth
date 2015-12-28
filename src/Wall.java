import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Wall implements Showable {

    Terminal terminal;

    public Wall(Terminal terminal) {
        this.terminal = terminal;
    }

    public void show() {

        terminal.applyForegroundColor(Terminal.Color.WHITE);

        terminal.applyBackgroundColor(Terminal.Color.BLACK);

        terminal.putCharacter(' ');

        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);

    }

}
