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


    public int size;
    public int[] ships;
    
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
        if(in == null) 
            throw new IOException("in not inited");


        String message = receivemessage();
        
        if (message.startsWith("size")) {
            String[] parts = message.split(" ");
            size = Integer.parseInt(parts[1]);
            
            sendmessage("done");

            message = receivemessage();

            if (message.startsWith("ships")) {
                String[] shipParts = message.split(" ");
                ships = new int[shipParts.length - 1];
                for (int i = 1; i < shipParts.length; i++) {
                    ships[i-1] = Integer.parseInt(shipParts[i]);
                }
                
                sendmessage("done");
                
            } else {
                throw new IOException("Erwartete 'ships', bekam: " + message);
            }
            
        } else if (message.startsWith("load")) {
            String[] parts = message.split(" ");
            String loadId = parts.length > 1 ? parts[1] : "";
            
            logic.load_game(loadId);
            
            sendmessage("ok");
            

        } else {
            throw new IOException("Ungültiges Setup-Protokoll: " + message);
        }

    }
    @Override    
    public boolean sendReady() {
        try {
            String message = receivemessage();
            if (message.equals("ready")) {
                sendmessage("ready");
                gameStarted = true;
            } else {
                throw new IOException("Erwartete 'ready', bekam: " + message);
            }
        } catch(Exception e) {
            System.err.println(e);
        }
        return gameStarted;
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
        System.out.println(message);
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
        System.out.println(line);
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
