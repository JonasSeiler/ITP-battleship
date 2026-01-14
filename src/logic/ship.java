package src.logic;
import java.util.Arrays;
public class ship {
    int length;
    int head_x;
    int head_y;
    int dir;        // 0 = x+, 1 = y+ , 2 = x-, 3 = y-
    int[] lifes;
    coordinate[] pos;

    public ship(int length) {
        this.length = length;
        this.lifes = new int[length];
        this.pos = new coordinate[length];
        Arrays.fill(lifes, 1);
    }
    public void set_pos(coordinate coord, int index) {
        pos[index] = coord;
    }

    public void set_dir_head(coordinate head, int index) {
        this.dir = index;
        this.head_x = head.x;
        this.head_y = head.y;
    }

    public coordinate get_pos (int index) {
        return pos[index];
    }

    public void set_life (int index) {
        lifes[index] = 0;
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
            if(lifes[i] == 1) return false;
        }
        return true;
    }
}
