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
        return waitForResponse("done");
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
        return waitForResponse("done");
    }
    
    /**
     * Sendet Load-Befehl an den Client
     * @param id Spielstand-ID
     * @return true wenn Client mit "ok" antwortet
     */
    public boolean sendLoad(String id) throws IOException {
        sendMessage("load " + id);
        return waitForResponse("ok");
    }
    
    /**
     * Sendet Ready-Nachricht an den Client
     * @return true wenn Client mit "ready" antwortet
     */
    public boolean sendReady() throws IOException {
        sendMessage("ready");
        boolean readyReceived = waitForResponse("ready");
        if (readyReceived) {
            gameStarted = true;
        }
        return readyReceived;
    }
    
    /**
     * Sendet Schuss an den Client
     * @param row Zeilenkoordinate (1-basiert)
     * @param col Spaltenkoordinate (1-basiert)
     * @return Antwortcode (0=Wasser, 1=Treffer, 2=Versenkt) oder -1 wenn Client "save" gesendet hat
     */
    public int sendShot(int row, int col) throws IOException {
        sendMessage("shot " + row + " " + col);
        String response = receiveMessageWithSaveCheck();
        
        if (response == null) {
            // Client hat save gesendet
            return -1;
        }
        
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
        return waitForResponse("ok");
    }
    
    /**
     * Sendet Pass-Nachricht an den Client
     */
    public void sendPass() throws IOException {
        sendMessage("pass");
    }
    
    /**
     * Empfängt Schuss vom Client
     * @return Array mit [row, col] oder null wenn Client "save" gesendet hat
     */
    public int[] receiveShot() throws IOException {
        String message = receiveMessageWithSaveCheck();
        
        if (message == null) {
            // Client hat save gesendet
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
     * Sendet Antwort auf Schuss an den Client
     * @param answerCode 0=Wasser, 1=Treffer, 2=Versenkt
     * @return true wenn Client "pass" gesendet hat, false wenn Client "save" gesendet hat
     */
    public boolean sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
        String response = receiveMessageWithSaveCheck();
        
        if (response == null) {
            // Client hat save gesendet
            return false;
        }
        
        if (response.equals("pass")) {
            return true; // Client hat pass gesendet
        }
        throw new IOException("Unerwartete Antwort: " + response);
    }
    
    /**
     * Allgemeine Methode zum Warten auf eine bestimmte Antwort (ohne Save-Check)
     */
    private boolean waitForResponse(String expectedResponse) throws IOException {
        String response = receiveMessage();
        
        if (response.equals(expectedResponse)) {
            return true;
        }
        throw new IOException("Unerwartete Antwort: " + response + " (erwartet: " + expectedResponse + ")");
    }
    
    /**
     * Empfängt eine Nachricht und prüft auf save (nur nach Spielstart)
     * @return Die Nachricht oder null wenn es ein save war
     */
    private String receiveMessageWithSaveCheck() throws IOException {
        String message = receiveMessage();
        
        if (message.startsWith("save")) {
            // Save während Spiel - mit ok antworten
            sendMessage("ok");
            return null; // Signalisiert, dass es ein save war
        }
        
        return message;
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
}
