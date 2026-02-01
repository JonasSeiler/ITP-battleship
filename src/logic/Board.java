package src.logic;
import java.util.*;
import java.io.*;
/**
 * implements a game Board for battleships
 * this includes, a field that stores the position of the users ships, 
 * a field that signifies where the opponent has already shot  and 
 * a field that signifies where the user has already shot at
 *
 * @author Jonas Seiler
 */
public class Board {
    /**
     * field for the position of the user's ships 
     * (0 = water | 1 = Ship)
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
    public Ship[] fleet;
    /**
     * size of the Board
     */
    private int size;
    /**
     * hit points of the opponent
     */
    public int opp_hp;
    /**
     * field for the positions the user has already shot at 
     * (-1 = no information | 0 = water | 1 = Ship | 2 = Ship and sunken)
     */
    public int[][] opp_hit;

    /**
     * Initializes the variables the board needs
     *
     * @param s         board size 
     * @param ship_set  ship length array
     */
    public Board(int s, int[] ship_set) {
        size = s;
        ship_pos = new int[size][size];
        hit_pos = new int[size][size];
        opp_hit = new int[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(ship_pos[i], 0);
            Arrays.fill(hit_pos[i], 0);
            Arrays.fill(opp_hit[i], -1);
        }
        fleet = new Ship[ship_set.length];
        

        for (int i = 0; i < ship_set.length; i++) { // Schiffe initialisieren
            fleet[i] = new Ship(ship_set[i]);
            opp_hp += ship_set[i];
        }

    }

