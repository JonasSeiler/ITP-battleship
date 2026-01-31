package src.coms;

import src.logic.*;
import java.io.*;
import java.util.*;

/**
 * 
 */
public class Bot extends NetworkPlayer {
    /**
     * 
     */
    public board ownBoard;
    /**
     * 
     */
    private Random random;
    /**
     * 
     */
    private coordinate generatedshot;
    /**
     * 
     */
    private game user; 

    /**
     * 
     */
    private int difficulty = 2;

    /**
     * 
     */
    private boolean paritymode = true;

    /**
     * 
     */
    private List<Integer> shiplengthsleft;
    /**
     * 
     */
    private int shipssunk = 0;
    /**
     * 
     */
    private int [][] probmap;
    /**
     * 
     */
    private boolean probmapinit = false;

    /**
     * 
     */
    private List<coordinate> hitseq;
    /**
     * 
     */
    private boolean hunting = true;
    /**
     * 
     */
    private coordinate currenttarget = null;
    /**
     * 
     */
    private int currentdirection = -1;
    /**
     * 
     */
    private boolean lastshothit = false;

    /**
     * 
     */
    private boolean isLoadGame = false;
    /**
     * 
     */
    private String loadGameId = null;
    /**
     * 
     */
    private int boardSize = 0;
    /**
     * 
     */
    private int[] shiplengths;


    //
    // General functions
    //

    /**
     * 
     */
    @Override
    public void start() {
        random = new Random();
        hitseq = new ArrayList<>();
        hunting = true;
        currentdirection = -1;
        lastshothit = false;
        isConnected = true;
    }

    /**
     * 
     * @param lvl
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
     * 
     * @return
     */
    public int getdifficulty() {
        return difficulty;
    } 

    /**
     * 
     * @param size
     * @return
     * @throws IOException
     */
    public boolean sendSize(int size) throws IOException {
        this.boardSize = size;
        
        return true;
    }
    
