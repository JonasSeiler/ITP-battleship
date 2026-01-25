package src.coms;

import java.net.*;
import java.io.*;

/**
 * 
 */
public class Server extends NetworkPlayer {
    /**
     * 
     */
    private ServerSocket serverSocket;
    /**
     * 
     */
    private Socket clientSocket;
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
     * @throws IOException
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
     * 
     * @param size
     * @return
     * @throws IOException
     */
    public boolean sendSize(int size) throws IOException {
        sendmessage("size " + size);
        return receivemessage().equals("done");
    }
    
    /**
     * 
     * @param ships
     * @return
     * @throws IOException
     */
    public boolean sendShips(int[] ships) throws IOException {
        StringBuilder sb = new StringBuilder("ships");
        for (int ship : ships) {
            sb.append(" ").append(ship);
        }
        
        sendmessage(sb.toString());
        return receivemessage().equals("done");
    }
    
    /**
     * 
     * @param id
     * @return
     * @throws IOException
     */
    public boolean sendLoad(String id) throws IOException {
        sendmessage("load " + id);
        return receivemessage().equals("ok");
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
    @Override
    public boolean sendReady() throws IOException {
        sendmessage("ready");
        boolean readyReceived = receivemessage().equals("ready");
        if (readyReceived) {
            gameStarted = true;
        }
        return readyReceived;
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
