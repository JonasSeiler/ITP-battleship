package src;

public class coordinate {
    public int x, y;
    public coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // override for equals function
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        coordinate other = (coordinate) obj;
        return x == other.x && y == other.y;
    }
}
