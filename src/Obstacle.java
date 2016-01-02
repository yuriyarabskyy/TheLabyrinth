import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by yuriyarabskyy on 25/12/15.
 */

//is an abstract class, because it doesn't implement the show method from the Showable interface
//static obstacle and dynamic obstacle both inherit from this class
public abstract class Obstacle implements Showable {

    protected Terminal terminal;

    protected Coordinates coordinates;

    public Obstacle (Terminal terminal, Coordinates coordinates) {
        this.terminal = terminal;
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void tryHit(Player player) {
        if (coordinates.equals(player.getCoordinates()))
            player.damage();
    }

}
