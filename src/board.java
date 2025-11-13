package src;

public class board {
    private int[][] ship_pos;
    private Boolean[][] hit_pos;
    private ship[] fleet;
    
    5 4 4 3 3 2 2 
    c 
    d 
    public board(int size, int[] ship_set) {
        ship_pos = new int[size][size];
        hit_pos = new Boolean[size][size];
        fleet = new ship[ship_set.length];
        // schiffe initialisieren 

    }
// add x- and y-
    
    public void place_ship(coordinate head, int dir, int s_index) {
        if(dir % 2 == 0) {
            for (int i = 0; i < fleet[s_index].length; i++) {    
                head.x += i;
                ship_pos[head.x][head.y] = 1;
                fleet[s_index].set_pos(head, i);
            }
        }
        else {
            for (int i = 0; i < fleet[s_index].length; i++) {
                head.y += i;
                ship_pos[head.x][head.y] = 1;
                fleet[s_index].set_pos(head, i);
            }
        }
    }

    public int check_hit(coordinate att) {
        hit_pos[att.x][att.y] = true;
        if(ship_pos[att.x][att.y] == 0) { 
            return 0;
    }
        for (ship ship : fleet) {
            for(int i = 0; i < ship.length; i++) {
                if(att.equals(ship.pos[i])) {
                    ship.lifes[i] = false;
                    if(ship.destroyed()) return 2;
                    return 1;
                }
            }
        }
        return -1;      // would mean a major error in the op. should never be reached
    }

    public boolean lost() {
        for(ship s : fleet) {
            if(!s.destroyed()) {
                return false;
            }
        }
        return true;
    }
}
