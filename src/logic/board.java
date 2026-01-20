package src.logic;
import java.util.*;
import java.io.*;
/**
 * implements a game board for battleships
 * this includes, a field that stores the position of the users ships, 
 * a field that signifies where the opponent has already shot  and 
 * a field that signifies where the user has already shot at
 *
 */
public class board {
    /**
     * field for the position of the user's ships 
     * (0 = water | 1 = ship)
     */
    public int[][] ship_pos;
    /**
     * field for the position of the shots the opponent has fired at the user 
     * (0 = not yet shot at | 1 = shot at)
     */
    public int[][] hit_pos;
    /**
     * array of the user's ships
     */
    public ship[] fleet;
    /**
     * size of the board
     */
    private int size;
    /**
     * hit points of the opponent
     */
    private int opp_hp;
    /**
     * field for the positions the user has already shot at 
     * (-1 = no information | 0 = water | 1 = ship | 2 = ship and sunken)
     */
    public int[][] opp_hit;

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

    /**
     * places a ship 
     * 
     * @param head the field a ship is in with the lowest (x&y position) for the specific ship
     * @param dir direction the ship is facing, 0 = horizontal | 1 = vertical
     * @param s_index 
     */
    public void place_ship(coordinate head, int dir, int s_index) {
        // dir: 0 = x,     1 =  y
        fleet[s_index].set_dir_head(head, dir);
        if(dir == 0) {
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

    /**
     * registers a shot the user shot at the opponent by saving it in opp_hit
     * and lowers the opponents hp by 1 if a ship was hit
     * @param shot coordinte where the user has shot at
     * @param response the answer the opponent gave for the shot at that position
     */
    public void register_shot(coordinate shot, int response) {
        opp_hit[shot.x][shot.y] = response;
        opp_hp = (response > 0) ? opp_hp-- : opp_hp;
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

    /**
     * checks the own Board what an opponent's shot hit
     *
     * @param att position the opponent is attacking
     * @return what the opponent hit (0 = water, 1 = ship, 2 = ship, also the entire ship was sunk)
     */
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

    /**
     * checks if the user lost the game because all of his ships were destroyed
     *
     * @return 
     */
    public boolean lost() {
        for(ship s : fleet) {
            if(!s.destroyed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks if user won because he sunk all his opponents ships
     */
    public boolean won() {
        if(opp_hp == 0) return false;
        else return true;
    }
    /**
     * checks if someone won
     */
    public boolean game_over() {
        if(won() || lost()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * saves the game by writing the data of the board object into a file that
     * can later be used to restore the game state
     *
     * @param file 
     */
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
             // write opp_hit into file
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(opp_hit[i][j]);
                }
                writer.write(row.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed saving: " + e.getMessage());
        }
    }
}
