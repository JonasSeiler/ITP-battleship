package src.logic;

/**
 * implements a simple coordinate class consisting of a x and a y integer
 *
 */
public class coordinate {
    public int x, y;
    public coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * should always be used instead of 
     * coordinate == coordinate 
     * return true if the x and y integers of the two coordinate objects match
     *
     * @param obj 
     * @return 
     */
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        coordinate other = (coordinate) obj;
        return x == other.x && y == other.y;
    }
}
