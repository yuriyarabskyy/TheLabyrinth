import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by yuriyarabskyy on 14/01/16.
 */
public class Finder {

    private static final Coordinates[] directions = {new Coordinates(1,0),new Coordinates(0,1),new Coordinates(-1,0),new Coordinates(0,-1)};
    private static final Character[] directionsCh = {'r','d','l','u'};


    //uses my own search algorithm to find the next move
    public static String searchPath(Coordinates coordinates, Coordinates playerCoordinates, Showable[][] map, int area) {

        Queue<Pair> queue = new LinkedList<>();

        Set<Coordinates> visitedCoords = new HashSet<>();

        if (area < Integer.MAX_VALUE) area *= 200;

        for (int i = 0; i < 4; i++) {

            Coordinates vect = coordinates.clone().add(directions[i]);

            if (vect.getX() >= 0 && vect.getX() < map.length && vect.getY() < map[0].length
                    && vect.getY() >= 0 && (map[vect.getX()][vect.getY()] == null || map[vect.getX()][vect.getY()] instanceof Arrow
                    || vect.equals(playerCoordinates))) {

                Pair pair = new Pair(vect, Character.toString(directionsCh[i]));
                queue.add(pair);
                visitedCoords.add(vect);

            }

        }

        if (queue.isEmpty()) return "";

        //if the player could not be found choose a random path
        int randomIndex = area - (int) (Math.random() * 1000) - 500;
        String randomRoute = "";

        for (int i = 0; i < area; i++) {

            //1 at the end marks that the route is random
            if (queue.isEmpty()) return randomRoute + '1';

            Pair next = queue.remove();

            for (int j = 0; j < 4; j++) {

                Coordinates vect = next.getCoordinates().clone().add(directions[j]);

                if (vect.getX() >= 0 && vect.getX() < map.length && !visitedCoords.contains(vect) &&
                        vect.getY() >= 0 && vect.getY() < map[0].length
                        && (map[vect.getX()][vect.getY()] == null || map[vect.getX()][vect.getY()] instanceof Arrow
                        || vect.equals(playerCoordinates)))
                {

                    Pair pair = new Pair(vect, next.getPath() + directionsCh[j]);
                    if (pair.getCoordinates().equals(playerCoordinates)) return pair.getPath();
                    queue.add(pair);
                    visitedCoords.add(vect);

                    if (i >= randomIndex && randomRoute.isEmpty()) randomRoute = next.getPath();

                }

            }


        }

        return randomRoute + '1';

    }

    //for the player in cheat mode
    public static String searchKey(Coordinates coordinates, Showable map[][], int keysLeft) {

        boolean findAwayOut = false;

        if (keysLeft == 0) findAwayOut = true;

        int area = Integer.MAX_VALUE;

        Queue<Pair> queue = new LinkedList<>();

        Set<Coordinates> visitedCoords = new HashSet<>();

        for (int i = 0; i < 4; i++) {

            Coordinates vect = coordinates.clone().add(directions[i]);

            if (vect.getX() > 0 && vect.getX() < map.length && vect.getY() < map[0].length
                    && vect.getY() > 0 && map[vect.getX()][vect.getY()] == null) {

                Pair pair = new Pair(vect, Character.toString(directionsCh[i]));
                queue.add(pair);
                visitedCoords.add(vect);

            }

        }

        if (queue.isEmpty()) return "";


        for (int i = 0; i < area; i++) {

            if (queue.isEmpty()) return "";

            Pair next = queue.remove();

            if ((!findAwayOut && map[next.getCoordinates().getX()][next.getCoordinates().getY()] instanceof Key)
                    ||(findAwayOut && map[next.getCoordinates().getX()][next.getCoordinates().getY()] instanceof Exit))
                return next.getPath();

            for (int j = 0; j < 4; j++) {

                Coordinates vect = next.getCoordinates().clone().add(directions[j]);

                if (vect.getX() > 0 && vect.getX() < map.length && !visitedCoords.contains(vect) &&
                        vect.getY() > 0 && vect.getY() < map[0].length && !(map[vect.getX()][vect.getY()] instanceof Wall)
                        && !(map[vect.getX()][vect.getY()] instanceof StaticObstacle)){

                    Pair pair = new Pair(vect, next.getPath() + directionsCh[j]);
                    queue.add(pair);
                    visitedCoords.add(vect);

                }

            }


        }

        return "";
    }

}
