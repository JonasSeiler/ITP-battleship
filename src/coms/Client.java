import java.net.*;
import java.io.*;
import java.util.Arrays;

class Client {
    final static int PORT = 50000;
    private static boolean gameStarted = false;
    private static boolean myTurn = false; // Client beginnt nicht
    private static int boardSize = 0;
    private static String[] ships = null;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Verwendung: java Client <server-ip>");
            return;
        }

        Socket s = null;
        BufferedReader usr = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Verbinde mit Server: " + args[0] + ":" + PORT);
            s = new Socket(args[0], PORT);
            System.out.println("Verbindung hergestellt.");

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Writer out = new OutputStreamWriter(s.getOutputStream());

            // --- SETUP PHASE ---
            System.out.println("\n=== SETUP PHASE ===");
            
            boolean setupComplete = false;
            while (!setupComplete) {
                // Erste Nachricht vom Server erhalten
                String serverMessage = receive(in);
                System.out.println("Empfangen: " + serverMessage);
                
                String[] parts = serverMessage.split(" ");
                String command = parts[0].toLowerCase();
                
                if (command.equals("size")) {
                    // Größe speichern
                    boardSize = Integer.parseInt(parts[1]);
                    System.out.println("Spielgröße: " + boardSize + "x" + boardSize);
                    
                    // done senden
                    send(out, "done");
                    System.out.println("Gesendet: done");
                    
                    // Schiffe empfangen
                    String shipsMessage = receive(in);
                    System.out.println("Empfangen: " + shipsMessage);
                    
                    String[] shipsParts = shipsMessage.split(" ");
                    if (shipsParts[0].equals("ships")) {
                        ships = Arrays.copyOfRange(shipsParts, 1, shipsParts.length);
                        System.out.println("Schiffslängen: " + Arrays.toString(ships));
                        
                        // done senden
                        send(out, "done");
                        System.out.println("Gesendet: done");
                        
                        // ready empfangen
                        String readyMsg = receive(in);
                        if (readyMsg.equals("ready")) {
                            // ready zurücksenden
                            send(out, "ready");
                            System.out.println("Gesendet: ready");
                            
                            setupComplete = true;
                            gameStarted = true;
                            System.out.println("\n=== SPIEL BEGINNT ===");
                            System.out.println("Warte auf ersten Zug des Servers...");
                        }
                    }
                    
                } else if (command.equals("load")) {
                    // Spiel laden
                    System.out.println("Lade Spiel mit ID: " + parts[1]);
                    
                    // ok senden
                    send(out, "ok");
                    System.out.println("Gesendet: ok");
                    
                    setupComplete = true;
                    gameStarted = true;
                    System.out.println("\n=== SPIEL GELADEN ===");
                    System.out.println("Warte auf ersten Zug des Servers...");
                }
            }

            // --- GAME LOOP ---
            while (gameStarted) {
                if (!myTurn) {
                    // Server ist am Zug
                    System.out.println("\n[WARTE AUF SERVER] Server ist am Zug...");
                    String serverMessage = receive(in);
                    System.out.println("Empfangen: " + serverMessage);
                    
                    String[] parts = serverMessage.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot")) {
                        // Server hat geschossen
                        System.out.println("Server schießt auf: Reihe " + parts[1] + ", Spalte " + parts[2]);
                        
                        // Antwort eingeben
                        System.out.print("[CLIENT ANTWORT] Antwort eingeben (0=Wasser, 1=Treffer, 2=Versenkt): ");
                        String answerInput = usr.readLine();
                        
                        // Validierung der Eingabe
                        while (!answerInput.equals("0") && !answerInput.equals("1") && !answerInput.equals("2")) {
                            System.out.print("Ungültig! Bitte 0, 1 oder 2 eingeben: ");
                            answerInput = usr.readLine();
                        }
                        
                        send(out, "answer " + answerInput);
                        System.out.println("Gesendet: answer " + answerInput);
                        
                        int result = Integer.parseInt(answerInput);
                        if (result == 0 || result == 2) {
                            // Bei Wasser oder Versenkt: Server beendet seinen Zug
                            System.out.println("Server ist fertig mit diesem Zug.");
                            // Client wird am Zug
                            myTurn = true;
                        } else if (result == 1) {
                            System.out.println("Server darf nochmal schießen (Treffer).");
                            // Server bleibt am Zug (myTurn bleibt false)
                        }
                        
                    } else if (command.equals("save")) {
                        // Server möchte speichern
                        System.out.println("Server möchte speichern: " + serverMessage);
                        send(out, "ok");
                        System.out.println("Gesendet: ok");
                        gameStarted = false;
                    } else {
                        System.out.println("Unerwartete Nachricht vom Server: " + serverMessage);
                    }
                    
                } else {
                    // Client ist am Zug
                    System.out.print("\n[CLIENT ZUG] Eingabe ('shot row col'): ");
                    String input = usr.readLine();
                    
                    if (input == null || input.trim().isEmpty()) continue;
                    
                    String[] parts = input.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot") && parts.length == 3) {
                        // Schuss senden
                        send(out, input);
                        System.out.println("Gesendet: " + input);
                        
                        // Antwort vom Server erhalten
                        String answer = receive(in);
                        System.out.println("Empfangen: " + answer);
                        
                        if (answer.startsWith("answer")) {
                            String[] answerParts = answer.split(" ");
                            int result = Integer.parseInt(answerParts[1]);
                            
                            if (result == 0) {
                                System.out.println("Antwort: WASSER (0)");
                                System.out.println("Client ist fertig mit diesem Zug.");
                                myTurn = false; // Server ist jetzt am Zug
                            } else if (result == 1) {
                                System.out.println("Antwort: TREFFER (1)");
                                System.out.println("Client darf nochmal schießen!");
                                // Client bleibt am Zug (myTurn bleibt true)
                            } else if (result == 2) {
                                System.out.println("Antwort: VERSENKT (2)");
                                System.out.println("Client ist fertig mit diesem Zug.");
                                myTurn = false; // Server ist jetzt am Zug
                            }
                        }
                    }
                }
            }

            // Verbindung schließen
            s.shutdownOutput();
            System.out.println("\nVerbindung geschlossen.");
            
        } catch (IOException e) {
            System.err.println("Fehler: " + e.getMessage());
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e) {
                System.err.println("Fehler beim Schließen: " + e.getMessage());
            }
        }
    }
    
    private static void send(Writer out, String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }
    
    private static String receive(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Verbindung verloren.");
        }
        return line.trim();
    }
}
