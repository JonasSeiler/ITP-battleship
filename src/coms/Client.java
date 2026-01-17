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
     * Empfängt eine Nachricht vom Server mit Save-Handling
     * Wartet auf: shot, pass oder save
     * @return MessageType mit Typ und Inhalt
     */
    public MessageType receiveMessageWithSaveHandling() throws IOException {
        String message = receiveMessage();
        
        if (message.startsWith("shot")) {
            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            return new MessageType(MessageType.Type.SHOT, new int[]{row, col});
        } 
        else if (message.startsWith("save")) {
            String id = message.substring(5).trim();
            // Sofort mit ok antworten
            sendMessage("ok");
            return new MessageType(MessageType.Type.SAVE, id);
        }
        else if (message.equals("pass")) {
            return new MessageType(MessageType.Type.PASS, null);
        }
        
        throw new IOException("Unerwartete Nachricht: " + message);
    }
    
    /**
     * Sendet Antwort auf Schuss an den Server
     * @param answerCode 0=Wasser, 1=Treffer, 2=Versenkt
     */
    public void sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
    }
    
    /**
     * Sendet Schuss an den Server
     * @param row Zeilenkoordinate (1-basiert)
     * @param col Spaltenkoordinate (1-basiert)
     * @return Antwortcode vom Server (0, 1, oder 2)
     */
    public int sendShot(int row, int col) throws IOException {
        sendMessage("shot " + row + " " + col);
        String response = receiveMessage();
        
        if (response.startsWith("answer")) {
            String[] parts = response.split(" ");
            return Integer.parseInt(parts[1]);
        }
        throw new IOException("Unerwartete Antwort: " + response);
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
        return receiveMessage().equals("ok");
    }
    
    /**
     * Sendet Pass-Nachricht an den Server
     */
    public void sendPass() throws IOException {
        sendMessage("pass");
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
    
    /**
     * Hilfsklasse für Nachrichtentypen
     */
    public static class MessageType {
        public enum Type { SHOT, SAVE, PASS }
        
        public final Type type;
        public final Object data;
        
        public MessageType(Type type, Object data) {
            this.type = type;
            this.data = data;
        }
        
        public boolean isShot() { return type == Type.SHOT; }
        public boolean isSave() { return type == Type.SAVE; }
        public boolean isPass() { return type == Type.PASS; }
        
        public int[] getShotCoords() { return (int[]) data; }
        public String getSaveId() { return (String) data; }
    }
    
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
