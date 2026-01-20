package src.coms;

import src.logic.*;
import java.io.*;
import java.util.*;

/**
 * Bot-Klasse für einspieler Modus gegen Computer
 * Implementiert die gleiche Schnittstelle wie Server/Client aber ohne Netzwerk
 */
public class Bot extends NetworkPlayer {
    // Bot Spiel-Logik
    private board ownBoard;          // Eigenes Spielfeld mit Schiffen
    private board enemyBoard;       // Spielfeld für Tracking gegnerischer Schüsse
    private Random random;
    private List<coordinate> possibleShots;
    private coordinate lastShot;
    private boolean lastShotHit = false;
    private coordinate shot;
    private boolean shotHit = false;
    private coordinate botgencoordiante;
    private game user; 

    // Setup-Information
    private boolean isLoadGame = false;
    private String loadGameId = null;
    private int boardSize = 0;
    private int[] shipLengths;
    
    // Für die interne Kommunikation (Bot vs Bot-Logik)
    private Queue<String> incomingMessages = new LinkedList<>();
    private Queue<String> outgoingMessages = new LinkedList<>();
    
    /**
     * Startet den Bot (keine Netzwerkverbindung nötig)
     */
    @Override
    public void start() {
        random = new Random();
        possibleShots = new ArrayList<>();
        isConnected = true;
    }
    
    /**
     * Sendet Spielgröße (Bot als Server)
     */
    public boolean sendSize(int size) throws IOException {
        this.boardSize = size;
        // Initialisiere mögliche Schüsse
        initializePossibleShots();
        return true; // Bot antwortet sich selbst mit "done"
    }
    
    /**
     * Sendet Schiffe (Bot als Server)
     */
    public boolean sendShips(int[] shipLengths) throws IOException {
        this.shipLengths = shipLengths;
        
        // Eigenes Board initialisieren und Schiffe platzieren
        ownBoard = new board(boardSize, shipLengths);
        placeShipsAutomatically(shipLengths);
        
        return true; // Bot antwortet sich selbst mit "done"
    }
    
    /**
     * Sendet Load-Befehl (Bot als Server) own board initilisieren
     */
    public boolean sendLoad(String id) throws IOException {
        isLoadGame = true;
        loadGameId = id;
        // Hier würde Spielstand geladen werden
        return true; // Bot antwortet sich selbst mit "ok"
    }
    
    /**
     * Sendet Ready-Nachricht
     */
    public boolean sendReady() throws IOException {
        gameStarted = true;
        return true; // Bot antwortet sich selbst mit "ready"
    }
    
    /**
     * Sendet Schuss (Bot schießt auf menschlichen Spieler)
     */
    @Override
    public int sendShot(int row, int col) throws IOException {
        // Konvertiere zu 0-basierten Koordinaten für interne Logik
        shot = new coordinate(row, col);
        
        return ownBoard.check_hit(shot);
    }
    
    /**
     * Generiert einen intelligenten Schuss basierend auf aktueller Spielsituation
     */
    public coordinate generateSmartShot() {
        // Wenn letzter Schuss ein Treffer war, schieße in der Nähe
        if (lastShotHit && lastShot != null) {
            coordinate nearbyShot = getNearbyShot(lastShot);
            if (nearbyShot != null) {
                return nearbyShot;
            }
        }
        
        // Ansonsten zufälligen Schuss aus möglichen Schüssen wählen
        if (!possibleShots.isEmpty()) {
            int index = random.nextInt(possibleShots.size());
            coordinate shot = possibleShots.get(index);
            possibleShots.remove(index);
            return shot;
        }
        
        // Fallback: komplett zufälliger Schuss (0-basiert)
        return new coordinate(random.nextInt(boardSize), random.nextInt(boardSize));
    }
    
    /**
     * Empfängt Setup-Nachrichten (Bot als Client)
     */
    public GameConfig receiveSetup() throws IOException {
        // Für Einzelspieler nehmen wir an, Bot ist Server
        throw new IOException("Bot im Einzelspielermodus muss als Server fungieren (sendSize/sendShips verwenden)");
    }
    
    /**
     * Empfängt Nachricht mit Save-Handling (wartet auf menschlichen Spieler)
     */
    @Override
    void receiveMessageWithSaveHandling() throws IOException {
        // generate shot from bot

        user.get_hit(botgencoordiante);

    }
    
    /**
     * Sendet Antwort auf Schuss des menschlichen Spielers
     */
    @Override
    public void sendAnswer(int answerCode) throws IOException {
        
        ownBoard.register_shot(botgencoordiante, answerCode);

        // later access the 2d array (opp_hit) and use it to make shot decision in generate smart shot

    }
    
