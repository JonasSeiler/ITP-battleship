package src;
import java.util.Arrays;
// rm coordinate class and incorporate it into ship
public class ship {
    int length;
    int head_x;
    int head_y;
    int dir;        // 0 = x+, 1 = y+ , 2 = x-, 3 = y-
    boolean[] lifes;
    coordinate[] pos;

    public ship(int length) {
        this.length = length;
        this.lifes = new boolean[length];
        this.pos = new coordinate[length];
        Arrays.fill(lifes, true);
    }
    public void set_pos(coordinate coord, int index) {
        pos[index] = coord;
    }

    public void print_ship() {
        System.out.println("Length: " + length);
        for(int i = 0; i < pos.length; i++) {
            System.out.print("x = " + pos[i].x + "\t");
            System.out.print("y = " + pos[i].y + "\t");
            System.out.println("alive? = " + lifes[i]);
        }
    }

    public boolean destroyed () {
        for(int i=0; i < length; i++) {
            if(lifes[i]) return false;
        }
        return true;
    }
}
