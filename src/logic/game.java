package src.logic;
import java.io.*;

import src.gui.battlescreen;
import src.coms.Server;

public class game {
    private board board1;
    private battlescreen gui;
    private Server coms;
    private boolean user_t;
    // add gui and coms classes
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
            // load opp_hit here
            board1 = new_b;
            // load a new battlescreen with the correct ships and hits 
        } catch (Exception e) {
            System.err.println("Failed saving: " + e.getMessage());

        }
    }
    
    public int send_shot(int x, int y) {
        // turn user turn ui off
        try {
        int response = coms.sendShot(x, y); // response = 0,1,2
            // add enemy field to board for save/load and for shot validation
            board1.register_shot(new coordinate(x, y), response);
            if(board1.won()) {
                // add win sequence here
            }
            if(response == 0) {
                coms.sendPass();
                start_opp_turn();
            } else {
                start_local_turn();
            }
        return response;
        } catch (Exception e) {
            return send_shot(x, y);
        }
    }

    public void save_game() {
        long u_time = java.time.Instant.now().toEpochMilli();
        String file = "TB_" + u_time;
        board1.save_game(file);
        try {
        coms.sendSave(file);
        } catch(Exception e) {
            
        }
    }
    
    public void save_opp_game(String file) {
        board1.save_game(file);
        try {
        coms.sendSave(file);
        } catch(Exception e) {
            
        }

    }

    public int get_hit(coordinate p) {
        int answer = board1.check_hit(p); 
        coms.sendAnswer(answer);
        switch(answer) {
            case 0:
                gui.colorPlayerShip(p.x, p.y, answer);
                start_local_turn();
                break;
            case 1:
                gui.colorPlayerShip(p.x, p.y, answer);
                start_opp_turn();
                break;
            case 2:
                gui.colorPlayerShip(p.x, p.y, answer);
                start_opp_turn();
                if(board1.lost()) {
                // add losing sequence here
                }
                break;
            case -1:

                break;
        }
        return answer; 
        
    }

    public void start_local_turn() {
        if(board1.game_over()) {
            // activate turn ui for user
            // wait for shot from user
        }
    }
    
    public void start_opp_turn() {
        if(board1.game_over()) {
            // disable user turn ui    
            // wait for network shot from opp
            // ask for a generallized recieveMsg function senShotWithSaveCheck but instead of just checking for a save check for a save or shot, to handle both
        }
    }
} 
