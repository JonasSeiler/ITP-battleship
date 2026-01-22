package src.coms;

import java.io.*;
import java.util.*;
import src.logic.*;

/**
 * Abstrakte Basisklasse für Server, Client und Bot
 * Enthält gemeinsame Funktionalität für die Netzwerkkommunikation
 */
public abstract class NetworkPlayer {
    protected boolean isConnected = false;
    protected boolean gameStarted = false;
    protected final int PORT = 50000;
    protected game logic; 
    /**
     * Allgemeine Methode zum Senden von Nachrichten
     */
    protected abstract void sendMessage(String message) throws IOException;
    
    /**
     * Allgemeine Methode zum Empfangen von Nachrichten
     */
    protected abstract String receiveMessage() throws IOException;
    
    /**
     * Empfängt eine Nachricht mit Save-Handling
     * Wartet auf: shot, pass oder save
     */
    public void receiveMessageWithSaveHandling() throws IOException {
        String message = receiveMessage();
        
        if (message.startsWith("shot")) {
            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[1]) - 1; // Konvertiere zu 0-basiert
            int col = Integer.parseInt(parts[2]) - 1;
            coordinate coord = new coordinate(row, col);
            logic.get_hit(coord);
        } 
        else if (message.startsWith("save")) {
            String id = message.substring(5).trim();
            // Sofort mit ok antworten
            logic.save_opp_game(id);
            sendMessage("ok");
        }
        else if (message.equals("pass")) {
            logic.start_local_turn();
        }
        
        throw new IOException("Unerwartete Nachricht: " + message);
    }
    
    /**
     * Sendet Antwort auf Schuss
     * @param answerCode 0=Wasser, 1=Treffer, 2=Versenkt
     */
    public void sendAnswer(int answerCode) throws IOException {
        sendMessage("answer " + answerCode);
    }
    
    /**
     * Sendet Schuss
     * @param row Zeilenkoordinate (1-basiert)
     * @param col Spaltenkoordinate (1-basiert)
     * @return Antwortcode (0=Wasser, 1=Treffer, 2=Versenkt)
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
     * Sendet Save-Befehl
     * @param id Spielstand-ID
     * @return true wenn mit "ok" geantwortet wird
     */
    public boolean sendSave(String id) throws IOException {
        if (!gameStarted) {
            throw new IllegalStateException("Save ist nur nach Spielstart erlaubt");
        }
        sendMessage("save " + id);
        return receiveMessage().equals("ok");
    }
    
    /**
     * Sendet Pass-Nachricht
     */
    public void sendPass() throws IOException {
        sendMessage("pass");
    }
    
    public void set_game(game g) {
        this.logic = g;
    }
    /**
     * Schließt die Verbindung
     */
    public abstract void close() throws IOException;
    
    // Abstrakte Methoden
    public abstract void start() throws IOException;
    
    // Getter-Methoden
    public boolean isConnected() { return isConnected; }
    public boolean isGameStarted() { return gameStarted; }
    
    
    /**
     * Datenklasse für Spielkonfiguration
     */
    public static class GameConfig {
        public final int size;
        public final int[] ships;
        public final boolean isLoad;
        public final String loadId;
        
        public GameConfig(int size, int[] ships, boolean isLoad) {
            this(size, ships, isLoad, null);
        }
        
        public GameConfig(int size, int[] ships, boolean isLoad, String loadId) {
            this.size = size;
            this.ships = ships;
            this.isLoad = isLoad;
            this.loadId = loadId;
        }
    }
}
