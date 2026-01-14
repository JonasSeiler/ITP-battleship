package src.logic;
import java.io.*;

import src.gui.battlescreen;

public class game {
    private board board1;
    // add gui and coms classes
    private battlescreen gui;
    public game(int size, int[] ship_set) {
        board1 = new board(size, ship_set);
    }

    public void load_game(String filepath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
           
            // read grid size
            int new_s = Integer.parseInt(reader.readLine().trim());
            
            // read ship array
            String line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            int[] s_set = new int[parts.length];
            for (int i = 0; i < s_set.length; i++) {
                s_set[i] = Integer.parseInt(parts[i]);
            }

            // init new board
            board new_b = new board(new_s, s_set);
            
            // read and copy ship_pos 
            for (int i = 0; i < s_set.length; i++) {
                String row = reader.readLine();
                String[] segments = row.trim().split("\\s+");
                for (int j = 0; j < s_set.length; j++) {
                    new_b.ship_pos[i][j] = Integer.parseInt(segments[j]);
                }
            }
            // read and copy hit_pos
            for (int i = 0; i < s_set.length; i++) {
                String row = reader.readLine();
                String[] segments = row.trim().split("\\s+");
                for (int j = 0; j < s_set.length; j++) {
                    new_b.hit_pos[i][j] = Integer.parseInt(segments[j]);
                }
            }

            // read and copy fleet
            for (ship s : new_b.fleet) {
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int i = 0; i < s.length; i++) {
                    s.pos[i].x = Integer.parseInt(parts[i]);
                }
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int i = 0; i < s.length; i++) {
                    s.pos[i].y = Integer.parseInt(parts[i]);
                }
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int i = 0; i < s.length; i++) {
                    s.lifes[i] = Integer.parseInt(parts[i]);
                }

            } 
            board1 = new_b;

        } catch (Exception e) {
            System.err.println("Failed saving: " + e.getMessage());

        }
    }
    
    public int send_shot(int x, int y) {
        //server msg here 
        int response = 1; // response = 0,1,2
        
        return response;
    }

    public int get_hit(coordinate p) {
        int answer = board1.check_hit(p); 
        switch(answer) {
            case 0:
                
                break;
            case 1:

                break;
            case 2:
                
                break;
            case -1:

                break;
        }
        return answer; 
        
    }


/*    public static void main(String[] args) {
        // add pre-game init || load game 
        while (!board1.lost() || !board2.lost()) {
            if (board_1 == true) {
                board_1 = false;
                board_2 = true;
                switch (board2.check_hit(attack)) {
                    case 0:
                        // player1 missed
                        break;
                    case 1:
                        // player1 hit
                        board_1 = true;
                        board_2 = false;
                        break;
                    case 2:
                        // player1 hit and sank a ship
                        if (board2.lost()) {
                            continue;
                        }
                        break;
                }
            } else if (board_2 == true) {
                board_2 = false;
                board_1 = true;
                switch (board1.check_hit(attack)) {
                    case 0:
                        // player2 missed
                        break;
                    case 1:
                        // player2 hit 
                        board_2 = true;
                        board_1 = false;
                        break;
                    case 2:
                        // player2 hit and sank a ship
                        if (board1.lost()) {
                            continue;
                        }
                        break;
                }
            }
        }
        // add winning sequence
    }*/ 
}
