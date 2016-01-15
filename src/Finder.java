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


    //uses breadth-first search in its core to find the next move
    public static String searchPath(Coordinates coordinates, Coordinates playerCoordinates, Showable[][] map, int area) {

        Queue<Pair> queue = new LinkedList<>();

        Set<Coordinates> visitedCoords = new HashSet<>();

        for (int i = 0; i < 4; i++) {

            Coordinates vect = coordinates.clone().add(directions[i]);

            if (vect.getX() > 0 && vect.getX() < map.length && vect.getY() < map[0].length
                    && vect.getY() > 0 && (map[vect.getX()][vect.getY()] == null || map[vect.getX()][vect.getY()] instanceof Arrow)) {

                Pair pair = new Pair(vect, Character.toString(directionsCh[i]));
                queue.add(pair);
                visitedCoords.add(vect);

            }

        }

        if (queue.isEmpty()) return "";

        //if the player could not be found choose a random path
        int randomIndex = area*200 - (int) (Math.random() * 1000) - 500;
        String randomRoute = "";

        for (int i = 0; i < area*200; i++) {

            if (queue.isEmpty()) return randomRoute;

            Pair next = queue.remove();

            if (next.getCoordinates().equals(playerCoordinates)) return next.getPath();

            for (int j = 0; j < 4; j++) {

                Coordinates vect = next.getCoordinates().clone().add(directions[j]);

                if (vect.getX() > 0 && vect.getX() < map.length && !visitedCoords.contains(vect) &&
                        vect.getY() > 0 && vect.getY() < map[0].length
                        && (map[vect.getX()][vect.getY()] == null || map[vect.getX()][vect.getY()] instanceof Arrow)) {

                    Pair pair = new Pair(vect, next.getPath() + directionsCh[j]);
                    queue.add(pair);
                    visitedCoords.add(vect);

                    if (i >= randomIndex && randomRoute.isEmpty()) randomRoute = next.getPath();

                }

            }


        }

        return randomRoute;

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
