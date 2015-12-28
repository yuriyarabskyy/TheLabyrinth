import com.googlecode.lanterna.terminal.Terminal;

import java.util.List;

/**
 * Created by yuriyarabskyy on 26/12/15.
 */
public class DynamicObstacleController implements Runnable {

    private Game   game;
    private Pauser pauser;

    public DynamicObstacleController(Game game, Pauser pauser) {
        this.game   = game;
        this.pauser = pauser;
    }

    public void run() {

        //TODO Change to get methods
        while (Integer.parseInt(game.getPlayer().getHealth()) > 0) {

            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!pauser.stop) {

                for (DynamicObstacle obj : game.getDynobList()) obj.move(game.getField(), game.getOffset(), game.getPlayer(), game.getStats());

            }

        }

    }

}
