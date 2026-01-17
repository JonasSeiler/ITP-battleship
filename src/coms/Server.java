import java.net.*;
import java.io.*;

public class Server {
    private final int PORT = 50000;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private Writer out;
    
    // Status-Variablen
    private boolean isConnected = false;
    private boolean gameStarted = false;
    
    /**
     * Startet den Server und wartet auf Client-Verbindung
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        clientSocket = serverSocket.accept();
        isConnected = true;
        
        // Streams initialisieren
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new OutputStreamWriter(clientSocket.getOutputStream());
    }
    
    /**
     * Sendet die Spielgröße an den Client
     * @param size Spielgröße (quadratisch)
     * @return true wenn Client mit "done" antwortet
     */
    public boolean sendSize(int size) throws IOException {
        sendMessage("size " + size);
        return receiveMessage().equals("done");
    }
    
    /**
     * Sendet die Schiffslängen an den Client
     * @param ships Array von Schiffslängen (absteigend sortiert)
     * @return true wenn Client mit "done" antwortet
     */
    public boolean sendShips(int[] ships) throws IOException {
        StringBuilder sb = new StringBuilder("ships");
        for (int ship : ships) {
            sb.append(" ").append(ship);
        }
        
        sendMessage(sb.toString());
        return receiveMessage().equals("done");
    }
    
    /**
     * Sendet Load-Befehl an den Client
     * @param id Spielstand-ID
     * @return true wenn Client mit "ok" antwortet
     */
    public boolean sendLoad(String id) throws IOException {
        sendMessage("load " + id);
        return receiveMessage().equals("ok");
    }
    
    /**
     * Sendet Ready-Nachricht an den Client
     * @return true wenn Client mit "ready" antwortet
     */
    public boolean sendReady() throws IOException {
        sendMessage("ready");
        boolean readyReceived = receiveMessage().equals("ready");
        if (readyReceived) {
            gameStarted = true;
        }
        return readyReceived;
    }
    
    /**
     * Sendet Schuss an den Client
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
     * Sendet Save-Befehl an den Client
     * @param id Spielstand-ID
     * @return true wenn Client mit "ok" antwortet
     */
    public boolean sendSave(String id) throws IOException {
        if (!gameStarted) {
            throw new IllegalStateException("Save ist nur nach Spielstart erlaubt");
        }
        sendMessage("save " + id);
        return receiveMessage().equals("ok");
    }
    
    /**
     * Sendet Pass-Nachricht an den Client
     */
    public void sendPass() throws IOException {
        sendMessage("pass");
    }
    
    /**
     * Empfängt eine Nachricht vom Client mit Save-Handling
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
     * Sendet Antwort auf Schuss an den Client
     * @param answerCode 0=Wasser, 1=Treffer, 2=Versenkt
     */
    public void sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
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
        if (clientSocket != null) {
            clientSocket.shutdownOutput();
            clientSocket.close();
        }
        if (serverSocket != null) {
            serverSocket.close();
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
}
