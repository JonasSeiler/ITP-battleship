import java.net.*;
import java.io.*;

/**
 * Server-Implementierung
 */
public class Server extends NetworkPlayer {
    private ServerSocket serverSocket;
    
    /**
     * Startet den Server und wartet auf Client-Verbindung
     */
    @Override
    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        socket = serverSocket.accept();
        isConnected = true;
        
        // Streams initialisieren
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());
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
     * Schließt die Verbindung (erweitert für ServerSocket)
     */
    @Override
    public void close() throws IOException {
        super.close();
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