 /**
  * Specifically for the Bot to check if a placement of a ship is valid (true)
  * or would interfere with another ship (false)
  *
  * @param head     most top left tile of a ship
  * @param dir      direction of the ship 
  * @param s_index  index in the ship set array 
  * @return 
  */
 public boolean isPlacementvalid(Coordinate head, int dir, int s_index) {

    int length = fleet[s_index].length;
    for (int i = 0; i < length; i++) {
        int x = (dir == 0) ? head.x + i : head.x;
        int y = (dir == 0) ? head.y : head.y + i;
            if (!inBounds(x, y)) {
                return false;
            }

        // Check 3x3 area around this Ship cell
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = x + dx;
                int checkY = y + dy;
                // Check if Coordinate is within Board bounds
                if (inBounds(checkX, checkY)) {
                    
                    // Check if there's already a Ship at this position
                    if (ship_pos[checkX][checkY] == 1) {
                        return false;
                    }
                    }             
                }
            }
        }
    
    return true;
}

    
    /**
     * places a Ship 
     * 
     * @param head     most top left tile of a ship
     * @param dir      direction of the ship 
     * @param s_index  index in the ship set array 
     */
    public void place_ship(Coordinate head, int dir, int s_index) {
        // dir: 0 = x,     1 =  y
        fleet[s_index].set_dir_head(head, dir);
        if(dir == 0) {
            for (int i = 0; i < fleet[s_index].length; i++) {
                System.out.println(head.x + " " + head.y);
                ship_pos[head.x][head.y] = 1;
                fleet[s_index].set_pos(new Coordinate(head.x, head.y), i);
                head.x += 1;
            }
        }
        else {
            for (int i = 0; i < fleet[s_index].length; i++) {
                System.out.println(head.x + " " + head.y);
                ship_pos[head.x][head.y] = 1;
                fleet[s_index].set_pos(new Coordinate(head.x, head.y), i);
                head.y += 1;
            }
        }
    }

    /**
     * registers a shot the user shot at the opponent by saving it in opp_hit
     * and lowers the opponents hp by 1 if a Ship was hit
     *
     * @param shot      coordinte where the user has shot at
     * @param response  the answer the opponent gave for the shot at that position
     */
    public void register_shot(Coordinate shot, int response) {
        opp_hit[shot.x][shot.y] = response;
        if(response == 2) {
            if((inBounds(shot.x+1, shot.y)) && opp_hit[shot.x+1][shot.y] == 1) {
                Coordinate surrounding = new Coordinate(shot.x+1, shot.y);
                register_shot(surrounding, response);
            }
            if((inBounds(shot.x-1, shot.y)) && opp_hit[shot.x-1][shot.y] == 1) {
                Coordinate surrounding = new Coordinate(shot.x-1, shot.y);
                register_shot(surrounding, response);
            }
            if((inBounds(shot.x, shot.y+1)) && opp_hit[shot.x][shot.y+1] == 1) {
                Coordinate surrounding = new Coordinate(shot.x, shot.y+1);
                register_shot(surrounding, response);
            }
            if((inBounds(shot.x, shot.y-1)) && opp_hit[shot.x][shot.y-1] == 1) {
                Coordinate surrounding = new Coordinate(shot.x, shot.y-1);
                register_shot(surrounding, response);
            }
        }
    }
    public void dec_hp(int i) {
        if(i > 0)
        opp_hp -= 1;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 &&
               x < size &&
               y < size;
    }

    /**
     * checks the own Board what an opponent's shot hit
     *
     * @param att   position the opponent is attacking
     * @return      what the opponent hit (0 = water, 1 = Ship, 2 = Ship, also the entire Ship was sunk)
     */
    public int check_hit(Coordinate att) {
        // registers hit
        hit_pos[att.x][att.y] = 1;
        if(ship_pos[att.x][att.y] == 0) { 
            return 0;
        }
        int dmg = 1;
        for (Ship Ship : fleet) {
            for(int i = 0; i < Ship.length; i++) {
                if(Ship.pos[i].y == att.y &&Ship.pos[i].x == att.x ) {
                    Ship.lifes[i] = 0;
                    if(Ship.destroyed()) {
                        dmg = 2;
                    }
                    return dmg;
                }
            }
        }
        return dmg;
    }

    /**
     * checks if the user lost the game because all of his ships were destroyed
     *
     * @return true if the player lost else false
     */
    public boolean lost() {
        for(Ship s : fleet) {
            if(!s.destroyed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks if user won because he sunk all his opponents ships
     *
     * @return true if the player won else false
     */
    public boolean won() {
        if(opp_hp == 0) { 
            return true;
        }
        return false;
    }
    /**
     * checks if the game is over
     *
     * @return true if the game is over else false
     */
    public boolean game_over() {
        if(won() || lost()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * saves the game by writing the data of the Board object into a file that
     * can later be used to restore the game state
     *
     * @param file  file name
     */
    public void save_game(String file) {
        String userHome = System.getProperty("user.home");
        String saveDir;
        String os = System.getProperty("os.name").toLowerCase();

        // Platform-specific save locations
        if (os.contains("win")) {
            // Windows: Documents\Battleship\
            saveDir = userHome + "\\Documents\\Battleship\\";
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/Battleship/
            saveDir = userHome + "/Library/Application Support/Battleship/";
        } else {
            // Linux/Unix: ~/.local/share/battleship/
            saveDir = userHome + "/.local/share/battleship/";
        }

        // Create directory if it doesn't exist
        File directory = new File(saveDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String savePath = saveDir + file;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath))) {
            // write size into file
            writer.write(String.valueOf(size));
            writer.newLine();
            // write ship_set array into file 
            for(Ship Ship : fleet) {
                writer.write(Ship.length + " ");
            }
            writer.newLine();
            // write ship_pos into file
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(ship_pos[i][j] + " ");
                }
                writer.write(row.toString());
                writer.newLine();
            }
            // write hit_pos into file
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(hit_pos[i][j] + " ");
                }
                writer.write(row.toString());
                writer.newLine();
            }
            // write entire fleet into file
            // order: x-pos, y-pos, lifes
            for (Ship Ship : fleet) {
                for (int i = 0; i < Ship.pos.length; i++) {
                    writer.write(Ship.pos[i].x + " ");
                }
                writer.newLine();
                for (int i = 0; i < Ship.pos.length; i++) {
                    writer.write(Ship.pos[i].y + " ");
                }
                writer.newLine();
                for (int i = 0; i < Ship.pos.length; i++) {
                    writer.write(Ship.lifes[i] + " ");
                }
                writer.newLine();
                writer.write(String.valueOf(Ship.dir));
                writer.newLine();
            }
             // write opp_hit into file
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(opp_hit[i][j] + " ");
                }
                writer.write(row.toString());
                writer.newLine();
            }
            writer.write(opp_hp + " ");

        } catch (IOException e) {
            System.err.println("Failed saving: " + e.getMessage());
        }
    }
}    

