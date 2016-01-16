
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
                Thread.sleep(350);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!game.getPause()) {

                //activate the first obstacle
                if (game.isResetDynamicObstacles() && game.getDynobList().size() > 0) {

                    DynamicObstacle minObj = game.getDynobList().get(0);
                    double minDist = minObj.getCoordinates().distTo(game.getPlayer().getCoordinates());

                    for (DynamicObstacle obstacle : game.getDynobList()) {

                        double dist = obstacle.getCoordinates().distTo(game.getPlayer().getCoordinates());

                        if (dist < minDist) {
                            minDist = dist;
                            minObj = obstacle;
                        }

                    }

                    minObj.move(game.getField(), game.getOffset(), game.getPlayer(), true);

                    game.setResetDynamicObstacles(false);

                }

                for (DynamicObstacle obj : game.getDynobList()) {

                    if (game.getPause()) break;

                    obj.move(game.getField(), game.getOffset(), game.getPlayer(), false);

                }

            }

        }

    }

}
