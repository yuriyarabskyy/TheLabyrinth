
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public class DynamicObstacle extends Obstacle{

    private Terminal terminal;

    private Coordinates coordinates;

    Coordinates[] directions = {new Coordinates(1,0),new Coordinates(0,1),new Coordinates(-1,0),new Coordinates(0,-1)};

    public DynamicObstacle(Terminal terminal, Coordinates coordinates) {

        this.terminal = terminal;

        this.coordinates = coordinates;

    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void show() {

        terminal.moveCursor(coordinates.getX(), coordinates.getY());

        terminal.applyForegroundColor(Terminal.Color.MAGENTA);

        terminal.applyBackgroundColor(Terminal.Color.BLUE);

        terminal.putCharacter('D');

    }

    public void move(Field field, Coordinates offset, Player player, Stats stats) {

        //TODO

        synchronized (terminal) {

            Coordinates coord;

            int x = coordinates.getX() - offset.getX() + 1;
            int y = coordinates.getY() - offset.getY() + 1;

            //if the object is over the screen
            if (x > terminal.getTerminalSize().getColumns() - 2 ||
                    y > terminal.getTerminalSize().getRows() - 5 || x <= 0 || y <= 0) return;

            terminal.applyForegroundColor(Terminal.Color.GREEN);

            terminal.applyBackgroundColor(Terminal.Color.BLUE);

            terminal.moveCursor(x, y);

            terminal.putCharacter(' ');

            do {

                int rand = (int) (Math.random() * 4);

                coord = coordinates.clone().add(directions[rand]);

            } while (field.getMap()[coord.getX()][coord.getY()] != null);

            coordinates = coord;

            x = coordinates.getX() - offset.getX() + 1;
            y = coordinates.getY() - offset.getY() + 1;

            //if the object is over the screen
            if (x > terminal.getTerminalSize().getColumns() - 2 ||
                    y > terminal.getTerminalSize().getRows() - 5 || x <= 0 || y <= 0) return;

            terminal.applyBackgroundColor(Terminal.Color.YELLOW);

            terminal.moveCursor(x, y);

            terminal.putCharacter(' ');

            if (coordinates.equals(player.getCoordinates())) {
                hit(player);
                player.redraw();
                stats.redraw();
            }

        }
    }

}
