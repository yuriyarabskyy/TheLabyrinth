import java.util.TimerTask;

/**
 * Created by yuriyarabskyy on 11/01/16.
 */
public class DamagableSetter extends TimerTask {

    Player player;

    DamagableSetter(Player player) {
        this.player = player;
    }

    public void run() {

        player.setGotHit(false);

        player.redraw();

    }

}
