package src.logic;
import java.util.*;
import java.io.*;
public class board {
    public int[][] ship_pos;
    public int[][] hit_pos;
    public ship[] fleet;
    private int size;
    private int opp_hp;
    private int[][] opp_hit;

    public board(int s, int[] ship_set) {
        size = s;
        ship_pos = new int[size][size];
        hit_pos = new int[size][size];
        opp_hit = new int[size][size];
        Arrays.fill(ship_pos, 0);
        Arrays.fill(hit_pos, 0);
        Arrays.fill(opp_hit, -1);
        fleet = new ship[ship_set.length];
        

        for (int i = 0; i < ship_set.length; i++) { // Schiffe initialisieren
            fleet[i] = new ship(ship_set[i]);
            opp_hp += ship_set[i];
        }

    }

    public void place_ship(coordinate head, int dir, int s_index) {
        // dir: 0 = x,     1 =  y
        fleet[s_index].set_dir_head(head, dir);
        if(dir % 2 == 0) {
            if(dir == 0) {
                for (int i = 0; i < fleet[s_index].length; i++) {
                    head.x += i;
                    ship_pos[head.x][head.y] = 1;
                    fleet[s_index].set_pos(head, i);
                }
            } else {
                for (int i = 0; i < fleet[s_index].length; i++) {
                    head.x -= i;
                    ship_pos[head.x][head.y] = 1;
                    fleet[s_index].set_pos(head, i);
                }
            }
        }
        else {
            if(dir == 1) {
                for (int i = 0; i < fleet[s_index].length; i++) {
                    head.y += i;
                    ship_pos[head.x][head.y] = 1;
                    fleet[s_index].set_pos(head, i);
                }
            } else {
                 for (int i = 0; i < fleet[s_index].length; i++) {
                    head.y -= i;
                    ship_pos[head.x][head.y] = 1;
                    fleet[s_index].set_pos(head, i);
                }              
            }
        }
    }
    public void register_shot(coordinate shot, int response) {
        opp_hit[shot.x][shot.y] = response;
        if(response == 2) {
            if(opp_hit[shot.x+1][shot.y] == 1) {
                coordinate surrounding = new coordinate(shot.x+1, shot.y);
                register_shot(surrounding, response);
            }
            if(opp_hit[shot.x-1][shot.y] == 1) {
                coordinate surrounding = new coordinate(shot.x-1, shot.y);
                register_shot(surrounding, response);

            }
            if(opp_hit[shot.x][shot.y+1] == 1) {
                coordinate surrounding = new coordinate(shot.x, shot.y+1);
                register_shot(surrounding, response);

            }
            if(opp_hit[shot.x][shot.y-1] == 1) {
                coordinate surrounding = new coordinate(shot.x, shot.y-1);
                register_shot(surrounding, response);
            }

        }
    }

    public int check_hit(coordinate att) {
        // registers hit
        hit_pos[att.x][att.y] = 1;
        if(ship_pos[att.x][att.y] == 0) { 
            return 0;
        }
        for (ship ship : fleet) {
            for(int i = 0; i < ship.length; i++) {
                if(att.equals(ship.pos[i])) {
                    ship.lifes[i] = 0;
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

    public boolean won() {
        if(opp_hp == 0) return false;
        else return true;
    }
    public boolean game_over() {
        if(won() || lost()) {
            return true;
        } else {
            return false;
        }
    }

    public void save_game(String file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // write size into file
            writer.write(String.valueOf(size));
            writer.newLine();
            // write ship_set array into file 
            for(ship ship : fleet) {
                writer.write(ship.length);
                writer.write(" ");
            }
            writer.newLine();
            // write ship_pos into file
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(ship_pos[i][j]);
                }
                writer.write(row.toString());
                writer.newLine();
            }
            // write hit_pos into file
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(hit_pos[i][j]);
                }
                writer.write(row.toString());
                writer.newLine();
            }
            // write entire fleet into file
            // order: x-pos, y-pos, lifes
            for (ship ship : fleet) {
                for (int i = 0; i < ship_pos.length; i++) {
                    writer.write(ship.pos[i].x);
                }
                writer.newLine();
                for (int i = 0; i < ship_pos.length; i++) {
                    writer.write(ship.pos[i].y);
                }
                writer.newLine();
                for (int i = 0; i < ship_pos.length; i++) {
                    writer.write((ship.lifes[i]));
                }
                writer.newLine();
            }
            // save opp_hit here
        } catch (IOException e) {
            System.err.println("Failed saving: " + e.getMessage());
        }
    }
}
