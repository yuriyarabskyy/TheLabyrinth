
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class Entrance implements Showable{


    Terminal terminal;

    public Entrance(Terminal terminal) {
        this.terminal = terminal;
    }

    public void show() {

        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);

        terminal.putCharacter(' ');

    }

}
