
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class StaticObstacle extends Obstacle {

    Terminal terminal;

    public StaticObstacle(Terminal terminal) {

        this.terminal = terminal;

    }

    public void show() {

        terminal.applyForegroundColor(Terminal.Color.RED);

        terminal.applyBackgroundColor(Terminal.Color.RED);

        terminal.putCharacter(' ');

    }

}
