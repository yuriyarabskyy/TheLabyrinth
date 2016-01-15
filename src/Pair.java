/**
 * Created by yuriyarabskyy on 14/01/16.
 */
public class Pair {

    private Coordinates coordinates;

    private String path;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Pair(Coordinates coordinates, String path) {
        this.coordinates = coordinates;
        this.path = path;
    }
}