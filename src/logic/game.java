package src.logic;
import java.io.*;

import javax.swing.SwingWorker;

import src.gui.*;
import src.coms.*;

/**
 * the game class uses a board object to implement the games logic, controls the turn order
 * of the player and is used to process the communication with the opponent
 *
 */
public class game {
    private board board1;
    private battlescreen gui;
    private NetworkPlayer coms;
    public int u_turn = 1;
    private int[] s_dir;
    // add gui and coms classes
    public game(int size, int[] ship_set, battlescreen g, NetworkPlayer n) {
        board1 = new board(size, ship_set);
        this.gui = g;
        this.coms = n;
        s_dir = new int[ship_set.length];
         for (int i = 0; i < gui.DIR.length; i++) {
            if(gui.DIR[i]) s_dir[i] = 0;
            else s_dir[i] = 1;
        }
    }

    /**
     * reads data from a file that saved the previous game session 
     * to initialize a board object to the same position as before and start 
     * the battlescreen in the same state as the board
     *
     * @param filepath the name of a file that stores game data from a previous game session
     */
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

    /**
     * setup_board() syncs the board with the battlescreen after the user 
     * placed all his ships
     */
    public void setup_board() {
        coordinate[] c = gui.COR;
        for (int i = 0; i < gui.SHIPS.length; i++) {
            board1.place_ship(c[i], s_dir[i], i);
        }        
    }
    /**
     * forwards the coordinate the user is shooting at to the network 
     * after the opponent answers register the shot for potential saving
     * and properly choose whos turn it is or if the user won
     *
     * @param x horizontal position of the shot fired by the user
     * @param y vertical position of the shot fired by the user
     */
    public void send_shot(int x, int y) {
        gui.disableUI();
        new SwingWorker<Void, Void>() {
            public int response = 0;
            protected Void doInBackground() {
                try {
                    response = coms.sendShot(x, y); // response = 0,1,2
                } catch(Exception e) {
                    System.err.println("sending shot error: " + e);
                }
                return null;
            }
            protected void done() {
                board1.register_shot(new coordinate(x, y), response);
                board1.dec_hp(response);
                gui.shot_answer(response);
                if(board1.won()) {
                    // add win sequence here1
                    start_local_turn();
                    return;
                }
                if(response == 0) {
                    start_opp_turn(); 
                } else {
                    start_local_turn();
                }
            }
        }.execute();
    }

    /**
     * saves the game by calling the board objects save_game() method 
     * and sends save id to the network so the enemy also saves its 
     * own game state
     */
    public void save_game() {
        // consider moving the filename that gets generated to the start of the program so the file gets overridden if you save multiple times during a session
        long u_time = java.time.Instant.now().toEpochMilli();
        String file = "TB_" + u_time;
        board1.save_game(file);
        try {
        coms.sendSave(file);
        } catch(Exception e) {

            System.err.println("Network error caught in save_game: " + e);
        }
    }
    
    /**
     * identical to save_game, but this time the enemy wants to 
     * save the game instead of the user 
     *
     * @param file 
     */
    public void save_opp_game(String file) {
        board1.save_game(file);
        try {
        coms.sendSave(file);
        } catch(Exception e) {
            
            System.err.println("Network error caught in save_opp_game: " + e);
        }

    }

    /**
     * the opponent is shooting at the players board the game automatically
     * answers forwards the coordinate and answer to the gui
     *
     * @param p coordinate of the position the opponent is shooting at
     */
    public void get_hit(coordinate p) {
        int answer = board1.check_hit(p);
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() {
                try {
                coms.sendAnswer(answer);
                } catch (Exception e) {
                    System.err.println("Network error caught in get_hit: " + e);
                }
                return null;
            }
            protected void done() { 
                switch(answer) {
                    case 0:
                    gui.colorPlayerShip(p.x, p.y, answer);
                    start_local_turn();
                    break;
                    case 1:
                    gui.colorPlayerShip(p.x, p.y, answer);
                    u_turn = 0;
                    start_opp_turn();
                    break;
                    case 2:
                    gui.colorPlayerShip(p.x, p.y, answer);
                    if(board1.lost()) {
                        start_local_turn();
                        break;
                    }
                    u_turn = 0;
                    start_opp_turn();
                    break;
                }
            }
        }.execute();
    }
 
    /**
     * checks if the game is over, if not allows the user to play
     * by activating the users ui
     */
    public void start_local_turn() {
        if(!board1.game_over()) {
            System.out.println("start local turn");
            gui.enableUI();
            u_turn = 1;
            // wait for shot from user
        } else if(u_turn != 2) {
            if(board1.won()) {
                gui.game_over(true);
                u_turn = 2;
            }
            if(board1.lost()) {
                gui.game_over(false);   
                u_turn = 2;
            }
        }
    }
    
    /**
     * checks if the game is over, if not allows the enemy to play its turn 
     * by listening for a shot, save or pass
     */
    public void start_opp_turn() {
        if(!board1.game_over()) {
            System.out.println("start opp turn");
            gui.disableUI();
            new SwingWorker<Void, Void>() {
                protected Void doInBackground() {
                    try {
                        if(u_turn == 1) {
                            u_turn = 0;
                            coms.sendPass();
                        }
                        coms.receivemessagewsave();
                    } catch (Exception e) {
                        System.err.println("Network error caught in start_opp_turn(): " + e);
                    }
                    return null;
                }
            }.execute();
        } else if (u_turn != 2){
             if(board1.won()) {
                gui.game_over(true);
                u_turn = 2;
            }
            if(board1.lost()) {
                gui.game_over(false);   
                u_turn = 2;
            }
        }
    }
} 
