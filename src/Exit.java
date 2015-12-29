
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Exit implements Showable {

    private Terminal terminal;

    public Exit(Terminal terminal) {
        this.terminal = terminal;
    }

    public void show() {

        terminal.applyForegroundColor(Terminal.Color.CYAN);

        terminal.applyBackgroundColor(Terminal.Color.BLUE);

        terminal.putCharacter('A');

    }

}
