package src.logic;
import java.util.Arrays;
/**
 * implements a singular Ship
 *
 * @author Jonas Seiler
 */
public class Ship {
    /**
     * length of the ship
     */
    public int length;
    /**
     * x Coordinate of the ships head
     */
    public int head_x;
    /**
     * y Coordinate of the ships head
     */
    public int head_y;
    /**
     * decides if the ship is oriented vertically (1) or horizontally (0)
     */
    public int dir;        // 0 = x+, 1 = y+
    /**
     * stores the lifes of the ship per tile 
     */
    public int[] lifes;
    /**
     * stores the Coordinates the ship occupies
     */
    public Coordinate[] pos;

    /**
     * properly initializes all the variables without actually placing the ship
     *
     * @param length    length of the ship
     */
    public Ship(int length) {
        this.length = length;
        this.lifes = new int[length];
        this.pos = new Coordinate[length];
        Arrays.fill(lifes, 1);
        for (int i = 0; i < length; i++) {
            pos[i] = new Coordinate(-1, -1);
        }
    }
    /**
     * sets the position for a single segment of a Ship
     *
     * @param coord Coordinate of the segment
     * @param index index of the segment
     */
    public void set_pos(Coordinate coord, int index) {
        pos[index] = coord;
    }

    /**
     * sets the direction and the head of a Ship
     *
     * @param head  Coordinate for the head 
     * @param d     direction 
     */
    public void set_dir_head(Coordinate head, int d) {
        this.dir = d;
        this.head_x = head.x;
        this.head_y = head.y;
    }

    /**
     * return the position of the index'th segment
     *
     * @param index specifies which segment's position to return
     * @return the coordinate of the index
     */
    public Coordinate get_pos(int index) {
        return pos[index];
    }

    /**
     * prints the information of a Ship
     */
    public void print_ship() {
        System.out.println("Length: " + length);
        for(int i = 0; i < pos.length; i++) {
            System.out.print("x = " + pos[i].x + "\t");
            System.out.print("y = " + pos[i].y + "\t");
            System.out.println("alive? = " + lifes[i]);
        }
    }

    /**
     * checks of a Ship is destroyed
     *
     * @return true if destroyed, else false 
     */
    public boolean destroyed () {
        for(int i=0; i < length; i++) {
            if(lifes[i] == 1) return false;
        }
        return true;
    }
}
