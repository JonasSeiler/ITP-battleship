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
    private board ownBoard;
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
    private List<Integer> shiplenghtsleft;
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
        
        if (difficulty == 3) {
            initprobmap();
        }

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

        shiplenghtsleft = new ArrayList<>();
        for (int l : shiplengths) {
            shiplenghtsleft.add(l);
        }
        
        shipssunk = 0;

        placeships(shiplengths);

        return true;
    }
    
    /**
     * 
     * @param id
     * @return
     * @throws IOException
     */
    public boolean sendLoad(String id) throws IOException {
        isLoadGame = true;
        loadGameId = id;
        return true;
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
    
        if (row < 0 || row > boardSize || col < 0 || col > boardSize) {
            throw new IOException("Ungueltige Koordinaten: (" + row + ", " + col + ")");
        }
        
        coordinate shot = new coordinate(row, col);
        
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

        if (difficulty == 3) {
            updateProbabilityMap(generatedshot, answerCode);
        }
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
        if (shiplenghtsleft != null) {
            shiplenghtsleft.clear();
        }

        hunting = true;
        currentdirection = -1;
        lastshothit = false;
        currenttarget = null;
    }
    
    /**
     * 
     * @param shiplenghts
     * @throws IOException
     */
    private void placeships(int[] shiplenghts) throws IOException {
        switch(difficulty) {
            case 1: placeshipseasy(shiplenghts); break;
            case 2: placeshipsmedium(shiplenghts); break;
            case 3: placeshipshard(shiplenghts); break;
            default: placeshipsmedium(shiplenghts); break;
        }
    }

    /**
     * 
     * @param shiplengths
     * @throws IOException
     */
    private void placeshipseasy(int[] shiplengths) throws IOException {
    
        for (int i = 0; i < shiplengths.length; i++) {
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 300;
        
            while (!placed && attempts < maxAttempts) {
                attempts++;
                boolean horizontal = random.nextBoolean();
                int x = random.nextInt(boardSize);
                int y = random.nextInt(boardSize);
            
                coordinate start = new coordinate(x, y);
            
                if (canPlaceShipWithSpacing(start, shiplengths[i], horizontal, 1)) {
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

    /**
     * 
     * @param shiplengths
     * @throws IOException
     */
    private void placeshipsmedium(int[] shiplengths) throws IOException {
    
        Integer[] sortedIndices = getSortedShipIndices(shiplengths);
    
        for (int idx : sortedIndices) {
            int length = shiplengths[idx];
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 500;
        
            while (!placed && attempts < maxAttempts) {
                attempts++;
            
                boolean horizontal = random.nextBoolean();
            
                int minCoord = 1;
                int maxCoord = boardSize - 2;
            
                if (maxCoord < minCoord) {
                    minCoord = 0;
                    maxCoord = boardSize - 1;
                }
            
                int x = minCoord + random.nextInt(Math.max(1, maxCoord - minCoord + 1));
                int y = minCoord + random.nextInt(Math.max(1, maxCoord - minCoord + 1));
            
                coordinate start = new coordinate(x, y);
            
                if (canPlaceShipWithSpacing(start, length, horizontal, 1)) {
                    ownBoard.place_ship(start, horizontal ? 0 : 1, idx);
                    placed = true;
                }
            
                if (!placed && attempts > maxAttempts / 2) {
                    x = random.nextInt(boardSize);
                    y = random.nextInt(boardSize);
                    start = new coordinate(x, y);
                
                    if (canPlaceShipWithSpacing(start, length, horizontal, 1)) {
                        ownBoard.place_ship(start, horizontal ? 0 : 1, idx);
                        placed = true;
                    }
                }
            }
        
            if (!placed) {
                throw new IOException("Konnte Schiff der Laenge " + length + 
                                " nicht platzieren (Mittel-Stufe). " +
                                "Versuche: " + maxAttempts);
            }
        }
    }

    /**
     * 
     * @param shiplengths
     * @throws IOException
     */
    private void placeshipshard(int[] shiplengths) throws IOException {
        Integer[] sortedIndices = getSortedShipIndices(shiplengths);
    
        for (int idx : sortedIndices) {
            int length = shiplengths[idx];
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 800;
        
            while (!placed && attempts < maxAttempts) {
                attempts++;
            
                boolean horizontal;
                if (boardSize < 10 && length >= 4) {
                    horizontal = false;
                } else {
                    horizontal = random.nextBoolean();
                }
            
                int x, y;
            
                if (length >= 4) {
                    int margin = (int)(boardSize * 0.15);
                    x = margin + random.nextInt(boardSize - 2 * margin);
                    y = margin + random.nextInt(boardSize - 2 * margin);
                } else if (length == 3) {
                    int margin = (int)(boardSize * 0.075);
                    x = margin + random.nextInt(boardSize - 2 * margin);
                    y = margin + random.nextInt(boardSize - 2 * margin);
                } else {
                    if (random.nextFloat() < 0.7) {
                        int margin = (int)(boardSize * 0.1);
                        x = margin + random.nextInt(boardSize - 2 * margin);
                        y = margin + random.nextInt(boardSize - 2 * margin);
                    } else {
                        x = random.nextInt(boardSize);
                        y = random.nextInt(boardSize);
                    }
                }
            
                coordinate start = new coordinate(x, y);
            
                if (canPlaceShipWithSpacing(start, length, horizontal, 1)) {
                    ownBoard.place_ship(start, horizontal ? 0 : 1, idx);
                    placed = true;
                    continue;
                }
            
                if (!placed) {
                    horizontal = !horizontal;
                    if (canPlaceShipWithSpacing(start, length, horizontal, 1)) {
                        ownBoard.place_ship(start, horizontal ? 0 : 1, idx);
                        placed = true;
                    }
                }
            
                if (!placed && attempts > maxAttempts * 0.9) {
                    x = random.nextInt(boardSize);
                    y = random.nextInt(boardSize);
                    horizontal = random.nextBoolean();
                    start = new coordinate(x, y);
                    
                    if (canPlaceShipWithSpacing(start, length, horizontal, 1)) {
                        ownBoard.place_ship(start, horizontal ? 0 : 1, idx);
                        placed = true;
                    }
                }
            }
        
            if (!placed) {
                placed = attemptSystematicPlacement(idx, length);
            }
        
            if (!placed) {
                throw new IOException("Konnte Schiff der Laenge " + length + 
                " nicht platzieren (Schwer-Stufe). " +
                "Versuche: " + maxAttempts);
            }
        }
    }

    /**
     * 
     * @param shipIndex
     * @param length
     * @return
     */
    private boolean attemptSystematicPlacement(int shipIndex, int length) {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                coordinate start = new coordinate(x, y);
        
                if (canPlaceShipWithSpacing(start, length, true, 1)) {
                    ownBoard.place_ship(start, 0, shipIndex);
                    return true;
                }
        
                if (canPlaceShipWithSpacing(start, length, false, 1)) {
                    ownBoard.place_ship(start, 1, shipIndex);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param shiplengths
     * @return
     */
    private Integer[] getSortedShipIndices(int[] shiplengths) {
        if (shiplengths == null || shiplengths.length == 0) {
            return new Integer[0];
        }
    
        Integer[] indices = new Integer[shiplengths.length];
        for (int i = 0; i < shiplengths.length; i++) {
            indices[i] = i;
        }
    
        Arrays.sort(indices, (a, b) -> Integer.compare(shiplengths[b], shiplengths[a]));
    
        return indices;
    }

    /**
     * 
     * @param start
     * @param length
     * @param horizontal
     * @param spacing
     * @return
     */
    private boolean canPlaceShipWithSpacing(coordinate start, int length, boolean horizontal, int spacing) {
        if (ownBoard == null || ownBoard.ship_pos == null || boardSize <= 0) {
            return false;
        }
    
        int startX = Math.max(0, start.x - spacing);
        int startY = Math.max(0, start.y - spacing);
        int endX, endY;
    
        if (horizontal) {
            if (start.y + length - 1 + spacing >= boardSize) {
                return false;
            }

            endX = Math.min(boardSize - 1, start.x + spacing);
            endY = Math.min(boardSize - 1, start.y + length - 1 + spacing);

        } else {
            if (start.x + length - 1 + spacing >= boardSize) {
                return false;
            }

            endX = Math.min(boardSize - 1, start.x + length - 1 + spacing);
        
            endY = Math.min(boardSize - 1, start.y + spacing);
        }
    
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (ownBoard.ship_pos[x][y] != 0) {
                    return false;
                }
            }
        }
    
        return true;
    }

    /**
     * 
     */
    private void initprobmap() {
        if (boardSize <= 0) {
            return;
        }
    
        probmap = new int[boardSize][boardSize];
    
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                probmap[i][j] = 1;
            }
        }
    
        probmapinit = true;
    }

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
     * 
     * @return
     * @throws IOException
     */
    private coordinate genshoteasy() throws IOException {
        return getrandomcell();
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private coordinate genshotmedium() throws IOException {
        if (!hunting) {
            return target();
        } else {
            return getparitycell();
        }
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    private coordinate genshothard() throws IOException {
        if (!probmapinit) {
            initprobmap();
        }
        return gethighestprobcell();
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
     * @return
     * @throws IOException
     */
    private coordinate target() throws IOException {
        if (currenttarget == null || hitseq.isEmpty()) {
            hunting = true;
            return getparitycell();
        }
    
        if (currentdirection == -1) {
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
                coordinate shot = possibleShots.get(random.nextInt(possibleShots.size()));
                currentdirection = getDirection(currenttarget, shot);
                return shot;
            }
        } 
        else {
            coordinate lastHit = hitseq.get(hitseq.size() - 1);
        
            int newX = lastHit.x + getDirectionDeltaX(currentdirection);
            int newY = lastHit.y + getDirectionDeltaY(currentdirection);
        
            if (isCellAvailable(newX, newY)) {
                return new coordinate(newX, newY);
            } 
            else {
                int oppositeDir = getOppositeDirection(currentdirection);
                newX = currenttarget.x + getDirectionDeltaX(oppositeDir);
                newY = currenttarget.y + getDirectionDeltaY(oppositeDir);
                
                if (isCellAvailable(newX, newY)) {
                    currentdirection = oppositeDir;
                    return new coordinate(newX, newY);
                }
            }
        }
    
        hunting = true;
        currentdirection = -1;
        currenttarget = null;
        return getparitycell();
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
                int sunkLength = hitseq.size();
                if (shiplenghtsleft != null) {
                    shiplenghtsleft.removeIf(len -> len == sunkLength);
                }
                shipssunk++;
            
                hitseq.clear();
                currenttarget = null;
                currentdirection = -1;
                hunting = true;
            } else {
                hunting = false;
            }
        } else {
            if (!hunting) {
                if (currentdirection != -1) {
                    currentdirection = getOppositeDirection(currentdirection);
                } else {
                    hunting = true;
                    hitseq.clear();
                    currenttarget = null;
                }
            }
        }
    }
    
    /**
     * 
     * @param shot
     * @param result
     */
    private void updateProbabilityMap(coordinate shot, int result) {
        if (!probmapinit || probmap == null) return;
    
        probmap[shot.x][shot.y] = 0;
    
        if (result == 1 || result == 2) {
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int x = shot.x + dir[0];
                int y = shot.y + dir[1];
                if (x >= 0 && x < boardSize && y >= 0 && y < boardSize && probmap[x][y] > 0) {
                    probmap[x][y] += 10;
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
