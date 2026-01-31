package src.logic;

/**
 * implements a simple Coordinate class consisting of a x and a y integer
 *
 */
public class Coordinate {
    public int x, y;
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * should always be used instead of 
     * Coordinate == Coordinate 
     * return true if the x and y integers of the two Coordinate objects match
     *
     * @param obj 
     * @return 
     */
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Coordinate other = (Coordinate) obj;
        return x == other.x && y == other.y;
    }
}
