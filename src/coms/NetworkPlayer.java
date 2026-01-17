import java.net.*;
import java.io.*;

/**
 * Abstrakte Basisklasse für Server und Client
 * Enthält gemeinsame Funktionalität für die Netzwerkkommunikation
 */
public abstract class NetworkPlayer {
    protected Socket socket;
    protected BufferedReader in;
    protected Writer out;
    
    // Status-Variabeln
    protected boolean isConnected = false;
    protected boolean gameStarted = false;
    protected final int PORT = 50000;
    
    /**
     * Allgemeine Methode zum Senden von Nachrichten
     */
    protected void sendMessage(String message) throws IOException {
        if (out == null) {
            throw new IOException("Verbindung nicht initialisiert");
        }
        out.write(message + "\n");
        out.flush();
    }
    
    /**
     * Allgemeine Methode zum Empfangen von Nachrichten
     */
    protected String receiveMessage() throws IOException {
        if (in == null) {
            throw new IOException("Verbindung nicht initialisiert");
        }
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Verbindung verloren");
        }
        return line.trim();
    }
    
    /**
     * Empfängt eine Nachricht mit Save-Handling
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
     * Sendet Antwort auf Schuss
     * @param answerCode 0=Wasser, 1=Treffer, 2=Versenkt
     */
    public void sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
    }
    
    /**
     * Sendet Schuss
     * @param row Zeilenkoordinate (1-basiert)
     * @param col Spaltenkoordinate (1-basiert)
     * @return Antwortcode (0=Wasser, 1=Treffer, 2=Versenkt)
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
     * Sendet Save-Befehl
     * @param id Spielstand-ID
     * @return true wenn mit "ok" geantwortet wird
     */
    public boolean sendSave(String id) throws IOException {
        if (!gameStarted) {
            throw new IllegalStateException("Save ist nur nach Spielstart erlaubt");
        }
        sendMessage("save " + id);
        return receiveMessage().equals("ok");
    }
    
    /**
     * Sendet Pass-Nachricht
     */
    public void sendPass() throws IOException {
        sendMessage("pass");
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
    
    // Abstrakte Methoden, die von Unterklassen implementiert werden müssen
    public abstract void start() throws IOException;
    
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
