/**
 * Created by yuriyarabskyy on 25/12/15.
 */

public class Coordinates {

    public int x, y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //try construct a coordinate from an object assuming it's
    //a String representing a x and y separated by a comma
    public Coordinates(Object object) throws java.lang.NumberFormatException{

        String[] coords = ((String)object).split(",");

        try {

            x = Integer.parseInt(coords[0]);

            y = Integer.parseInt(coords[1]);

        }
        catch (NumberFormatException e) { e.printStackTrace(); }

    }

    public Coordinates add(Coordinates coord) {
        x += coord.x;
        y += coord.y;
        return this;
    }

    public Coordinates add(int i) {
        x += i;
        y += i;
        return this;
    }

    public Coordinates clone() {
        return new Coordinates(x, y);
    }

    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    public Coordinates subtract(Coordinates coord) {
        x -= coord.x;
        y -= coord.y;
        return this;
    }

    @Override
    public boolean equals(Object coord) {

        if (!(coord instanceof Coordinates)) return false;

        Coordinates coordinates = (Coordinates) coord;

        if (x == coordinates.x && y == coordinates.y) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public double distTo(Coordinates coord) {
        return Math.sqrt((coord.getX() - x)*(coord.getX() - x)+(coord.getY() - y)*(coord.getY() - y));
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
