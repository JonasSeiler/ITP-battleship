import java.net.*;
import java.io.*;
import java.util.Arrays;

class Client {
    final static int PORT = 50000;

    // Speicherung der Spielparameter (Deklaration behoben)
    private static int boardSize = 0;
    private static String[] shipLengths = null;

    // Methode zum Senden einer Nachricht
    private static void sendMessage(Writer out, String message) throws IOException {
        System.out.println("C >>> " + message);
        out.write(String.format("%s%n", message));
        out.flush();
    }

    // Methode zum Empfangen einer Nachricht
    private static String receiveMessage(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            System.out.println("Verbindung durch Server geschlossen.");
            throw new IOException("Verbindung beendet.");
        }
        System.out.println("C <<< " + line);
        return line.trim();
    }
    
    // Methode zum Parsen der Nachricht in Befehl und Parameter
    private static String[] parseMessage(String message) {
        return message.split(" ");
    }


    // Phase 1: Interaktives Setup (Client-Responder)
    private static void handleSetup(BufferedReader in, Writer out, BufferedReader usr) throws IOException {
        
        System.out.println("\n[SETUP - CLIENT WARTET] Erwartet 'size N' oder 'load id' vom Server...");
        
        // --- 1. S: size N ODER load id ---
        String initialMsg = receiveMessage(in); 
        String[] initialParts = parseMessage(initialMsg);
        String command = initialParts[0].toLowerCase();
        
        if (command.equals("size")) {
            // S: size N
            if (initialParts.length < 2) throw new IOException("Protokollfehler: 'size' ohne Parameter.");
            
            boardSize = Integer.parseInt(initialParts[1]); // Speichert die Größe
            System.out.printf("👉 Lokal gespeichert: Größe %d x %d%n", boardSize, boardSize); // Ausgabe ändert sich

            // C: done (Pong)
            sendMessage(out, "done");

            // --- 2. S: ships length... ---
            String shipsMsg = receiveMessage(in);
            String[] shipsParts = parseMessage(shipsMsg);

            if (shipsParts[0].equals("ships")) {
                shipLengths = Arrays.copyOfRange(shipsParts, 1, shipsParts.length);
                System.out.printf("👉 Lokal gespeichert: Schiffe %s%n", Arrays.toString(shipLengths));
                
                // C: done (Pong)
                sendMessage(out, "done");
            } else {
                throw new IOException("Protokollfehler: Erwartete 'ships' nach 'size'.");
            }

        } else if (command.equals("load")) {
            // S: load id
            if (initialParts.length < 2) throw new IOException("Protokollfehler: 'load' ohne Parameter.");
            
            System.out.println("Lade Spielstand ID: " + initialParts[1]);
            // C: ok
            sendMessage(out, "ok");
            System.out.println("--- Lade-Prozess gestartet. Spiel beginnt. ---");
            return; // Setup bei Load beendet
            
        } else {
            throw new IOException("Protokollfehler: Erwartete 'size' oder 'load' als erste Nachricht.");
        }

        // --- 3. S: ready (Ping) ---
        if (receiveMessage(in).equals("ready")) {
             // C: ready (Pong)
            sendMessage(out, "ready");
        } else {
             throw new IOException("Protokollfehler: Erwartete 'ready' vom Server.");
        }
        
        System.out.println("\n--- Setup abgeschlossen. Spiel beginnt. ---");
    }

    public static void main (String [] args) {
        if (args.length == 0) {
            System.err.println("Nutzung: java Client <server_ip_adresse>");
            return;
        }

        Socket s = null;
        BufferedReader usr = new BufferedReader(new InputStreamReader(System.in));

        try {
            s = new Socket(args[0], PORT);
            System.out.println("Verbindung hergestellt.");

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Writer out = new OutputStreamWriter(s.getOutputStream());

            // Phase 1: Setup durchführen (interaktiv)
            handleSetup(in, out, usr);
            
            // Phase 2: Interaktive Spielschleife (Client wartet)
            System.out.println("\n--- SPIELSCHLEIFE GESTARTET ---");
            System.out.println("Der Client wartet auf den ersten Zug des Servers (Erwartet: shot/save)");

            while (true) {
                // EMPFÄNGER-LOGIK: Client wartet auf shot, pass oder save
                System.out.print("\n[CLIENT WARTET] Erwartet 'shot', 'pass' oder 'save' vom Server... (Oder geben Sie 'exit' ein, um zu beenden): ");
                
                String receivedMsg = receiveMessage(in); // Wartet auf Server-Kommando
                String[] parts = parseMessage(receivedMsg);
                String command = parts[0].toLowerCase();
                
                if (command.equals("shot")) {
                    // S: shot row col
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    System.out.printf("Server schießt auf: Reihe %d, Spalte %d.%n", row, col);
                    
                    // SENDER-LOGIK: Client schickt Antwort (answer a)
                    
                    while (true) {
                        System.out.print("[CLIENT SENDET] Geben Sie 'answer a' (0=Wasser, 1=Treffer, 2=Versenkt) ein: ");
                        String answerInput = usr.readLine();
                        if (answerInput == null || answerInput.isEmpty()) continue;
                        
                        String[] answerParts = parseMessage(answerInput);
                        if (answerParts[0].toLowerCase().equals("answer") && answerParts.length == 2) {
                            sendMessage(out, answerInput); // C: answer a
                            if (Integer.parseInt(answerParts[1]) == 2) {
                                System.out.println("Spiel beendet.");
                                return;
                            }
                            break;
                        } else {
                            System.out.println("Ungültige 'answer' Eingabe.");
                        }
                    }

                } else if (command.equals("pass")) {
                    // S: pass
                    System.out.println("Server hat gepasst. Client ist am Zug.");
                    
                    // SENDER-LOGIK: Client schickt Schuss (shot row col) oder save/pass
                    while (true) {
                         System.out.print("[CLIENT SENDET] Eingabe (shot row col | save id | exit): ");
                         String clientInput = usr.readLine();
                         if (clientInput == null || clientInput.isEmpty() || clientInput.equals("exit")) return;
                         
                         String[] clientParts = parseMessage(clientInput);
                         String clientCommand = clientParts[0].toLowerCase();
                         
                         if (clientCommand.equals("shot") && clientParts.length == 3) {
                             sendMessage(out, clientInput); // C: shot
                             break;
                         } else if (clientCommand.equals("save") && clientParts.length == 2) {
                              sendMessage(out, clientInput); // C: save id
                              break;
                         } else {
                            System.out.println("Ungültiges Kommando oder Format.");
                         }
                    }
                    
                    // Erwarte nun die Antwort des Servers (answer a oder ok)
                    String serverResponse = receiveMessage(in);
                    // Hier müsste die Logik zur Verarbeitung der Server-Antwort folgen.

                } else if (command.equals("save")) {
                    // S: save id
                    System.out.println("Aufforderung zum Speichern erhalten: " + parts[1]);
                    // C: ok
                    sendMessage(out, "ok"); 
                } else {
                    System.out.println("Unerwartete Nachricht im Spielzug: " + command);
                    break;
                }
            }

            if (s != null) {
                s.shutdownOutput();
                System.out.println("Verbindung ordnungsgemäß geschlossen.");
            }

        } catch (NumberFormatException e) {
             System.err.println("Fehler: Unerwartetes Zahlenformat in Protokollnachricht.");
        } catch (ConnectException e) {
             System.err.println("Verbindungsfehler: Läuft der Server unter " + args[0] + ":" + PORT + "?");
        } catch (IOException e) {
            System.err.println("Netzwerk- oder I/O-Fehler: " + e.getMessage());
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e) {
                System.err.println("Fehler beim Schließen des Sockets: " + e.getMessage());
            }
        }
    }
}
