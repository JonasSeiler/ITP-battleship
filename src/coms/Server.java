import java.net.*;
import java.io.*;
import java.util.Arrays;

class Server {
    final static int PORT = 50000;
    private static boolean gameStarted = false;
    private static boolean myTurn = true; // Server beginnt

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
                    // Neue Spielgröße
                    send(out, "size " + parts[1]);
                    System.out.println("Gesendet: size " + parts[1]);
                    
                    // Warte auf done vom Client
                    String response = receive(in);
                    if (response.equals("done")) {
                        // Schiffe eingeben
                        System.out.print("[SERVER SETUP] Schiffe eingeben (Längen absteigend, z.B. '5 4 3'): ");
                        String shipsInput = usr.readLine();
                        send(out, "ships " + shipsInput);
                        System.out.println("Gesendet: ships " + shipsInput);
                        
                        // Warte auf done vom Client
                        response = receive(in);
                        if (response.equals("done")) {
                            // Beide ready senden
                            send(out, "ready");
                            System.out.println("Gesendet: ready");
                            
                            response = receive(in);
                            if (response.equals("ready")) {
                                setupComplete = true;
                                gameStarted = true;
                                System.out.println("\n=== SPIEL BEGINNT ===");
                                System.out.println("Server ist am Zug (beginnt).");
                            }
                        }
                    }
                    
                } else if (command.equals("load") && parts.length == 2) {
                    // Geladenes Spiel
                    send(out, input);
                    System.out.println("Gesendet: " + input);
                    
                    // Warte auf ok vom Client
                    String response = receive(in);
                    if (response.equals("ok")) {
                        setupComplete = true;
                        gameStarted = true;
                        System.out.println("\n=== SPIEL GELADEN ===");
                        System.out.println("Server ist am Zug (beginnt).");
                    }
                } else {
                    System.out.println("Ungültige Eingabe. Format: 'size 10' oder 'load 123456'");
                }
            }

            // --- GAME LOOP ---
            while (gameStarted) {
                if (myTurn) {
                    System.out.print("\n[SERVER ZUG] Eingabe ('shot row col' oder 'options'): ");
                    String input = usr.readLine();
                    
                    if (input == null || input.trim().isEmpty()) continue;
                    
                    String[] parts = input.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot") && parts.length == 3) {
                        // Schuss senden
                        send(out, input);
                        System.out.println("Gesendet: " + input);
                        
                        // Antwort vom Client erhalten
                        String answer = receive(in);
                        if (answer.startsWith("answer")) {
                            String[] answerParts = answer.split(" ");
                            int result = Integer.parseInt(answerParts[1]);
                            
                            if (result == 0) {
                                System.out.println("Antwort: WASSER (0)");
                                System.out.println("Zug wechselt zu Client.");
                                myTurn = false; // Client ist jetzt am Zug
                            } else if (result == 1) {
                                System.out.println("Antwort: TREFFER (1)");
                                System.out.println("Server darf nochmal schießen!");
                                // Server bleibt am Zug (myTurn bleibt true)
                            } else if (result == 2) {
                                System.out.println("Antwort: VERSENKT (2)");
                                System.out.println("Zug wechselt zu Client.");
                                myTurn = false; // Client ist jetzt am Zug
                            }
                        }
                        
                    } else if (command.equals("options")) {
                        // Options-Menü
                        System.out.println("\n=== OPTIONS MENÜ ===");
                        System.out.println("1. save - Spiel speichern und beenden");
                        System.out.println("2. continue - Weiter spielen");
                        System.out.print("Auswahl: ");
                        String option = usr.readLine();
                        
                        if (option.equals("1") || option.toLowerCase().equals("save")) {
                            System.out.print("Speicher-ID eingeben: ");
                            String saveId = usr.readLine();
                            send(out, "save " + saveId);
                            System.out.println("Gesendet: save " + saveId);
                            
                            // Warte auf ok vom Client
                            String response = receive(in);
                            if (response.equals("ok")) {
                                System.out.println("Spiel gespeichert. Verbindung wird beendet.");
                                gameStarted = false;
                            }
                        } else {
                            System.out.println("Weiter spielen...");
                        }
                    }
                    
                } else {
                    // Client ist am Zug - warte auf seinen Schuss
                    System.out.println("\n[WARTE AUF CLIENT] Client ist am Zug...");
                    
                    String clientMessage = receive(in);
                    System.out.println("Empfangen: " + clientMessage);
                    
                    String[] parts = clientMessage.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot")) {
                        // Client hat geschossen
                        System.out.println("Client schießt auf: Reihe " + parts[1] + ", Spalte " + parts[2]);
                        
                        // Antwort eingeben
                        System.out.print("[SERVER ANTWORT] Antwort eingeben (0=Wasser, 1=Treffer, 2=Versenkt): ");
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
                            // Bei Wasser oder Versenkt: Client beendet seinen Zug
                            System.out.println("Client ist fertig mit diesem Zug.");
                            // Server wird am Zug sein
                            myTurn = true;
                        } else if (result == 1) {
                            System.out.println("Client darf nochmal schießen (Treffer).");
                            // Client bleibt am Zug (myTurn bleibt false)
                        }
                        
                    } else if (command.equals("save")) {
                        // Client möchte speichern
                        System.out.println("Client möchte speichern: " + clientMessage);
                        send(out, "ok");
                        System.out.println("Gesendet: ok");
                        gameStarted = false;
                    } else {
                        System.out.println("Unerwartete Nachricht vom Client: " + clientMessage);
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
