
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class StaticObstacle extends Obstacle {

    public StaticObstacle(Terminal terminal) {
        super(terminal, new Coordinates(0,0));
    }

    public void show() {

        terminal.applyForegroundColor(Terminal.Color.RED);

        terminal.applyBackgroundColor(Terminal.Color.RED);

        terminal.putCharacter(' ');

    }

}
