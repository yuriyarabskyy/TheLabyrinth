
/**
 * Created by yuriyarabskyy on 25/12/15.
 */
public abstract class Obstacle implements Showable {

    public void hit(Damagable player){
        player.damage();
    }

}
