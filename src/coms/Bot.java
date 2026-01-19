import java.io.*;
import java.util.*;

/**
 * Bot-Klasse für einspieler Modus gegen Computer
 * Implementiert die gleiche Schnittstelle wie Server/Client aber ohne Netzwerk
 */
public class Bot extends NetworkPlayer {
    // Bot-Spezifische Variablen
    private int[][] ownBoard;       // Eigenes Spielfeld mit Schiffen
    private int[][] enemyBoard;     // Spielfeld für gegnerische Schüsse
    private List<int[]> ships;      // Liste der Schiffe
    private int boardSize;
    
    // Shot-Logik
    private Random random;
    private List<int[]> possibleShots;
    private int lastShotRow = -1;
    private int lastShotCol = -1;
    private boolean lastShotHit = false;
    private String currentSaveId = null;
    
    // Setup-Information
    private boolean isLoadGame = false;
    private String loadGameId = null;
    
    /**
     * Startet den Bot (keine Netzwerkverbindung nötig)
     */
    @Override
    public void start() throws IOException {
        random = new Random();
        possibleShots = new ArrayList<>();
        isConnected = true;
        System.out.println("Bot gestartet. Wähle 'size' für neues Spiel oder 'load' für geladenes Spiel.");
    }
    
    /**
     * Sendet Spielgröße (Bot als Server)
     */
    public boolean sendSize(int size) throws IOException {
        this.boardSize = size;
        ownBoard = new int[size][size];
        enemyBoard = new int[size][size];
        initializePossibleShots();
        System.out.println("Bot: Spielfeldgröße " + size + "x" + size + " gesetzt.");
        return true; // Bot antwortet sich selbst mit "done"
    }
    
    /**
     * Sendet Schiffe (Bot als Server)
     */
    public boolean sendShips(int[] shipLengths) throws IOException {
        this.ships = new ArrayList<>();
        for (int length : shipLengths) {
            ships.add(new int[]{length, 0, 0}); // Länge, startRow, startCol
        }
        
        // Schiffe automatisch platzieren
        placeShipsAutomatically(shipLengths);
        System.out.println("Bot: " + shipLengths.length + " Schiffe platziert.");
        return true; // Bot antwortet sich selbst mit "done"
    }
    
    /**
     * Sendet Load-Befehl (Bot als Server)
     */
    public boolean sendLoad(String id) throws IOException {
        isLoadGame = true;
        loadGameId = id;
        System.out.println("Bot: Lade Spiel mit ID " + id);
        return true; // Bot antwortet sich selbst mit "ok"
    }
    
    /**
     * Sendet Ready-Nachricht
     */
    public boolean sendReady() throws IOException {
        gameStarted = true;
        System.out.println("Bot: Bereit. Spiel beginnt!");
        return true; // Bot antwortet sich selbst mit "ready"
    }
    
    /**
     * Sendet Schuss (Bot schießt)
     */
    @Override
    public int sendShot(int row, int col) throws IOException {
        lastShotRow = row;
        lastShotCol = col;
        
        // Entferne diesen Schuss aus möglichen Schüssen
        removeShotFromPossible(row, col);
        
        // Simuliere Antwort des menschlichen Spielers
        // In der realen Implementierung würde dies von der Spiellogik kommen
        System.out.println("Bot schießt auf: " + row + ", " + col);
        
        // Hier würde normalerweise die Antwort vom menschlichen Spieler kommen
        // Für die Demo geben wir eine zufällige Antwort zurück
        int answer = simulateHumanAnswer(row, col);
        
        // Aktualisiere enemyBoard mit der Antwort
        enemyBoard[row-1][col-1] = answer + 1; // +1 um 0=leer, 1=Wasser, 2=Treffer, 3=Versenkt
        
        // Shot-Logik aktualisieren
        lastShotHit = (answer == 1 || answer == 2);
        
        return answer;
    }
    
    /**
     * Generiert einen intelligenten Schuss
     */
    public int[] generateSmartShot() {
        // Wenn letzter Schuss ein Treffer war, schieße in der Nähe
        if (lastShotHit && lastShotRow != -1 && lastShotCol != -1) {
            int[] nearbyShot = getNearbyShot(lastShotRow, lastShotCol);
            if (nearbyShot != null) {
                return nearbyShot;
            }
        }
        
        // Ansonsten zufälligen Schuss aus möglichen Schüssen wählen
        if (!possibleShots.isEmpty()) {
            int index = random.nextInt(possibleShots.size());
            int[] shot = possibleShots.get(index);
            possibleShots.remove(index);
            return shot;
        }
        
        // Fallback: komplett zufälliger Schuss
        return new int[]{random.nextInt(boardSize) + 1, random.nextInt(boardSize) + 1};
    }
    