    /**
     * 
     * @param shiplengths
     * @return
     * @throws IOException
     */
    public boolean sendShips(int[] shiplengths) throws IOException {
        this.shiplengths = shiplengths;
        
        ownBoard = new board(boardSize, shiplengths);

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
     * loads the Bot's side of the game
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
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            System.out.println("Reading: ");
            // read grid size
            int new_s = Integer.parseInt(reader.readLine().trim());
            
            System.out.println("Reading: " + new_s);
            // read ship array
            String line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            int[] s_set = new int[parts.length];
            System.out.print("Reading: ");
            for (int i = 0; i < s_set.length; i++) {
                s_set[i] = Integer.parseInt(parts[i]);
                System.out.print(s_set[i] + " ");
            }

            // init new board
            board new_b = new board(new_s, s_set);
            
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
            for (ship s : new_b.fleet) {
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
     * 
     * @return
     * @throws IOException
     */
    public boolean sendReady() throws IOException {
        gameStarted = true;
        return true;
    }
    
    /**
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
    
        coordinate shot = new coordinate(row, col);  // 0-basiert!
    
        return ownBoard.check_hit(shot);
    }
    
    /**
     * 
     * @throws IOException
     */
    @Override
    public void receivemessagewsave() throws IOException {
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
     * saves the Bot's side of the game
     *
     * @param id 
     * @return 
     */
    @Override 
    public boolean sendSave(String id) {
        ownBoard.save_game(id + "Bot");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(id + "Bot", true))) {
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
     * 
     * @param message
     * @throws IOException
     */
    @Override
    protected void sendmessage(String message) throws IOException {
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
    @Override
    protected String receivemessage() throws IOException {
        return "";
    }
    
    /**
     * 
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
    }
    
    //
    // automatic shipplacement related functions
    //

    /**
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

                coordinate start = new coordinate(x, y);

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
     * 
     * @return
     */
    private coordinate genshot() {
        try {
        switch(difficulty) {
            case 1: return genshoteasy();
            case 2: return genshotmedium();
            case 3: return genshothard();
            default: return genshotmedium();
            }
        } catch (Exception e) {
            System.err.println("couldnt generate shot: " + e);
            return new coordinate(0, 0);
        } 
    }

    /**
     *  shoots completely random shots at the board 
     * @return shot coordinate
     * @throws IOException
     */
    private coordinate genshoteasy() throws IOException {
        return getrandomcell();
    }

    /**
     * shoots in a parity pattern and huntships once found
     * @return shot coordinate
     * @throws IOException
     */
    private coordinate genshotmedium() throws IOException {
        // verwende gleichen code wie von hardshot und passe 
        // die logik in initprobmap und gehighestprobcell an
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
     * shoots in a parity pattern and huntships once found
     * 
     * @return
     * @throws IOException
     */
    private coordinate genshothard() throws IOException {
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
     * 
     */
    private void initprobmap() {
        if (boardSize <= 0) {
            return;
        }


        // paritiy muster setzen
        if (difficulty == 2) {
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) { 
                       probmap[i][j] = ((i+j) % 2 == 0) ? 1 : 0;          
                }
            }
        }

        // probmuster enstprechend des kleinsten Schiffes setzen
        if (difficulty == 3) {
            // neuercode
            int smallestship = 5;

            for (int ship : shiplengths) {
                if (ship < smallestship) {
                    smallestship = ship;
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
     *
     *
     */
    private void updateprobmapforsmallestship() {
        if (difficulty != 3 || shiplengthsleft == null || shiplengthsleft.isEmpty()) {
            return;
        } 

        int smallestship = 5;

        for (int ship : shiplengths) {
            if (ship < smallestship) {
                smallestship = ship;
            }
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) { 
                if (ownBoard.opp_hit[i][j] == -1) {
                    probmap[i][j] = ((i + (smallestship -1) * j) % smallestship == 0) ? 1 : 0;          
                }
            }
        }
    }


    /**
     * 
     * @return
     * @throws IOException
     */
    private coordinate gethighestprobcell() throws IOException {
        if (!probmapinit) {
            initprobmap();
        }

        if (probmap == null) {
            return getrandomcell();
        }

        int maxProb = -1;
        List<coordinate> bestCells = new ArrayList<>();

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (isCellAvailable(x, y)) {
                    if (probmap[x][y] > maxProb) {
                        maxProb = probmap[x][y];
                        bestCells.clear();
                        bestCells.add(new coordinate(x, y));
                    } else if (probmap[x][y] == maxProb) {
                        bestCells.add(new coordinate(x, y));
                    }
                }
            }
        }

        if (bestCells.isEmpty()) {
            throw new IOException("Keine verfuegbaren Zellen fuer Wahrscheinlichkeitsauswahl");
        }

        return bestCells.get(random.nextInt(bestCells.size()));
    }

    /**
     * 
     * @param shot
     * @param result
     */
    private void updateProbabilityMap(coordinate shot, int result) {
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
                if (x >= 0 && x < boardSize && y >= 0 && y < boardSize && probmap[x][y] > 0) {
                    probmap[x][y] += 10;
                }
            }
        } else if (result == 2) {
            for (coordinate shipCell : hitseq) {
                // Mark all 8 surrounding cells (including diagonals)
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int x = shipCell.x + dx;
                        int y = shipCell.y + dy;

                        // Skip the ship cell itself
                        if (dx == 0 && dy == 0) continue;

                        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
                            probmap[x][y] = 0;
                        }
                    }
                }
            }

            // Update probability map for new smallest ship
           updateprobmapforsmallestship();
        }
    }





    /**
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
        return ownBoard.opp_hit[x][y] == -1;
    }

    /** 
     * 
     * @param coord
     * @return
     */
    private boolean isCellAvailable(coordinate coord) {
        if (coord == null) return false;
        return isCellAvailable(coord.x, coord.y);
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private coordinate getrandomcell() throws IOException {
        if (ownBoard == null || ownBoard.opp_hit == null) {
            throw new IOException("Board nicht initialisiert");
        }
    
        List<coordinate> availableCells = new ArrayList<>();
    
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (isCellAvailable(x, y)) {
                    availableCells.add(new coordinate(x, y));
                }
            }
        }
    
        if (availableCells.isEmpty()) {
            throw new IOException("Keine freien Zellen mehr verfuegbar");
        }
    
