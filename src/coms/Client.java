package src.coms;

import java.net.*;
import java.io.*;

/**
 * Client-Implementierung
 */
public class Client extends NetworkPlayer {
    private String serverAddress;
    private Socket socket;
    private BufferedReader in;
    private Writer out;
    
    /**
     * Stellt Verbindung zum Server her
     * @param address Server-IP-Adresse
     */
    @Override
    public void start() throws IOException {
        socket = new Socket(serverAddress, PORT);
        isConnected = true;
        
        // Streams initialisieren
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());
    }
    
    /**
     * Setzt die Server-Adresse (muss vor start() aufgerufen werden)
     */
    public void setServerAddress(String address) {
        this.serverAddress = address;
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
    
    @Override
    protected void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }
    
    @Override
    protected String receiveMessage() throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Verbindung verloren");
        }
        return line.trim();
    }
    
    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.shutdownOutput();
            socket.close();
        }
        isConnected = false;
        gameStarted = false;
    }
}
