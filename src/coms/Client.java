import java.net.*;
import java.io.*;

public class Client {
    private final int PORT = 50000;
    private Socket socket;
    private BufferedReader in;
    private Writer out;
    
    // Status-Variablen
    private boolean isConnected = false;
    private boolean gameStarted = false;
    private String lastSaveId = null;
    
    /**
     * Stellt Verbindung zum Server her
     * @param address Server-IP-Adresse
     */
    public void connect(String address) throws IOException {
        socket = new Socket(address, PORT);
        isConnected = true;
        
        // Streams initialisieren
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());
    }
    
    /**
     * Empfängt und verarbeitet Setup-Nachrichten vom Server
     * @return GameConfig mit Spielkonfiguration
     */
    public GameConfig receiveSetup() throws IOException {
        String message = receiveMessage();
        
        if (message.startsWith("size")) {
            // Größe empfangen
            String[] parts = message.split(" ");
            int size = Integer.parseInt(parts[1]);
            
            // done senden
            sendMessage("done");
            
            // Schiffe empfangen
            message = receiveMessage();
            if (message.startsWith("ships")) {
                String[] shipParts = message.split(" ");
                int[] ships = new int[shipParts.length - 1];
                for (int i = 1; i < shipParts.length; i++) {
                    ships[i-1] = Integer.parseInt(shipParts[i]);
                }
                
                // done senden
                sendMessage("done");
                
                // ready empfangen und antworten
                message = receiveMessage();
                if (message.equals("ready")) {
                    sendMessage("ready");
                    gameStarted = true;
                    return new GameConfig(size, ships, false);
                }
            }
            
        } else if (message.startsWith("load")) {
            // Load empfangen
            String[] parts = message.split(" ");
            String loadId = parts.length > 1 ? parts[1] : "";
            
            // ok senden
            sendMessage("ok");
            
            // ready empfangen und antworten
            message = receiveMessage();
            if (message.equals("ready")) {
                sendMessage("ready");
                gameStarted = true;
                return new GameConfig(0, null, true, loadId);
            }
        }
        
        throw new IOException("Ungültiges Setup-Protokoll");
    }
    
    /**
     * Empfängt Schuss vom Server
     * @return Array mit [row, col] oder null wenn Server "save" gesendet hat
     */
    public int[] receiveShot() throws IOException {
        String message = receiveMessageWithSaveCheck();
        
        if (message == null) {
            // Server hat save gesendet
            return null;
        }
        
        if (message.startsWith("shot")) {
            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            return new int[]{row, col};
        }
        
        throw new IOException("Unerwartete Nachricht: " + message);
    }
    
    /**
     * Sendet Antwort auf Schuss an den Server
     * @param answerCode 0=Wasser, 1=Treffer, 2=Versenkt
     * @return true wenn erfolgreich, false wenn Server "save" gesendet hat
     */
    public boolean sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
        
        // Nach Antwort können wir pass oder save erwarten
        String response = receiveMessageWithSaveCheck();
        
        if (response == null) {
            // Server hat save gesendet
            return false;
        }
        
        // Erwarte pass (bei Wasser oder Versenkt) oder nichts (bei Treffer)
        // Diese Logik muss in Ihrer Spiel-Logik entschieden werden
        return true;
    }
    
    /**
     * Sendet Schuss an den Server
     * @param row Zeilenkoordinate (1-basiert)
     * @param col Spaltenkoordinate (1-basiert)
     * @return Antwortcode vom Server (0, 1, 2) oder -1 wenn Server "save" gesendet hat
     */
    public int sendShot(int row, int col) throws IOException {
        sendMessage("shot " + row + " " + col);
        
        String response = receiveMessageWithSaveCheck();
        if (response == null) {
            return -1; // Server hat save gesendet
        }
        
        if (response.startsWith("answer")) {
            String[] parts = response.split(" ");
            return Integer.parseInt(parts[1]);
        }
        throw new IOException("Unerwartete Antwort: " + response);
    }
    
    /**
     * Sendet Pass-Nachricht an den Server
     */
    public void sendPass() throws IOException {
        sendMessage("pass");
    }
    
    /**
     * Empfängt Pass-Nachricht vom Server
     * @return true wenn "pass" empfangen
     */
    public boolean receivePass() throws IOException {
        return receiveMessageWithSaveCheck().equals("pass");
    }
    
    /**
     * Sendet Save-Befehl an den Server
     * @param id Spielstand-ID
     * @return true wenn Server mit "ok" antwortet
     */
    public boolean sendSave(String id) throws IOException {
        if (!gameStarted) {
            throw new IllegalStateException("Save ist nur nach Spielstart erlaubt");
        }
        sendMessage("save " + id);
        return waitForResponse("ok");
    }
    
    /**
     * Empfängt Save-Befehl vom Server und antwortet mit ok
     * @return Spielstand-ID oder null
     */
    public String receiveSave() throws IOException {
        sendMessage("ok");
        // Save-ID wurde bereits in receiveMessageWithSaveCheck verarbeitet
        return lastSaveId;
    }
    
    /**
     * Empfängt eine Nachricht und prüft auf save (nur nach Spielstart)
     * @return Die Nachricht oder null wenn es ein save war
     */
    private String receiveMessageWithSaveCheck() throws IOException {
        String message = receiveMessage();
        
        if (message.startsWith("save")) {
            // Save während Spiel - ID speichern
            lastSaveId = message.substring(5).trim();
            // Automatisch ok antworten
            sendMessage("ok");
            return null; // Signalisiert, dass es ein save war
        }
        
        return message;
    }
    
    /**
     * Allgemeine Methode zum Warten auf eine bestimmte Antwort
     */
    private boolean waitForResponse(String expectedResponse) throws IOException {
        String response = receiveMessage();
        
        if (response.equals(expectedResponse)) {
            return true;
        }
        throw new IOException("Unerwartete Antwort: " + response);
    }
    
    /**
     * Allgemeine Methode zum Senden von Nachrichten
     */
    private void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }
    
    /**
     * Allgemeine Methode zum Empfangen von Nachrichten
     */
    private String receiveMessage() throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Verbindung verloren");
        }
        return line.trim();
    }
    
    /**
     * Schließt die Verbindung
     */
    public void close() throws IOException {
        if (socket != null) {
            socket.shutdownOutput();
            socket.close();
        }
        isConnected = false;
        gameStarted = false;
    }
    
    // Getter-Methoden
    public boolean isConnected() { return isConnected; }
    public boolean isGameStarted() { return gameStarted; }
    public String getLastSaveId() { return lastSaveId; }
    
    /**
     * Datenklasse für Spielkonfiguration
     */
    public static class GameConfig {
        public final int size;
        public final int[] ships;
        public final boolean isLoad;
        public final String loadId;
        
        public GameConfig(int size, int[] ships, boolean isLoad) {
            this(size, ships, isLoad, null);
        }
        
        public GameConfig(int size, int[] ships, boolean isLoad, String loadId) {
            this.size = size;
            this.ships = ships;
            this.isLoad = isLoad;
            this.loadId = loadId;
        }
    }
}
