package src.logic;
import java.util.Arrays;
/**
 * implements a singular ship
 *
 */
public class ship {
    int length;
    int head_x;
    int head_y;
    int dir;        // 0 = x+, 1 = y+
    int[] lifes;
    coordinate[] pos;

    public ship(int length) {
        this.length = length;
        this.lifes = new int[length];
        this.pos = new coordinate[length];
        Arrays.fill(lifes, 1);
        Arrays.fill(pos, new coordinate(-1, -1));
    }
    /**
     * sets the position for a single segment of a ship
     *
     * @param coord coordinate of the segment
     * @param index index of the segment
     */
    public void set_pos(coordinate coord, int index) {
        pos[index] = coord;
    }

    /**
     * sets the direction and the head of a ship
     *
     * @param head 
     * @param d 
     */
    public void set_dir_head(coordinate head, int d) {
        this.dir = d;
        this.head_x = head.x;
        this.head_y = head.y;
    }

    /**
     * return the position of the index'th segment
     *
     * @param index specifies which segment's position to return
     * @return 
     */
    public coordinate get_pos (int index) {
        return pos[index];
    }

    /**
     * prints the information of a ship
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
     * checks of a ship is destroyed
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
