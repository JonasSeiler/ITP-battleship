package src.coms;

import java.io.*;
import java.util.*;
import src.logic.*;

/**
 * 
 */
public abstract class NetworkPlayer {
    /**
     * 
     */
    protected boolean isConnected = false;
    /**
     * 
     */
    protected boolean gameStarted = false;
    /**
     * 
     */
    protected final int PORT = 50000;
    /**
     * 
     */
    protected game logic; 
    /**
     * 
     * @param message
     * @throws IOException
     */
    protected abstract void sendmessage(String message) throws IOException;
    
    /**
     * 
     * @return
     * @throws IOException
     */
    protected abstract String receivemessage() throws IOException;
    
    /**
     * 
     * @throws IOException
     */
    public void receivemessagewsave() throws IOException {
        String message = receivemessage();
        
        if (message.startsWith("shot")) {
            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[1]) - 1;
            int col = Integer.parseInt(parts[2]) - 1;
            coordinate coord = new coordinate(row, col);
            logic.get_hit(coord);
        } 
        else if (message.startsWith("save")) {
            String id = message.substring(5).trim();
            logic.save_opp_game(id);
            sendmessage("ok");
        }
        else if (message.equals("pass")) {
            logic.start_local_turn();
        }
        
        throw new IOException("Unerwartete Nachricht: " + message);
    }
    
    /**
     * 
     * @param answerCode
     * @throws IOException
     */
    public void sendAnswer(int answerCode) throws IOException {
        sendmessage("answer " + answerCode);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @return
     * @throws IOException
     */
    public int sendShot(int row, int col) throws IOException {
        sendmessage("shot " + row + " " + col);
        String response = receivemessage();
        
        if (response.startsWith("answer")) {
            String[] parts = response.split(" ");
            return Integer.parseInt(parts[1]);
        }
        throw new IOException("Unerwartete Antwort: " + response);
    }
    
    /**
     * 
     * @param id
     * @return
     * @throws IOException
     * @throws IllegalStateException
     */
    public boolean sendSave(String id) throws IOException {
        if (!gameStarted) {
            throw new IllegalStateException("Save ist nur nach Spielstart erlaubt");
        }
        sendmessage("save " + id);
        return receivemessage().equals("ok");
    }
    
    /**
     * 
     * @throws IOException
     */
    public void sendPass() throws IOException {
        sendmessage("pass");
    }
    
    /**
     * 
     * @param g
     */
    public void set_game(game g) {
        this.logic = g;
    }
    
    /**
     * 
     * @throws IOException
     */
    public abstract void close() throws IOException;
    
    /**
     * 
     * @throws IOException
     */
    public abstract void start() throws IOException;
    
    /**
     * 
     * @return
     */
    public boolean isConnected() { return isConnected; }
    
    /**
     * 
     * @return
     */
    public boolean isGameStarted() { return gameStarted; }
    
}