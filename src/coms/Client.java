package src.coms;

import java.net.*;
import java.io.*;

/**
 * Client is a subclass of NetworkPlayer that is used to act as the Client of the Network
 * connection
 * @author Jamie Kopp
 */
public class Client extends NetworkPlayer {
    /**
     * the ip address of the host
     */
    private String serverAddress;
    /**
     * socket object for communication
     */
    private Socket socket;
    /**
     * object to read from buffer
     */
    private BufferedReader in;
    /**
     * object to write to buffer
     */
    private Writer out;


    public int size;
    public int[] ships;
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        socket = new Socket(serverAddress, PORT);
        isConnected = true;
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());
    }
    
    /**
     * setter method to set the server address
     * 
     * @param ip address from Host
     * @author Jamie Kopp
     */
    public void setServerAddress(String address) {
        this.serverAddress = address;
    }
    
    /**
     * method for receiving the game setup from a host
     *
     * @throws IOException
     * @author Jamie Kopp
     */
    public boolean receiveSetup() throws IOException {
        if(in == null) 
            throw new IOException("in not inited");


        String message = receiveMessage();
        
        if (message.startsWith("size")) {
            String[] parts = message.split(" ");
            size = Integer.parseInt(parts[1]);
            
            sendMessage("done");

            message = receiveMessage();

            if (message.startsWith("ships")) {
                String[] shipParts = message.split(" ");
                ships = new int[shipParts.length - 1];
                for (int i = 1; i < shipParts.length; i++) {
                    ships[i-1] = Integer.parseInt(shipParts[i]);
                }
                
                sendMessage("done");
                
            } else {
                throw new IOException("Erwartete 'ships', bekam: " + message);
            }
            
        } else if (message.startsWith("load")) {
            String[] parts = message.split(" ");
            String loadId = parts.length > 1 ? parts[1] : "";
            
            logic.load_game(loadId);
            
            sendMessage("ok");
            return true;

        } else {
            throw new IOException("Ungültiges Setup-Protokoll: " + message);
        }
        return false;
    }

    /** 
     * {@inheritDoc}
     */
    @Override    
    public boolean sendReady() {
        try {
            String message = receiveMessage();
            if (message.equals("ready")) {
                sendMessage("ready");
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
     * {@inheritDoc}
     */
    @Override
    protected void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println(message);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String receiveMessage() throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Verbindung verloren");
        }
        System.out.println(line);
        return line.trim();
    }
    
    /**
     * {@inheritDoc}
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
