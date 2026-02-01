package src.logic;
import java.io.*;

import javax.swing.SwingWorker;

import src.gui.*;
import src.coms.*;

/**
 * the Game class uses a Board object to implement the games logic, controls the turn order
 * of the player and is used to process the communication with the opponent
 * @author Jonas Seiler
 */
public class Game {
    /**
     * the player's board
     */
    private Board board1;
    /**
     * gui screen the logic needs to interact with
     */
    private Battlescreen gui;
    /**
     * connection to the opponent
     */
    private NetworkPlayer coms;
    /**
     * 1 = it's the players turn; 0 = it's the opponents turn; 2 = game over
     */
    public int u_turn = 1;
    /**
     * array that is used for converting the bool DIR array from the gui to the 0/1 array that the logic uses
     */
    private int[] s_dir;
    /**
     * array that the Mainframe uses to initialize the battlescreen when a game is loaded
     */
    public Coordinate[] s_heads;
    /**
     * array that the Mainframe uses to initialize the battlescreen when a game is loaded
     */
    public boolean[] gui_dir;
    /**
     * array that the Mainframe uses to initialize the battlescreen when a game is loaded
     */
    public int[] s_len;
    /**
     * board size
     */
    public int size;

    /**
     * initializes the board, connects the NetworkPlayer and initializes the s_dir array
     *
     * @param size 
     * @param ship_set 
     * @param n 
     */
    public Game(int size, int[] ship_set, NetworkPlayer n) {
        board1 = new Board(size, ship_set);
        this.coms = n;
        s_dir = new int[ship_set.length];
    }

    /**
     * reads data from a file that saved the previous Game session 
     * to initialize a Board object to the same position as before and start 
     * the Battlescreen in the same state as the Board
     *
     * @param filepath the name of a file that stores Game data from a previous Game session
     */
    public void load_game(String filepath) {
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

        String savePath = saveDir + filepath;

        try(BufferedReader reader = new BufferedReader(new FileReader(savePath))) {
           
            // read grid size
            int new_s = Integer.parseInt(reader.readLine().trim());
            
            // read Ship array
            String line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            int[] s_set = new int[parts.length];
            for (int i = 0; i < s_set.length; i++) {
                s_set[i] = Integer.parseInt(parts[i]);
            }

            // init new Board
            Board new_b = new Board(new_s, s_set);
            
            // read and copy ship_pos 
            for (int i = 0; i < new_s; i++) {
                String row = reader.readLine();
                String[] segments = row.trim().split("\\s+");
                for (int j = 0; j < new_s; j++) {
                    new_b.ship_pos[i][j] = Integer.parseInt(segments[j]);
                }
            }
            // read and copy hit_pos
            for (int i = 0; i < new_s; i++) {
                String row = reader.readLine();
                String[] segments = row.trim().split("\\s+");
                for (int j = 0; j < new_s; j++) {
                    new_b.hit_pos[i][j] = Integer.parseInt(segments[j]);
                }
            }

            // read and copy fleet
            for (Ship s : new_b.fleet) {
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int i = 0; i < s.length; i++) {
                    s.pos[i].x = Integer.parseInt(parts[i]);
                    if(i == 0) s.head_x = s.pos[i].x;
                }
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int i = 0; i < s.length; i++) {
                    s.pos[i].y = Integer.parseInt(parts[i]);
                    if(i == 0) s.head_y = s.pos[i].y;
                }
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int i = 0; i < s.length; i++) {
                    s.lifes[i] = Integer.parseInt(parts[i]);
                }
            s.dir = Integer.parseInt(reader.readLine().trim());
            } 
            // read and copy opp_hit
            for (int i = 0; i < new_s; i++) {
                String row = reader.readLine();
                String[] segments = row.trim().split("\\s+");
                for (int j = 0; j < new_s; j++) {
                    new_b.opp_hit[i][j] = Integer.parseInt(segments[j]);
                }
            }
            int hp = Integer.parseInt(reader.readLine().trim());
            new_b.opp_hp = hp;
            board1 = null;
            board1 = new_b;

            s_dir = null;
            s_dir = new int[s_set.length];
            size = new_s;
            s_heads = new Coordinate[s_set.length];
            gui_dir = new boolean[s_set.length];
            s_len = new int[s_set.length];
            s_len = s_set;
            for (int i = 0; i < s_set.length; i++) {
                s_heads[i] = new Coordinate(board1.fleet[i].head_x, board1.fleet[i].head_y); 
                gui_dir[i] = (board1.fleet[i].dir == 0) ? true : false;
            }
            // load a new Battlescreen with the correct ships and hits 
        } catch (Exception e) {
            System.err.println("Failed loading: " + e.getMessage());

        }
    }

    /**
     * syncs the gui to the board object when a game is loaded from a file
     */
    public void load_gui() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(board1.opp_hit[i][j] != -1) {
                    gui.colorEnemyShip(i, j, board1.opp_hit[i][j]);
                }
                if(board1.hit_pos[i][j] == 1) {
                    int ans = board1.check_hit(new Coordinate(i, j));
                    gui.colorPlayerShip(i, j, ans);
                }
            }
        }
    }
    /**
     * sets the Battlescreen and converts the bool direction array that the gui uses
     * into the int direction array the logic uses
     *
     * @param b 
     */
    public void set_gui(Battlescreen b) {
        this.gui = b; 
        for (int i = 0; i < gui.DIR.length; i++) {
            if(gui.DIR[i]) s_dir[i] = 0;
            else s_dir[i] = 1;
        }
        

    } 

    /**
     * szncs the Board with the Battlescreen after the user placed all his ships
     */
    public void setup_board() {
        Coordinate[] c = gui.COR;
        for (int i = 0; i < gui.SHIPS.length; i++) {
            board1.place_ship(c[i], s_dir[i], i);
        }        
    }
    /**
     * forwards the Coordinate the user is shooting at to the network 
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
                board1.register_shot(new Coordinate(x, y), response);
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
     * saves the Game by calling the Board objects save_game() method 
     * and sends save id to the network so the enemy also saves its 
     * own Game state
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
        if (u_turn == 1) {
            start_local_turn();
        } else {
            start_opp_turn();
        }
    }
    
    /**
     * identical to save_game, but this time the enemy wants to 
     * save the Game instead of the user 
     *
     * @param file 
     */
    public void save_opp_game(String file) {
        board1.save_game(file);
        if (u_turn == 1) {
            start_local_turn();
        } else {
            start_opp_turn();
        }
    }

    /**
     * when the opponent is shooting at the players Board the Game automatically
     * answers and forwards the Coordinate and answer to the gui to visualize the shot
     *
     * @param p Coordinate of the position the opponent is shooting at
     */
    public void get_hit(Coordinate p) {
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
     * checks if the Game is over, if not allows the user to play
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
     * checks if the Game is over, if not allows the enemy to play its turn 
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
                        coms.receiveMessagewsave();
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