    /**
     * Empfängt Setup-Nachrichten (Bot als Client)
     */
    public GameConfig receiveSetup() throws IOException {
        // Bot kann entweder als Server oder Client fungieren
        // Für Einzelspieler nehmen wir an, Bot ist Server
        throw new IOException("Bot im Einzelspielermodus muss als Server fungieren (sendSize/sendShips verwenden)");
    }
    
    /**
     * Empfängt Nachricht mit Save-Handling (wartet auf menschlichen Spieler)
     */
    @Override
    public MessageType receiveMessageWithSaveHandling() throws IOException {
        // Bot wartet auf Schuss des menschlichen Spielers
        // In einer realen Implementierung würde hier auf Benutzereingabe gewartet
        System.out.println("Bot: Warte auf Schuss des menschlichen Spielers...");
        
        // Für die Demo: Simuliere einen Schuss des menschlichen Spielers
        try {
            Thread.sleep(1000); // Kurze Pause für Realismus
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulierter Schuss des menschlichen Spielers (in Wirklichkeit von GUI/CLI)
        int[] humanShot = simulateHumanShot();
        
        // Überprüfe ob der Schuss ein Treffer war
        int row = humanShot[0];
        int col = humanShot[1];
        int result = checkShotOnOwnBoard(row, col);
        
        System.out.println("Menschlicher Spieler schießt auf: " + row + ", " + col);
        System.out.println("Ergebnis: " + 
            (result == 0 ? "Wasser" : result == 1 ? "Treffer" : "Versenkt"));
        
        return new MessageType(MessageType.Type.SHOT, humanShot);
    }
    
    /**
     * Sendet Antwort auf Schuss des menschlichen Spielers
     */
    @Override
    public void sendAnswer(int answerCode) throws IOException {
        // Bot antwortet auf Schuss des menschlichen Spielers
        System.out.println("Bot antwortet mit: " + 
            (answerCode == 0 ? "Wasser" : answerCode == 1 ? "Treffer" : "Versenkt"));
        
        // Bei Wasser oder Versenkt: Bot wird am Zug sein
        if (answerCode == 0 || answerCode == 2) {
            // Bot generiert nächsten Schuss
            int[] nextShot = generateSmartShot();
            int result = sendShot(nextShot[0], nextShot[1]);
            
            if (result == 0) {
                // Bot hat Wasser geschossen, menschlicher Spieler ist dran
                sendPass();
            }
            // Bei Treffer oder Versenkt bleibt Bot am Zug
        }
    }
    
    /**
     * Sendet Pass (Bot gibt Zug ab)
     */
    @Override
    public void sendPass() throws IOException {
        System.out.println("Bot passt. Menschlicher Spieler ist am Zug.");
    }
    
    /**
     * Schiffe automatisch platzieren
     */
    private void placeShipsAutomatically(int[] shipLengths) {
        for (int length : shipLengths) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                attempts++;
                boolean horizontal = random.nextBoolean();
                int row = random.nextInt(boardSize);
                int col = random.nextInt(boardSize);
                
                if (canPlaceShip(row, col, length, horizontal)) {
                    placeShip(row, col, length, horizontal);
                    placed = true;
                }
            }
            
            if (!placed) {
                System.err.println("Konnte Schiff der Länge " + length + " nicht platzieren!");
            }
        }
    }
    
    /**
     * Prüft ob Schiff platziert werden kann
     */
    private boolean canPlaceShip(int startRow, int startCol, int length, boolean horizontal) {
        if (horizontal) {
            if (startCol + length > boardSize) return false;
            for (int c = startCol; c < startCol + length; c++) {
                if (ownBoard[startRow][c] != 0) return false;
            }
        } else {
            if (startRow + length > boardSize) return false;
            for (int r = startRow; r < startRow + length; r++) {
                if (ownBoard[r][startCol] != 0) return false;
            }
        }
        return true;
    }
    
    /**
     * Platziert ein Schiff
     */
    private void placeShip(int startRow, int startCol, int length, boolean horizontal) {
        if (horizontal) {
            for (int c = startCol; c < startCol + length; c++) {
                ownBoard[startRow][c] = 1; // 1 = Teil eines Schiffes
            }
        } else {
            for (int r = startRow; r < startRow + length; r++) {
                ownBoard[r][startCol] = 1;
            }
        }
    }
    
    /**
     * Initialisiert Liste aller möglichen Schüsse
     */
    private void initializePossibleShots() {
        possibleShots.clear();
        for (int r = 1; r <= boardSize; r++) {
            for (int c = 1; c <= boardSize; c++) {
                possibleShots.add(new int[]{r, c});
            }
        }
    }
    
    /**
     * Entfernt Schuss aus möglichen Schüssen
     */
    private void removeShotFromPossible(int row, int col) {
        possibleShots.removeIf(shot -> shot[0] == row && shot[1] == col);
    }
    
    /**
     * Findet Schuss in der Nähe eines Treffers
     */
    private int[] getNearbyShot(int row, int col) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        List<int[]> nearby = new ArrayList<>();
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (newRow >= 1 && newRow <= boardSize && 
                newCol >= 1 && newCol <= boardSize) {
                
                // Prüfe ob dieser Schuss noch möglich ist
                for (int[] shot : possibleShots) {
                    if (shot[0] == newRow && shot[1] == newCol) {
                        nearby.add(shot);
                        break;
                    }
                }
            }
        }
        
        if (!nearby.isEmpty()) {
            int[] shot = nearby.get(random.nextInt(nearby.size()));
            possibleShots.remove(shot);
            return shot;
        }
        
        return null;
    }
    
    /**
     * Überprüft Schuss auf eigenem Board
     */
    private int checkShotOnOwnBoard(int row, int col) {
        // Konvertiere zu 0-basierten Indizes
        int r = row - 1;
        int c = col - 1;
        
        if (r < 0 || r >= boardSize || c < 0 || c >= boardSize) {
            return 0; // Ungültiger Schuss = Wasser
        }
        
        if (ownBoard[r][c] == 1) { // Treffer auf Schiff
            ownBoard[r][c] = 2; // Markiere als getroffen
            
            // Prüfe ob Schiff versenkt
            if (isShipSunk(row, col)) {
                return 2; // Versenkt
            }
            return 1; // Treffer
        }
        
        return 0; // Wasser
    }
    
    /**
     * Prüft ob Schiff versenkt
     */
    private boolean isShipSunk(int row, int col) {
        // Einfache Implementierung: Zähle alle getroffenen Teile
        // In einer vollständigen Implementierung würde man Schiffe tracken
        return false; // Vereinfachung für Demo
    }
    
    /**
     * Simuliert Antwort des menschlichen Spielers
     */
    private int simulateHumanAnswer(int row, int col) {
        // In der realen Implementierung kommt dies von der GUI/Spiel-Logik
        // Hier simulieren wir zufällige Antworten
        return random.nextInt(3); // 0, 1 oder 2
    }
    
    /**
     * Simuliert Schuss des menschlichen Spielers
     */
    private int[] simulateHumanShot() {
        // In der realen Implementierung kommt dies von der GUI
        // Hier: Zufälliger Schuss
        return new int[]{random.nextInt(boardSize) + 1, random.nextInt(boardSize) + 1};
    }
    
    /**
     * Gibt Spielfeld-Repräsentation zurück (für Debugging)
     */
    public String getBoardRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Eigenes Spielfeld:\n");
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                sb.append(ownBoard[r][c] == 0 ? "." : ownBoard[r][c] == 1 ? "S" : "X");
                sb.append(" ");
            }
            sb.append("\n");
        }
        
        sb.append("\nGegnerisches Spielfeld (Schüsse):\n");
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                char ch = '.';
                if (enemyBoard[r][c] == 1) ch = 'O'; // Wasser
                else if (enemyBoard[r][c] == 2) ch = 'T'; // Treffer
                else if (enemyBoard[r][c] == 3) ch = 'V'; // Versenkt
                sb.append(ch).append(" ");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    // Getter für Spielinformation
    public int getBoardSize() { return boardSize; }
    public boolean isLoadGame() { return isLoadGame; }
    public String getLoadGameId() { return loadGameId; }
}
