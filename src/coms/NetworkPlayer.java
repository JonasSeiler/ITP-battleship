package src.coms;

import java.io.*;
import java.util.*;
import src.logic.*;

/**
 * NetworkPlayer is an abstract upper class for the network communication
 * @author Jamie Kopp
 */
public abstract class NetworkPlayer {
    /**
     * variable to see if communication is established
     */
    protected boolean isConnected = false;
    /**
     * variable to see if game is started
     */
    protected boolean gameStarted = false;
    /**
     * the port used for network connection
     */
    protected final int PORT = 50000;
    /**
     * a Game object to use game logic related functions
     */
    protected Game logic; 
    /**
     * a method to send messages over the network
     *
     * @param message the string to be send
     * @throws IOException 
     * @author Jamie Kopp
     */
    protected abstract void sendMessage(String message) throws IOException;
    
    /**
     * a method to recieve messages over the network
     *
     * @return the string recieved
     * @throws IOException
     * @author Jamie Kopp
     */
    protected abstract String receiveMessage() throws IOException;
    
    /**
     * a method to send the ready message
     *
     * @return true if ready recieved
     * @throws IOException
     * @author Jamie Kopp
     */
    public abstract boolean sendReady() throws IOException;

    /**
     * a method to recieve a message when save can also be send by opponent
     *
     * @throws IOException
     * @author Jamie Kopp
     */
    public void receiveMessagewsave() throws IOException {
        String message = receiveMessage();
        if (message.startsWith("shot")) {
            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            Coordinate coord = new Coordinate(row, col);
            logic.get_hit(coord);
        } 
        else if (message.startsWith("save")) {
            String id = message.substring(5).trim();
            logic.save_opp_game(id);
            sendMessage("ok");
        }
        else if (message.equals("pass")) {
            logic.start_local_turn();
        }
        else 
            throw new IOException("Unerwartete Nachricht: " + message);
    }
    
    /**
     * a method to send the answer to the enemy shot
     *
     * @param answerCode 
     * @throws IOException
     * @author Jamie Kopp
     */
    public void sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
        if(answerCode == 0)
            readPass();
    }
    
    /**
     * method to read pass message
     *
     * @throws IOException
     * @author Jamie Kopp
     */
    public boolean readPass() throws IOException {
        String message = receiveMessage();
        if(message.startsWith("pass")) {
            return true;
        }
        return false;
    }
    /**
     * method to send the shot and recieve the answer
     *
     * @param row 
     * @param col
     * @return the answer from the shot send by enemy
     * @throws IOException
     * @author Jamie Kopp
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
     * method to prompt enemy to save the current game
     *
     * @param id
     * @return true if enemy send back "ok"
     * @throws IOException
     * @throws IllegalStateException
     * @author Jamie Kopp
     */
    public boolean sendSave(String id) throws IOException {
        if (!gameStarted) {
            throw new IllegalStateException("Save ist nur nach Spielstart erlaubt");
        }
        sendMessage("save " + id);
        return receiveMessage().equals("ok");
    }
    
    /**
     * method to send pass
     *
     * @throws IOException
     * @author Jamie Kopp
     */
    public void sendPass() throws IOException {
            sendMessage("pass");
    }
    
    /**
     * initiliazing the game logic object
     *
     * @param g is a game object
     * @author Jamie Kopp
     */
    public void set_game(Game g) {
        this.logic = g;
    }
    
    /**
     * method to close the connection and reset variables
     *
     * @throws IOException
     * @author Jamie Kopp
     */
    public abstract void close() throws IOException;
    
    /**
     * method to start up the connection and set variables
     *
     * @throws IOException
     * @author Jamie Kopp
     */
    public abstract void start() throws IOException;
    
    /**
     * method to check if connection is established
     *
     * @return
     * @author Jamie Kopp
     */
    public boolean isConnected() { return isConnected; }
    
    /**
     * method to check if game has started
     *
     * @return
     * @author Jamie Kopp
     */
    public boolean isGameStarted() { return gameStarted; }
    
}
