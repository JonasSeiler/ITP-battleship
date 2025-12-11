import java.net.*;
import java.io.*;

class Client {
    final static int PORT = 50000;
    
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
                    // Server hat size gesendet
                    System.out.print("\n[CLIENT SETUP] Eingabe ('done'): ");
                    String doneInput = usr.readLine();
                    while (!doneInput.equals("done")) {
                        System.out.print("Ungültig! Bitte 'done' eingeben: ");
                        doneInput = usr.readLine();
                    }
                    send(out, doneInput);
                    System.out.println("Gesendet: " + doneInput);
                    
                    // Empfange ships
                    String shipsMessage = receive(in);
                    System.out.println("Empfangen: " + shipsMessage);
                    
                    System.out.print("\n[CLIENT SETUP] Eingabe ('done'): ");
                    doneInput = usr.readLine();
                    while (!doneInput.equals("done")) {
                        System.out.print("Ungültig! Bitte 'done' eingeben: ");
                        doneInput = usr.readLine();
                    }
                    send(out, doneInput);
                    System.out.println("Gesendet: " + doneInput);
                    
                    // Empfange ready
                    String readyMsg = receive(in);
                    System.out.println("Empfangen: " + readyMsg);
                    
                    System.out.print("\n[CLIENT SETUP] Eingabe ('ready'): ");
                    String readyInput = usr.readLine();
                    while (!readyInput.equals("ready")) {
                        System.out.print("Ungültig! Bitte 'ready' eingeben: ");
                        readyInput = usr.readLine();
                    }
                    send(out, readyInput);
                    System.out.println("Gesendet: " + readyInput);
                    
                    setupComplete = true;
                    
                } else if (command.equals("load")) {
                    // Server hat load gesendet
                    System.out.print("\n[CLIENT SETUP] Eingabe ('ok'): ");
                    String okInput = usr.readLine();
                    while (!okInput.equals("ok")) {
                        System.out.print("Ungültig! Bitte 'ok' eingeben: ");
                        okInput = usr.readLine();
                    }
                    send(out, okInput);
                    System.out.println("Gesendet: " + okInput);
                    
                    // Empfange ready
                    String readyMsg = receive(in);
                    System.out.println("Empfangen: " + readyMsg);
                    
                    System.out.print("\n[CLIENT SETUP] Eingabe ('ready'): ");
                    String readyInput = usr.readLine();
                    while (!readyInput.equals("ready")) {
                        System.out.print("Ungültig! Bitte 'ready' eingeben: ");
                        readyInput = usr.readLine();
                    }
                    send(out, readyInput);
                    System.out.println("Gesendet: " + readyInput);
                    
                    setupComplete = true;
                }
            }

            // --- GAME LOOP ---
            System.out.println("\n=== SPIEL BEGINNT ===");
            
            boolean gameActive = true;
            boolean myTurn = false; // Client beginnt nicht
            
            while (gameActive) {
                if (!myTurn) {
                    // Server ist am Zug
                    System.out.println("\n[WARTE AUF SERVER] Server ist am Zug...");
                    
                    String serverMessage = receive(in);
                    System.out.println("Empfangen: " + serverMessage);
                    
                    String[] parts = serverMessage.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot")) {
                        // Server hat geschossen
                        System.out.print("\n[CLIENT ANTWORT] Eingabe ('answer 0', 'answer 1' oder 'answer 2'): ");
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
                            // Warte auf pass vom Server
                            String passMsg = receive(in);
                            if (passMsg.equals("pass")) {
                                System.out.println("Empfangen: " + passMsg);
                                myTurn = true; // Client ist jetzt am Zug
                            }
                        } else {
                            // Bei Treffer oder Versenkt: Server bleibt am Zug
                            // (keine Aktion nötig)
                        }
                        
                    } else if (command.equals("save")) {
                        // Server möchte speichern
                        System.out.print("\n[CLIENT ANTWORT] Eingabe ('ok'): ");
                        String okInput = usr.readLine();
                        while (!okInput.equals("ok")) {
                            System.out.print("Ungültig! Bitte 'ok' eingeben: ");
                            okInput = usr.readLine();
                        }
                        send(out, okInput);
                        System.out.println("Gesendet: " + okInput);
                        gameActive = false;
                        
                    } else if (command.equals("pass")) {
                        // Server gibt Zug ab
                        myTurn = true;
                    }
                    
                } else {
                    // Client ist am Zug
                    System.out.print("\n[CLIENT ZUG] Eingabe ('shot x y' oder 'save id'): ");
                    String input = usr.readLine();
                    
                    if (input == null || input.trim().isEmpty()) continue;
                    
                    String[] parts = input.split(" ");
                    String command = parts[0].toLowerCase();
                    
                    if (command.equals("shot") && parts.length == 3) {
                        // Schuss senden
                        send(out, input);
                        System.out.println("Gesendet: " + input);
                        
                        // Warte auf answer vom Server
                        String answer = receive(in);
                        System.out.println("Empfangen: " + answer);
                        
                        if (answer.startsWith("answer")) {
                            String[] answerParts = answer.split(" ");
                            int result = Integer.parseInt(answerParts[1]);
                            
                            if (result == 0) {
                                // Bei Wasser: pass senden
                                System.out.print("\n[CLIENT ZUG] Eingabe ('pass'): ");
                                String passInput = usr.readLine();
                                while (!passInput.equals("pass")) {
                                    System.out.print("Ungültig! Bitte 'pass' eingeben: ");
                                    passInput = usr.readLine();
                                }
                                send(out, passInput);
                                System.out.println("Gesendet: " + passInput);
                                myTurn = false; // Server ist jetzt am Zug
                            } else if (result == 1 || result == 2) {
                                // Bei Treffer oder Versenkt: Client bleibt am Zug
                                // (keine Aktion nötig)
                            }
                        }
                        
                    } else if (command.equals("save") && parts.length == 2) {
                        // Save senden
                        send(out, input);
                        System.out.println("Gesendet: " + input);
                        
                        // Warte auf ok vom Server
                        String response = receive(in);
                        if (response.equals("ok")) {
                            System.out.println("Empfangen: " + response);
                            gameActive = false;
                        }
                    } else {
                        System.out.println("Ungültiges Kommando. Erwartet: 'shot x y' oder 'save id'");
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
