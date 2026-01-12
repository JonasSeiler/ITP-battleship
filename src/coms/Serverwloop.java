import java.net.*;
import java.io.*;

class Serverwloop {
    final static int PORT = 50000;
    
    public static void main(String[] args) {
        ServerSocket ss = null;
        Socket s = null;
        BufferedReader usr = new BufferedReader(new InputStreamReader(System.in));

        try {
            ss = new ServerSocket(PORT);
            System.out.println("Server gestartet auf Port " + PORT);
            System.out.println("Warte auf Client-Verbindung...");
            s = ss.accept();
            System.out.println("Verbindung hergestellt.");

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Writer out = new OutputStreamWriter(s.getOutputStream());

            // --- SETUP PHASE ---
            System.out.println("\n=== SETUP PHASE ===");
            
            boolean setupComplete = false;
            while (!setupComplete) {
                System.out.print("\n[SERVER SETUP] Eingabe ('size rows' oder 'load id'): ");
                String input = usr.readLine();
                
                if (input == null || input.trim().isEmpty()) continue;
                
                String[] parts = input.split(" ");
                String command = parts[0].toLowerCase();
                
                if (command.equals("size") && parts.length == 2) {
                    // Option 1: size
                    send(out, input);
                    System.out.println("Gesendet: " + input);
                    
                    // Warte auf done vom Client
                    String response = receive(in);
                    if (response.equals("done")) {
                        System.out.println("Empfangen: " + response);
                        
                        // Eingabe der Schiffe
                        System.out.print("\n[SERVER SETUP] Schiffe eingeben (absteigend, z.B. '5 4 3'): ");
                        String shipsInput = usr.readLine();
                        String shipsMessage = "ships " + shipsInput;
                        send(out, shipsMessage);
                        System.out.println("Gesendet: " + shipsMessage);
                        
                        // Warte auf done vom Client
                        response = receive(in);
                        if (response.equals("done")) {
                            System.out.println("Empfangen: " + response);
                            
                            // Sende ready
                            send(out, "ready");
                            System.out.println("Gesendet: ready");
                            
                            // Empfange ready vom Client
                            response = receive(in);
                            if (response.equals("ready")) {
                                System.out.println("Empfangen: " + response);
                                setupComplete = true;
                            }
                        }
                    }
                    
                } else if (command.equals("load") && parts.length == 2) {
                    // Option 2: load
                    send(out, input);
                    System.out.println("Gesendet: " + input);
                    
                    // Warte auf ok vom Client
                    String response = receive(in);
                    if (response.equals("ok")) {
                        System.out.println("Empfangen: " + response);
                        
                        // Sende ready
                        send(out, "ready");
                        System.out.println("Gesendet: ready");
                        
                        // Empfange ready vom Client
                        response = receive(in);
                        if (response.equals("ready")) {
                            System.out.println("Empfangen: " + response);
                            setupComplete = true;
                        }
                    }
                } else {
                    System.out.println("Ungültige Eingabe. Format: 'size 10' oder 'load 123456'");
                }
            }

            // --- GAME LOOP ---
            System.out.println("\n=== SPIEL BEGINNT ===");
            
            boolean gameActive = true;
            boolean myTurn = true; // Server beginnt
            
            while (gameActive) {
                if (myTurn) {
                    // Server ist am Zug
                    System.out.print("\n[SERVER ZUG] Eingabe ('shot x y' oder 'save id'): ");
                    String input = usr.readLine();
                    
                    if (input == null || input.trim().isEmpty()) continue;
                    
                    String[] parts = input.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot") && parts.length == 3) {
                        // Schuss senden
                        send(out, input);
                        System.out.println("Gesendet: " + input);
                        
                        // Warte auf answer vom Client
                        String answer = receive(in);
                        System.out.println("Empfangen: " + answer);
                        
                        if (answer.startsWith("answer")) {
                            String[] answerParts = answer.split(" ");
                            int result = Integer.parseInt(answerParts[1]);
                            
                            if (result == 0) {
                                // Bei Wasser: pass senden
                                System.out.print("\n[SERVER ZUG] Eingabe ('pass'): ");
                                String passInput = usr.readLine();
                                while (!passInput.equals("pass")) {
                                    System.out.print("Ungültig! Bitte 'pass' eingeben: ");
                                    passInput = usr.readLine();
                                }
                                send(out, passInput);
                                System.out.println("Gesendet: " + passInput);
                                myTurn = false; // Client ist jetzt am Zug
                            } else if (result == 1 || result == 2) {
                                // Bei Treffer oder Versenkt: Server bleibt am Zug
                                // (keine Aktion nötig)
                            }
                        }
                        
                    } else if (command.equals("save") && parts.length == 2) {
                        // Save senden
                        send(out, input);
                        System.out.println("Gesendet: " + input);
                        
                        // Warte auf ok vom Client
                        String response = receive(in);
                        if (response.equals("ok")) {
                            System.out.println("Empfangen: " + response);
                            gameActive = false;
                        }
                    } else {
                        System.out.println("Ungültiges Kommando. Erwartet: 'shot x y' oder 'save id'");
                    }
                    
                } else {
                    // Client ist am Zug
                    System.out.println("\n[WARTE AUF CLIENT] Client ist am Zug...");
                    
                    String clientMessage = receive(in);
                    System.out.println("Empfangen: " + clientMessage);
                    
                    String[] parts = clientMessage.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot")) {
                        // Client hat geschossen
                        System.out.print("\n[SERVER ANTWORT] Eingabe ('answer 0', 'answer 1' oder 'answer 2'): ");
                        String answerInput = usr.readLine();
                        
                        // Validierung der Eingabe
                        while (!answerInput.equals("answer 0") && 
                               !answerInput.equals("answer 1") && 
                               !answerInput.equals("answer 2")) {
                            System.out.print("Ungültig! Bitte 'answer 0', 'answer 1' oder 'answer 2' eingeben: ");
                            answerInput = usr.readLine();
                        }
                        
                        send(out, answerInput);
                        System.out.println("Gesendet: " + answerInput);
                        
                        if (answerInput.equals("answer 0")) {
                            // Warte auf pass vom Client
                            String passMsg = receive(in);
                            if (passMsg.equals("pass")) {
                                System.out.println("Empfangen: " + passMsg);
                                myTurn = true; // Server ist jetzt am Zug
                            }
                        } else {
                            // Bei Treffer oder Versenkt: Client bleibt am Zug
                            // (keine Aktion nötig)
                        }
                        
                    } else if (command.equals("save")) {
                        // Client möchte speichern
                        System.out.print("\n[SERVER ANTWORT] Eingabe ('ok'): ");
                        String okInput = usr.readLine();
                        while (!okInput.equals("ok")) {
                            System.out.print("Ungültig! Bitte 'ok' eingeben: ");
                            okInput = usr.readLine();
                        }
                        send(out, okInput);
                        System.out.println("Gesendet: " + okInput);
                        gameActive = false;
                        
                    } else if (command.equals("pass")) {
                        // Client gibt Zug ab
                        myTurn = true;
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
                if (ss != null) ss.close();
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