    @Override
    protected void sendMessage(String message) throws IOException {
        // Bot sendet Nachricht an sich selbst (interne Verarbeitung)
        }
    
    @Override
    protected String receiveMessage() throws IOException {
        // Bot empfängt Nachricht von sich selbst
        if (!incomingMessages.isEmpty()) {
            return incomingMessages.poll();
        }
        // In einer echten Implementierung würde hier auf externe Eingabe gewartet
        throw new IOException("Keine Nachricht verfügbar");
        return ""
    }
    
    @Override
    public void close() {
        isConnected = false;
        gameStarted = false;
        incomingMessages.clear();
        outgoingMessages.clear();
    }
    
    // ===== BOT-SPIEL-LOGIK =====
    
    /**
     * Schiffe automatisch auf eigenem Board platzieren
     */
    private void placeShipsAutomatically(int[] shipLengths) {
        for (int i = 0; i < shipLengths.length; i++) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                attempts++;
                boolean horizontal = random.nextBoolean();
                int x = random.nextInt(boardSize);
                int y = random.nextInt(boardSize);
                
                coordinate start = new coordinate(x, y);
                
                // Prüfe ob Schiff platziert werden kann
                if (canPlaceShip(start, shipLengths[i], horizontal)) {
                    // Platziere Schiff (0 = horizontal, 1 = vertikal laut board.place_ship)
                    ownBoard.place_ship(start, horizontal ? 0 : 1, i);
                    placed = true;
                }
            }
            
            if (!placed) {
                System.err.println("Konnte Schiff der Länge " + shipLengths[i] + " nicht platzieren!");
            }
        }
    }
    
    /**
     * Prüft ob Schiff platziert werden kann
     */
    private boolean canPlaceShip(coordinate start, int length, boolean horizontal) {
        if (horizontal) {
            if (start.y + length > boardSize) return false;
            for (int i = 0; i < length; i++) {
                if (ownBoard.ship_pos[start.x][start.y + i] != 0) return false;
            }
        } else {
            if (start.x + length > boardSize) return false;
            for (int i = 0; i < length; i++) {
                if (ownBoard.ship_pos[start.x + i][start.y] != 0) return false;
            }
        }
        return true;
    }
    
    /**
     * Initialisiert Liste aller möglichen Schüsse (0-basiert)
     */
    private void initializePossibleShots() {
        possibleShots.clear();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                possibleShots.add(new coordinate(x, y));
            }
        }
    }
    
    /**
     * Entfernt Schuss aus möglichen Schüssen
     */
    private void removeShotFromPossible(coordinate shot) {
        possibleShots.removeIf(s -> s.x == shot.x && s.y == shot.y);
    }
    
    /**
     * Findet Schuss in der Nähe eines Treffers
     */
    private coordinate getNearbyShot(coordinate lastShot) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        List<coordinate> nearby = new ArrayList<>();
        
        for (int[] dir : directions) {
            int newX = lastShot.x + dir[0];
            int newY = lastShot.y + dir[1];
            
            if (newX >= 0 && newX < boardSize && 
                newY >= 0 && newY < boardSize) {
                
                // Prüfe ob dieser Schuss noch möglich ist
                for (coordinate shot : possibleShots) {
                    if (shot.x == newX && shot.y == newY) {
                        nearby.add(shot);
                        break;
                    }
                }
            }
        }
        
        if (!nearby.isEmpty()) {
            coordinate shot = nearby.get(random.nextInt(nearby.size()));
            possibleShots.remove(shot);
            return shot;
        }
        
        return null;
    }
    
    /**
     * Simuliert Antwort des menschlichen Spielers auf Bot-Schuss
     */
    private int simulateHumanAnswer(coordinate shot) {
        // In einer echten Implementierung würde dies von der GUI/Spiel-Logik kommen
        // Hier simulieren wir zufällige Antworten
        return random.nextInt(3); // 0, 1 oder 2
    }
    
    /**
     * Simuliert Schuss des menschlichen Spielers
     */
    private coordinate simulateHumanShot() {
        // In einer echten Implementierung würde dies von der GUI kommen
        // Hier: Zufälliger Schuss (0-basiert)
        return new coordinate(random.nextInt(boardSize), random.nextInt(boardSize));
    }
    
    // ===== HILFSMETHODEN =====
    
    public void set_user(game u) {
        this.user = u;
    }
}
