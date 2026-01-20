package src.logic;
import java.io.*;

import src.gui.*;
import src.coms.*;

public class game {
    private board board1;
    private battlescreen gui;
    private NetworkPlayer coms;
    private boolean user_t;
    private int[] s_dir;
    // add gui and coms classes
    public game(int size, int[] ship_set, battlescreen g, NetworkPlayer n) {
        board1 = new board(size, ship_set);
        this.gui = g;
        this.coms = n;
        for (int i = 0; i < gui.DIR.length; i++) {
            if(gui.DIR[i]) s_dir[i] = 0;
            else s_dir[i] = 1;
        }
 
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
                // read and copy opp_hit
                for (int i = 0; i < s_set.length; i++) {
                    String row = reader.readLine();
                    String[] segments = row.trim().split("\\s+");
                    for (int j = 0; j < s_set.length; j++) {
                        new_b.opp_hit[i][j] = Integer.parseInt(segments[j]);
                    }
                }
            } 
            board1 = new_b;
            // load a new battlescreen with the correct ships and hits 
        } catch (Exception e) {
            System.err.println("Failed saving: " + e.getMessage());

        }
    }
    
    public void setup_board() {
        for (int i = 0; i < gui.SHIPS.length; i++) {
            board1.place_ship(gui.COR[i], s_dir[i], i);
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
                // start_opp_turn() here interferes with the gui since the gui relies on the return from send_shot() 
                // but start_opp_turn() calls to wait for a shot/save message from the other opponent meaning right now 
                // when a player shoots and misses his shot will only be revealed after all the shots from the opponent
                // replace the return with a method call to the gui that visuallizes the hit
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
        try {
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
        } catch (Exception e) {
            return get_hit(p);
        }
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
            try {
                coms.receiveMessageWithSaveHandling();
            } catch (Exception e) {
                
            }
        }
    }
} 
