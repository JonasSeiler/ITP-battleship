package src.coms;

import src.logic.*;
import java.io.*;
import java.util.*;

/**
 * Bot is a subclass of NetworkPlayer that acts like a client object and also
 * generates the responsible answers
 * @author Jamie Kopp
 * @author Jonas Seiler
 */
public class Bot extends NetworkPlayer {
    /**
     * Board object used for part of the logic
     */
    public Board ownBoard;
    /**
     * a object for randomization
     */
    private Random random;
    /**
     * coordiante of bot generated shot
     */
    private Coordinate generatedshot;
    /**
     * game object for part of the logic
     */
    private Game user; 

    /**
     * a variavle for the game difficulty set to medium = 2 as default
     */
    private int difficulty = 2;
    /**
     * list of the ships lenghts which are left
     */
    private List<Integer> shiplengthsleft;
    /**
     * number of shipssunk
     */
    private int shipssunk = 0;
    /**
     * probability map for making shooting decision
     */
    private int [][] probmap;
    /**
     * booleand to check if probability map is initialized
     */
    private boolean probmapinit = false;

    /**
     * list of the last hits for making shooting decisions
     */
    private List<Coordinate> hitseq;
    /**
     * boolean to see if hunting mode is on
     */
    private boolean hunting = true;
    /**
     * coordinate of current target
     */
    private Coordinate currenttarget = null;
    /**
     * variable to find out the direction of enemy ship
     */
    private int currentdirection = -1;
    /**
     * boolean to check if last shot was a hit or not
     */
    private boolean lastshothit = false;

    /**
     * variable to check if game has been loaded
     */
    private boolean isLoadGame = false;
    /**
     * the id of the game to be loaded
     */
    private String loadGameId = null;
    /**
     * the board size of the game
     */
    private int boardSize = 0;
    /**
     * array of the ship lengths
     */
    private int[] shiplengths;
    /**
     * boolean array to check which direction have been tried out when making shooting decisions
     */
    private boolean[] triedDirections = new boolean[4]; // 0: right, 1: down, 2: left, 3: up
    /**
     * boolean for if one end of a ship has been found
     */
    private boolean foundPositiveEnd = false;
    /**
     *  boolean for if another end of a ship has been found
     */
    private boolean foundNegativeEnd = false;

    //
    // General functions
    //

    /**
     * {@inheritDoc} 
     */
    @Override
    public void start() {
        random = new Random();
        hitseq = new ArrayList<>();
        hunting = true;
        currentdirection = -1;
        lastshothit = false;
        isConnected = true;
        resetDirectionTracking();
    }

    /**
     * setter method for setting the difficulty of the algorithm 
     * @param lvl of the difficulty
     * @throws IOException
     */
    public void setdifficulty(int lvl) throws IOException {
        if (lvl >= 1 && lvl <= 3) {
            this.difficulty = lvl;
        } else {
            throw new IOException("Ungueltiger Schwierigkeitsgrad: " + lvl + " (muss 1-3 sein)");
        }
    }

    /**
     * getter method for difficulty
     *
     * @return the difficulty currently set
     */
    public int getdifficulty() {
        return difficulty;
    } 

    /**
     * sets the board size sent by user
     *
     * @param size of board
     * @return
     * @throws IOException
     */
    public boolean sendSize(int size) throws IOException {
        this.boardSize = size;

        return true;
    }

    /**
     * sets variables related to the ships array sent by user
     *
     * @param shiplengths
     * @return true if successfull
     * @throws IOException
     */
    public boolean sendShips(int[] shiplengths) throws IOException {
        this.shiplengths = shiplengths;

        ownBoard = new Board(boardSize, shiplengths);

        shiplengthsleft = new ArrayList<>();
        for (int l : shiplengths) {
            shiplengthsleft.add(l);
        }

        shipssunk = 0;

        placeships(shiplengths);

        if (difficulty == 2 || difficulty == 3) {
            probmap = new int[boardSize][boardSize];
            initprobmap();
        }
        return true;
    }

    /**
     * 
     * loads the Bot's side of the Game including the difficulty and probability map
     *
     * @param id
     * @return
     * @throws IOException
     */
    public boolean sendLoad(String id) throws IOException {
        isLoadGame = true;
        loadGameId = id;
        load_game(id + "Bot");
        return true;
    }