        return availableCells.get(random.nextInt(availableCells.size()));
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private coordinate getparitycell() throws IOException {
        if (ownBoard == null || ownBoard.opp_hit == null) {
            throw new IOException("Board nicht initialisiert");
        }
    
        List<coordinate> availableCells = new ArrayList<>();
        int parity = paritymode ? 0 : 1;
    
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (isCellAvailable(x, y) && (x + y) % 2 == parity) {
                    availableCells.add(new coordinate(x, y));
                }
            }
        }
    
        if (availableCells.isEmpty()) {
            parity = 1 - parity;
            paritymode = !paritymode;
        
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    if (isCellAvailable(x, y) && (x + y) % 2 == parity) {
                        availableCells.add(new coordinate(x, y));
                    }
                }
            }
        }
    
        if (availableCells.isEmpty()) {
            return getrandomcell();
        }
    
        return availableCells.get(random.nextInt(availableCells.size()));
    }

    

    /**
     * 
     * @return
     * @throws IOException
     */
    private coordinate target() throws IOException {
        if (currenttarget == null || hitseq.isEmpty()) {
            hunting = true;
            // Return to pattern shooting
            if (!probmapinit) {
                initprobmap();
            }
            return gethighestprobcell();
        }

        // First, try to determine direction if not known
        if (currentdirection == -1 && hitseq.size() >= 2) {
            // We have at least 2 hits, we can determine direction
            coordinate first = hitseq.get(0);
            coordinate second = hitseq.get(1);

            if (first.x == second.x) {
                // Horizontal ship
                currentdirection = 0; // 0 = horizontal (left/right)
            } else if (first.y == second.y) {
                // Vertical ship
                currentdirection = 1; // 1 = vertical (up/down)
            }
        }

        if (currentdirection == -1 && hitseq.size() == 1) {
            // Only one hit, try all directions
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            List<coordinate> possibleShots = new ArrayList<>();

            for (int[] dir : directions) {
                int newX = currenttarget.x + dir[0];
                int newY = currenttarget.y + dir[1];

                if (isCellAvailable(newX, newY)) {
                    possibleShots.add(new coordinate(newX, newY));
                }
            }

            if (!possibleShots.isEmpty()) {
                return possibleShots.get(random.nextInt(possibleShots.size()));
            }
        } else if (currentdirection != -1) {
            // We know the direction, shoot along it
            coordinate lastHit = hitseq.get(hitseq.size() - 1);

            // Try in positive direction first
            int newX = lastHit.x;
            int newY = lastHit.y;

            if (currentdirection == 0) { // Horizontal
                newY = lastHit.y + 1;
            } else { // Vertical
                newX = lastHit.x + 1;
            }

            if (isCellAvailable(newX, newY)) {
                return new coordinate(newX, newY);
            }

            // Try in negative direction
            newX = hitseq.get(0).x;
            newY = hitseq.get(0).y;

            if (currentdirection == 0) { // Horizontal
                newY = hitseq.get(0).y - 1;
            } else { // Vertical
                newX = hitseq.get(0).x - 1;
            }

            if (isCellAvailable(newX, newY)) {
                return new coordinate(newX, newY);
            }
        }

        // If we get here, we couldn't find a valid shot in the known direction
        // Switch back to hunting mode
        hunting = true;
        currentdirection = -1;
        currenttarget = null;

        if (!probmapinit) {
            initprobmap();
        }
        return gethighestprobcell();
    }

    /**
     * 
     * @param from
     * @param to
     * @return
     */
    private int getDirection(coordinate from, coordinate to) {
        if (to.x == from.x) {
            return (to.y > from.y) ? 0 : 2;
        } else {
            return (to.x > from.x) ? 1 : 3;
        }
    }

    /**
     * 
     * @param dir
     * @return
     */
    private int getOppositeDirection(int dir) {
        return (dir + 2) % 4;
    }

    /**
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
     * 
     * @param shot
     * @param result
     */
    private void updatetracking(coordinate shot, int result) {
        if (shot == null) return;

        lastshothit = (result == 1 || result == 2);

        if (result == 1 || result == 2) {
            hitseq.add(shot);

            if (currenttarget == null) {
                currenttarget = shot;
            }

            if (result == 2) {
                // Ship sunk
                int sunkLength = hitseq.size();

                // Remove ship from shiplengthsleft (for hard difficulty)
                if (shiplengthsleft != null) {
                    // Find and remove the first occurrence of this length
                    Integer lengthToRemove = sunkLength;
                    shiplengthsleft.remove(lengthToRemove);
                }

                shipssunk++;

                // Clear tracking for next ship
                hitseq.clear();
                currenttarget = null;
                currentdirection = -1;
                hunting = true;
            } else {
                // Hit but not sunk - continue hunting this ship
                hunting = false;
            }
        } else {
            // Miss
            if (!hunting) {
                // We were targeting a ship but missed
                // If we have a direction, try the other way
                if (currentdirection != -1) {
                    // We'll handle direction switching in the target() method
                } else {
                    // No direction yet, just continue with current ship
                }
            }
        }
    }
    
    

    /**
     * 
     * @param u
     */
    public void set_game(game u) {
        this.user = u;
    }
}
