import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 14/01/16.
 */
public class Arrow implements Showable{

    public static char[] symbols = {'\u2190', '\u2192', '\u2193', '\u2191'};

    private Terminal terminal;

    private char pointer;

    Arrow(Terminal terminal, int i) { this.terminal = terminal; pointer = symbols[i]; }

    public void show() {

        terminal.applyBackgroundColor(Terminal.Color.BLUE);
        terminal.applyForegroundColor(Terminal.Color.WHITE);
        terminal.putCharacter(pointer);

    }

}