    public void load_game(String filepath) {
        String userHome = System.getProperty("user.home");
        String saveDir;
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println(filepath);

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
        System.out.println(savePath);
        try(BufferedReader reader = new BufferedReader(new FileReader(savePath))) {
            System.out.println("Reading: ");
            // read grid size
            int new_s = Integer.parseInt(reader.readLine().trim());
            
            System.out.println("Reading: " + new_s);
            // read Ship array
            String line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            int[] s_set = new int[parts.length];
            System.out.print("Reading: ");
            for (int i = 0; i < s_set.length; i++) {
                s_set[i] = Integer.parseInt(parts[i]);
                System.out.print(s_set[i] + " ");
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

            int diff = Integer.parseInt(reader.readLine().trim());
            difficulty = diff;
            // read and copy probmap
            this.probmap = new int[new_s][new_s];
            for (int i = 0; i < new_s; i++) {
                String row = reader.readLine();
                String[] segments = row.trim().split("\\s+");
                for (int j = 0; j < new_s; j++) {
                    probmap[i][j] = Integer.parseInt(segments[j]);
                }
            }

            ownBoard = new_b;
            boardSize = new_s;
            shiplengths = s_set;

            // load a new battlescreen with the correct ships and hits 
        } catch (Exception e) {
            System.err.println("Failed loading: " + e.getMessage());

        }
    }


    /**
     * sets gamestarted to true if user send ready
     *
     * @return true so game can start
     * @throws IOException
     */
    public boolean sendReady() throws IOException {
        gameStarted = true;
        return true;
    }

    /**
     * registers the shot from user
     *
     * @param row
     * @param col
     * @return
     * @throws IOException
     */
    @Override
    public int sendShot(int row, int col) throws IOException {

        // Korrekte Grenzpruefung (0-basiert)
        if (row < 0 || row >= boardSize || 
        col < 0 || col >= boardSize) {
            throw new IOException("Ungueltige Koordinaten: (" + row + ", " + col + ") fuer Board-Groesse " + boardSize);
        }

        Coordinate shot = new Coordinate(row, col);  // 0-basiert!

        return ownBoard.check_hit(shot);
    }

    /**
     * the bot generates a shot and register the shot at the players bot
     *
     * @throws IOException
     */
    @Override
    public void receiveMessagewsave() throws IOException {
        generatedshot = genshot();

        if (generatedshot == null) {
            throw new IOException("Konnte keinen Schuss generieren");
        }

        if (user == null) {
            throw new IOException("User-Referenz nicht gesetzt");
        }

        user.get_hit(generatedshot);
    }

    /**
     * Bot uses answer from his generated shot to update shooting algorithm 
     *
     * @param answerCode
     * @throws IOException
     */
    @Override
    public void sendAnswer(int answerCode) throws IOException {
        if (generatedshot == null) {
        }

        ownBoard.register_shot(generatedshot, answerCode);

        updatetracking(generatedshot, answerCode);

        if (difficulty == 3 || difficulty == 2) {
            updateProbabilityMap(generatedshot, answerCode);
        }
    }

    /**
     * saves the Bot's side of the Game by saving the board first and then appending the probabillity map and difficulty
     *
     * @param id 
     * @return 
     */
    @Override 
    public boolean sendSave(String id) {
        ownBoard.save_game(id + "Bot");
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

        String savePath = saveDir + id + "Bot";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath, true))) {
            writer.newLine();
            writer.write(String.valueOf(difficulty));
            writer.newLine();

            for (int i = 0; i < boardSize; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < boardSize; j++) {
                    row.append(probmap[i][j] + " ");
                }
                writer.write(row.toString());
                writer.newLine();
            }
        } catch(Exception e) {

        }
        return true;
    }
    
    /**
     * for bot this method does nothing
     * 
     * @param message
     * @throws IOException
     */
    @Override
    protected void sendMessage(String message) throws IOException {
    }

    /**
     * for bot this method does nothing
     *
     * @return
     * @throws IOException
     */
    @Override
    protected String receiveMessage() throws IOException {
        return "";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        isConnected = false;
        gameStarted = false;

        if (hitseq != null) {
            hitseq.clear();
        }
        if (shiplengthsleft != null) {
            shiplengthsleft.clear();
        }

        hunting = true;
        currentdirection = -1;
        lastshothit = false;
        currenttarget = null;
        resetDirectionTracking();
    }

    //
    // automatic shipplacement related functions
    //

    /**
     * method for the bot to place his ships
     *
     * @param shiplengths
     * @throws IOException
     */
    private void placeships(int[] shiplengths) throws IOException {
        for (int i = 0; i < shiplengths.length; i++) {
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 1000;

            while (!placed && attempts < maxAttempts) {
                attempts++;
                boolean horizontal = random.nextBoolean();
                int x = random.nextInt(boardSize);
                int y = random.nextInt(boardSize);

                Coordinate start = new Coordinate(x, y);

                if (ownBoard.isPlacementvalid(start, horizontal ? 0 : 1, i)) {
                    ownBoard.place_ship(start, horizontal ? 0 : 1, i);
                    placed = true;
                }
            }

            if (!placed) {
                throw new IOException("Konnte Schiff der Laenge " + shiplengths[i] + 
                    " nicht platzieren (Leicht-Stufe). " +
                    "Versuche: " + maxAttempts);            
            }
        }
    }



    //
    // shooting related functions
    //

    /**
     * method to generate to shot according to the difficulty set
     *
     * @return
     */
    private Coordinate genshot() {
        try {
            switch(difficulty) {
                case 1: return genshoteasy();
                case 2: return genshotmedium();
                case 3: return genshothard();
                default: return genshotmedium();
            }
        } catch (Exception e) {
            System.err.println("couldnt generate shot: " + e);
            return new Coordinate(0, 0);
        } 
    }

    /**
     * shoots completely random shots at the Board 
     *
     * @return shot Coordinate
     * @throws IOException
     */
    private Coordinate genshoteasy() throws IOException {
        return getrandomcell();
    }

    /**
     * shoots in a parity pattern and huntships once found
     *
     * @return shot Coordinate
     * @throws IOException
     */
    private Coordinate genshotmedium() throws IOException {
        if (!hunting) {
            return target();
        } else {
            if (!probmapinit) {
                initprobmap();
            }
            return gethighestprobcell();
        }
    }

    /**
     * shoots in a parity pattern and huntships once found, updates the pattern according to smallest ship and doesnt shoot cells surrounding sunken ships
     * 
     * @return
     * @throws IOException
     */
    private Coordinate genshothard() throws IOException {
        if (!hunting) {
            return target();
        } else {
            if (!probmapinit) {
                initprobmap();
            }
            return gethighestprobcell();
        }

    }


    // Probabilitymap related functions

    /**
     * method for initializing the probability map
     */
    private void initprobmap() {

        if (boardSize <= 0) {
            return;
        }


        if (difficulty == 2) {
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) { 
                    probmap[i][j] = ((i+j) % 2 == 0) ? 1 : 0;          
                }
            }
        }

        if (difficulty == 3) {
            int smallestship = 5;

            for (int Ship : shiplengths) {
                if (Ship < smallestship) {
                    smallestship = Ship;
                }
            }

            setprobmaptopattern(smallestship);

        }

        probmapinit = true;
    }

    /**
     *  sets the pattern of the probmap 
     *
     */
    private void setprobmaptopattern(int s) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) { 
                probmap[i][j] = ((i + (s -1) * j) % s == 0) ? 1 : 0;          
            }
        }
    }

    /**
     * resest tracking direction
     *
     */
    private void resetDirectionTracking() {
        triedDirections = new boolean[4];
        foundPositiveEnd = false;
        foundNegativeEnd = false;
    }

    /**
     * updates the shooting pattern to the one of the smallest ship
     *
     */
    private void updateprobmapforsmallestship() {
        if (difficulty != 3 || shiplengthsleft == null || shiplengthsleft.isEmpty()) {
            return;
        } 

        int smallestship = 5;

        for (int Ship : shiplengthsleft) {
            if (Ship < smallestship) {
                smallestship = Ship;
            }
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) { 
                if (ownBoard.opp_hit[i][j] == -1 && probmap[i][j] != -1) {
                    probmap[i][j] = ((i + (smallestship -1) * j) % smallestship == 0) ? 1 : 0;          
                }
            }
        }
    }


    /**
     * method that gives back the cell with the highest probility
     *
     * @return the coordinate
     * @throws IOException
     */
    private Coordinate gethighestprobcell() throws IOException {
        if (!probmapinit) {
            initprobmap();
        }

        if (probmap == null) {
            return getrandomcell();
        }

        int maxProb = -1;
        List<Coordinate> bestCells = new ArrayList<>();

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (isCellAvailable(x, y) && probmap[x][y] > 0) {
                    if (probmap[x][y] > maxProb) {
                        maxProb = probmap[x][y];
                        bestCells.clear();
                        bestCells.add(new Coordinate(x, y));
                    } else if (probmap[x][y] == maxProb) {
                        bestCells.add(new Coordinate(x, y));
                    }
                }
            }
        }

        if (bestCells.isEmpty()) {
            return getrandomcell(); 
        }

        return bestCells.get(random.nextInt(bestCells.size()));
    }

    /**
     * method for updating the probability of the map
     * 
     * @param shot
     * @param result
     */
    private void updateProbabilityMap(Coordinate shot, int result) {
        if (!probmapinit || probmap == null) return;

        probmap[shot.x][shot.y] = 0;

        if (difficulty == 2) {
            return;
        }

        if (result == 1) {
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int x = shot.x + dir[0];
                int y = shot.y + dir[1];
                if (x >= 0 && x < boardSize && y >= 0 && y < boardSize && ownBoard.opp_hit[x][y] == -1) {
                    if (probmap[x][y] != -1) {
                        probmap[x][y] += 10;
                    }

                }
            }
        } else if (result == 2) {



            List<Coordinate> allShipCells = new ArrayList<>(hitseq);

            for (Coordinate shipCell : hitseq) {
                int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
                // Mark all 8 surrounding cells (including diagonals)
                for (int[] dir : dirs) {
                    int x = shipCell.x + dir[0];
                    int y = shipCell.y + dir[1];
                    if (x >= 0 && x < boardSize && y >= 0 && y < boardSize &&
                    ownBoard.opp_hit[x][y] == 1) {  // Another hit cell
                        Coordinate adj = new Coordinate(x, y);
                        boolean alreadyInList = false;
                        for (Coordinate c : allShipCells) {
                            if (c.x == adj.x && c.y == adj.y) {
                                alreadyInList = true;
                                break;
                            }
                        }
                        if (!alreadyInList) {
                            allShipCells.add(adj);
                        }
                    }
                }
            }

            for (Coordinate shipCell : allShipCells) {
                // Mark all 8 surrounding cells (including diagonals)
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int x = shipCell.x + dx;
                        int y = shipCell.y + dy;

                        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
                            probmap[x][y] = -1;
                        }
                    }
                }
            }

            hitseq.clear();

            // Update probability map for new smallest Ship
            updateprobmapforsmallestship();
        }
    }





    /**
     * method for checking if a cell is available
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean isCellAvailable(int x, int y) {
        if (ownBoard == null || ownBoard.opp_hit == null) {
            return false;
        }
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            return false;
        }

        // Check if cell has been shot already
        if (ownBoard.opp_hit[x][y] != -1) {
            return false;
        }

        // For hard difficulty, also check if cell is marked as off-limits
        if (difficulty == 3 && probmap != null) {
            // Cells marked as -1 are off-limits (adjacent to sunken ships)
            if (probmap[x][y] == -1) {
                return false;
            }
        }

        return true;
    }
    /** 
     * converting coordinate to two integers
     *
     * @param coord
     * @return
     */
    private boolean isCellAvailable(Coordinate coord) {
        if (coord == null) return false;
        return isCellAvailable(coord.x, coord.y);
    }

    /**
     * generates a random cell
     * 
     * @return a coordinate
     * @throws IOException
     */
    private Coordinate getrandomcell() throws IOException {
        if (ownBoard == null || ownBoard.opp_hit == null) {
            throw new IOException("Board nicht initialisiert");
        }

        List<Coordinate> availableCells = new ArrayList<>();

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (isCellAvailable(x, y)) {
                    availableCells.add(new Coordinate(x, y));
                }
            }
        }

        if (availableCells.isEmpty()) {
            throw new IOException("Keine freien Zellen mehr verfuegbar");
        }

        return availableCells.get(random.nextInt(availableCells.size()));
    }



    /**
     * a method for targeting a ship
     *
     * @return
     * @throws IOException
     */
    private Coordinate target() throws IOException {
        if (currenttarget == null || hitseq.isEmpty()) {
            hunting = true;
            // Return to pattern shooting
            if (!probmapinit) {
                initprobmap();
            }
            return gethighestprobcell();
        }

        if (hitseq.size() == 1) {
            return tryDirectionsFromFirstHit();
        }

        if (currentdirection == -1) {
            determineDirectionFromHits();
        }

        if (currentdirection != -1) {
            if (!foundPositiveEnd) {
                Coordinate lastHit = hitseq.get(hitseq.size() - 1);

                int newX = lastHit.x + getDirectionDeltaX(currentdirection);
                int newY = lastHit.y + getDirectionDeltaY(currentdirection);

                if (isCellAvailable(newX, newY)) {
                    return new Coordinate(newX, newY);
                } else {
                    foundPositiveEnd = true;
                }
            }

            if (!foundNegativeEnd) {
                Coordinate firstHit = hitseq.get(0);
                int oppositeDir = getOppositeDirection(currentdirection);
                int newX = firstHit.x + getDirectionDeltaX(oppositeDir);
                int newY = firstHit.y + getDirectionDeltaY(oppositeDir);

                if (isCellAvailable(newX, newY)) {
                    return new Coordinate(newX, newY);
                } else {
                    foundNegativeEnd = true;
                }
            }

            hunting = true;
            currentdirection = -1;
            currenttarget = null;
            resetDirectionTracking();
            return gethighestprobcell();
        }

        hunting = true;
        return gethighestprobcell();
    }

    /**
     * method for trying directions of a successfull hit
     *
     */
    private Coordinate tryDirectionsFromFirstHit() throws IOException {
        Coordinate firstHit = hitseq.get(0);
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int i = 0; i < 4; i++) {
            if (!triedDirections[i]) {
                int newX = firstHit.x + directions[i][0];
                int newY = firstHit.y + directions[i][1];

                if (isCellAvailable(newX, newY)) {
                    triedDirections[i] = true;
                    return new Coordinate(newX, newY);
                } else {
                    triedDirections[i] = true;
                }
            }
        }

        hunting = true;
        currenttarget = null;
        resetDirectionTracking();
        return gethighestprobcell();

    }

    /**
     * method for desciding which direction the ship that has been hit is 
     */
    private void determineDirectionFromHits() {
        if (hitseq.size() < 2) return;

        Coordinate first = hitseq.get(0);
        Coordinate second = hitseq.get(1);

        if (first.x == second.x) {
            // Horizontal
            if (second.y > first.y) {
                currentdirection = 0; // right
            } else {
                currentdirection = 2; // left
            }
        } else if (first.y == second.y) {
            // Vertical
            if (second.x > first.x) {
                currentdirection = 1; // down
            } else {
                currentdirection = 3; // up
            }
        }
    }

    /**
     * method for getting the direction
     *
     * @param from
     * @param to
     * @return
     */
    private int getDirection(Coordinate from, Coordinate to) {
        if (to.x == from.x) {
            return (to.y > from.y) ? 0 : 2;
        } else {
            return (to.x > from.x) ? 1 : 3;
        }
    }

    /**
     * method to get opposite direction 
     *
     * @param dir
     * @return
     */
    private int getOppositeDirection(int dir) {
        return (dir + 2) % 4;
    }

    /**
     * getter method related to target algorithm
     * 
     * @param dir
     * @return
     */
    private int getDirectionDeltaX(int dir) {
        switch(dir) {
            case 1: return 1;
            case 3: return -1;
            default: return 0;
        }
    }

    /** 
     * getter method related to target algorithm
     *
     * @param dir
     * @return
     */
    private int getDirectionDeltaY(int dir) {
        switch(dir) {
            case 0: return 1;
            case 2: return -1;
            default: return 0;
        }
    }

    /**
     * method for update the tracking algorithm
     *
     * @param shot
     * @param result
     */
    private void updatetracking(Coordinate shot, int result) {
        if (shot == null) return;

        lastshothit = (result == 1 || result == 2);

        if (result == 1 || result == 2) {
            boolean alreadyInList = false;
            for (Coordinate c : hitseq) {
                if (c.x == shot.x && c.y == shot.y) {
                    alreadyInList = true;
                    break;
                }
            }

            if (!alreadyInList) {
                hitseq.add(shot);
            }

            if (currenttarget == null) {
                currenttarget = shot;
            }

            if (result == 2) {
                // Ship sunk
                int sunkLength = hitseq.size();

                // Remove Ship from shiplengthsleft (for hard difficulty)
                if (shiplengthsleft != null) {
                    for (int i = 0; i < shiplengthsleft.size(); i++) {
                        if (shiplengthsleft.get(i) == sunkLength) {
                            shiplengthsleft.remove(i);
                            break;
                        }
                    }
                }

                shipssunk++;

                // Clear tracking for next Ship
                // hitseq.clear(); is needed to set all surrounding cells as -1 
                currenttarget = null;
                currentdirection = -1;
                hunting = true;
                resetDirectionTracking();
            } else {
                // Hit but not sunk - continue hunting this Ship
                hunting = false;
            }
        } else {
            // Miss
            if (!hunting) {

            }
        }
    }



    /**
     * setter method to set the user object
     *
     * @param u
     */
    public void set_game(Game u) {
        this.user = u;
    }
}
