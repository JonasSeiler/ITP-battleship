package src.coms;

import java.net.*;
import java.io.*;

/**
 * 
 */
public class Client extends NetworkPlayer {
    /**
     * 
     */
    private String serverAddress;
    /**
     * 
     */
    private Socket socket;
    /**
     * 
     */
    private BufferedReader in;
    /**
     * 
     */
    private Writer out;
    
    /**
     * 
     * @param address
     * @throws IOException
     */
    @Override
    public void start() throws IOException {
        socket = new Socket(serverAddress, PORT);
        isConnected = true;
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());
    }
    
    /**
     * 
     * @param address
     */
    public void setServerAddress(String address) {
        this.serverAddress = address;
    }
    
    /**
     * 
     * @throws IOException
     */
    public void receiveSetup() throws IOException {
        if (logic == null) {
            throw new IOException("Game-Objekt nicht gesetzt. set_game() muss vor receiveSetup() aufgerufen werden");
        }
        
        String message = receivemessage();
        
        if (message.startsWith("size")) {
            String[] parts = message.split(" ");
            int size = Integer.parseInt(parts[1]);
            
            sendmessage("done");
            
            message = receivemessage();
            if (message.startsWith("ships")) {
                String[] shipParts = message.split(" ");
                int[] ships = new int[shipParts.length - 1];
                for (int i = 1; i < shipParts.length; i++) {
                    ships[i-1] = Integer.parseInt(shipParts[i]);
                }
                
                sendmessage("done");
                
                message = receivemessage();
                if (message.equals("ready")) {
                    sendmessage("ready");
                    gameStarted = true;
                } else {
                    throw new IOException("Erwartete 'ready', bekam: " + message);
                }
            } else {
                throw new IOException("Erwartete 'ships', bekam: " + message);
            }
            
        } else if (message.startsWith("load")) {
            String[] parts = message.split(" ");
            String loadId = parts.length > 1 ? parts[1] : "";
            
            logic.load_game(loadId);
            
            sendmessage("ok");
            
            message = receivemessage();
            if (message.equals("ready")) {
                sendmessage("ready");
                gameStarted = true;
            } else {
                throw new IOException("Erwartete 'ready', bekam: " + message);
            }
        } else {
            throw new IOException("Ungültiges Setup-Protokoll: " + message);
        }
    }
    
    /**
     * 
     * @param message
     * @throws IOException
     */
    @Override
    protected void sendmessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
    @Override
    protected String receivemessage() throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Verbindung verloren");
        }
        return line.trim();
    }
    
    /**
     * 
     * @throws IOException
     */
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