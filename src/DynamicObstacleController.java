
import com.googlecode.lanterna.terminal.Terminal;

import java.util.List;

/**
 * Created by yuriyarabskyy on 26/12/15.
 */
public class DynamicObstacleController implements Runnable {

    private Game game;

    public DynamicObstacleController(Game game) {
        this.game   = game;
    }

    public void run() {

        while (!game.isCloseGame()) {

            try {
                Thread.sleep(450);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!game.getPause()) {

                for (DynamicObstacle obj : game.getDynobList()) {

                    if (game.getPause()) break;

                    obj.move(game.getField(), game.getOffset(), game.getPlayer());

                }

            }

        }

    }

}
