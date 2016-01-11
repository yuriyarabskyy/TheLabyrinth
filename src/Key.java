
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Key implements Showable{

    private Terminal terminal;

    public Key(Terminal terminal) {
        this.terminal = terminal;
    }

    public void show() {

        terminal.applyForegroundColor(Terminal.Color.WHITE);

        terminal.applyBackgroundColor(Terminal.Color.MAGENTA);

        terminal.putCharacter('K');

    }

}
