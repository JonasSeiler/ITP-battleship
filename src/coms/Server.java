package src.coms;

import java.net.*;
import java.io.*;

/**
 * Server is a subclass of NetworkPlayer that is used to act as the host of the Network
 * connection
 * @author Jamie Kopp
 */
public class Server extends NetworkPlayer {
    /**
     * a serverSocket object for communication
     */
    private ServerSocket serverSocket;
    /**
     * a normal socket object for the client side
     */
    private Socket clientSocket;
    /**
     * object to read from buffer
     */
    private BufferedReader in;
    /**
     * object to write to buffer
     */
    private Writer out;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        clientSocket = serverSocket.accept();
        isConnected = true;
        
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new OutputStreamWriter(clientSocket.getOutputStream());
    }
    
    /**
     * method to send the board size
     *
     * @param size of the board
     * @return true if enemy recieved size succefully
     * @throws IOException
     * @author Jamie Kopp
     */
    public boolean sendSize(int size) throws IOException {
        sendMessage("size " + size);
        return receiveMessage().equals("done");
    }
    
    /**
     * method to send the the ship array
     *
     * @param ships array containing ship size
     * @return true if enemy recieved size succefully
     * @throws IOException
     * @author Jamie Kopp
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
     * method to send the id to the enemy to load a game file
     *
     * @param id to load
     * @return true if enemy succefully loaded the game id
     * @throws IOException
     * @author Jamie Kopp
     */
    public boolean sendLoad(String id) throws IOException {
        sendMessage("load " + id);
        return receiveMessage().equals("ok");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendReady() throws IOException {
        sendMessage("ready");
        boolean readyReceived = receiveMessage().equals("ready");
        if (readyReceived) {
            gameStarted = true;
        }
        return readyReceived;
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
}
