import java.net.*;
import java.io.*;
import java.util.Arrays;

class Server {
    final static int PORT = 50000;

    // Speicherung der Spielparameter (jetzt nur noch eine Größe für beide Dimensionen)
    private static int boardSize = 0;
    private static String[] shipLengths = null;

    // Methode zum Senden einer Nachricht
    private static void sendMessage(Writer out, String message) throws IOException {
        System.out.println("S >>> " + message);
        out.write(String.format("%s%n", message));
        out.flush();
    }

    // Methode zum Empfangen einer Nachricht
    private static String receiveMessage(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            System.out.println("Verbindung durch Client geschlossen.");
            throw new IOException("Verbindung beendet.");
        }
        System.out.println("S <<< " + line);
        return line.trim();
    }

    // Methode zum Parsen der Nachricht in Befehl und Parameter
    private static String[] parseMessage(String message) {
        return message.split(" ");
    }

    // Phase 1: Interaktives Setup (Server-Initiator)
    private static void handleSetup(BufferedReader in, Writer out, BufferedReader usr) throws IOException {
        
        // SERVER MUSS STARTEN (size ODER load)
        while (true) {
            System.out.print("\n[SETUP - SERVER SENDET] Geben Sie 'size N' (z.B. size 10) oder 'load id' ein: ");
            String input = usr.readLine();
            if (input == null || input.isEmpty()) continue;
            
            String[] parts = parseMessage(input);
            String command = parts[0].toLowerCase();
            
            if (command.equals("size") && parts.length == 2) {
                // S: size N (Ping) -> N wird für Reihen und Spalten verwendet
                sendMessage(out, input);
                boardSize = Integer.parseInt(parts[1]);
                System.out.printf("👉 Lokal gespeichert: Größe %d x %d%n", boardSize, boardSize);

                // C: done (Pong)
                if (!receiveMessage(in).equals("done")) {
                    throw new IOException("Protokollfehler: Erwartete 'done' nach 'size'.");
                }
                break; // Erfolgreiches Setup-Kommando gesendet

            } else if (command.equals("load") && parts.length == 2) {
                // S: load id (Ping)
                sendMessage(out, input);
                // C: ok (Pong)
                if (!receiveMessage(in).equals("ok")) {
                    throw new IOException("Protokollfehler: Erwartete 'ok' nach 'load'.");
                }
                System.out.println("--- Lade-Prozess gestartet. Spiel beginnt. ---");
                return; // Setup bei Load beendet
            } else {
                System.out.println("Ungültige Eingabe oder Format. Wiederholen Sie die Eingabe.");
            }
        }
        
        // Nur wenn size gesendet wurde, geht es weiter mit ships
        // SERVER MUSS SCHIFFE SENDEN
        while (true) {
            System.out.print("\n[SETUP - SERVER SENDET] Geben Sie 'ships length...' ein: ");
            String input = usr.readLine();
            if (input == null || input.isEmpty()) continue;
            
            String[] parts = parseMessage(input);
            if (parts[0].toLowerCase().equals("ships") && parts.length >= 2) {
                // S: ships length... (Ping)
                sendMessage(out, input);
                shipLengths = Arrays.copyOfRange(parts, 1, parts.length);
                System.out.printf("👉 Lokal gespeichert: Schiffe %s%n", Arrays.toString(shipLengths));

                // C: done (Pong)
                if (!receiveMessage(in).equals("done")) {
                    throw new IOException("Protokollfehler: Erwartete 'done' nach 'ships'.");
                }
                break; // Erfolgreiches ships-Kommando gesendet
            } else {
                System.out.println("Ungültige Eingabe oder Format. Wiederholen Sie die Eingabe.");
            }
        }

        // SERVER MUSS READY SENDEN
        sendMessage(out, "ready"); // S: ready (Ping)

        // CLIENT MUSS READY SENDEN
        if (!receiveMessage(in).equals("ready")) { // C: ready (Pong)
            throw new IOException("Protokollfehler: Erwartete 'ready' vom Client.");
        }
        
        System.out.println("\n--- Setup abgeschlossen. Spiel beginnt. ---");
    }

    public static void main (String [] args) {
        Socket s = null;
        ServerSocket ss = null;
        BufferedReader usr = new BufferedReader(new InputStreamReader(System.in));

        try {
            // SYNTAX FEHLER behoben: Doppeltes 'new' entfernt
            ss = new ServerSocket(PORT);
            System.out.println("Warte auf Client-Verbindung auf Port " + PORT + " ...");
            s = ss.accept();
            System.out.println("Verbindung hergestellt.");

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Writer out = new OutputStreamWriter(s.getOutputStream());

            // Phase 1: Setup durchführen (interaktiv)
            handleSetup(in, out, usr);
            
            // Phase 2: Interaktive Spielschleife (Server beginnt)
            System.out.println("\n--- SPIELSCHLEIFE GESTARTET ---");
            System.out.println("Der Server ist am Zug (Erwartet Eingabe: shot/save)");

            while (true) {
                // SENDER-LOGIK: Server ist am Zug (erwartet Eingabe)
                System.out.print("\n[SERVER SENDET] Eingabe (shot row col | save id | pass | exit): ");
                String input = usr.readLine();
                if (input == null || input.isEmpty() || input.equals("exit")) break;
                
                String[] parts = parseMessage(input);
                String command = parts[0].toLowerCase();
                
                if (command.equals("shot") && parts.length == 3) {
                    sendMessage(out, input); // S: shot
                    
                    // Empfange Antwort auf Shot
                    String answerMsg = receiveMessage(in); // C: answer a
                    String[] answerParts = parseMessage(answerMsg);
                    
                    if (answerParts[0].equals("answer")) {
                        int result = Integer.parseInt(answerParts[1]);
                        System.out.printf("Antwort: %s. -> ", result == 0 ? "Wasser" : result == 1 ? "Treffer" : "Versenkt");

                        if (result == 0) {
                            System.out.println("Sende 'pass' und warte auf Client-Shot.");
                            sendMessage(out, "pass"); // S: pass
                            
                            // Warte auf Shot vom Client, da dieser jetzt am Zug ist
                            String clientShotMsg = receiveMessage(in); // C: shot row col
                            System.out.println("Client hat geschossen: " + clientShotMsg);
                            // Der Server müsste nun eine Antwort (answer) eingeben.

                        } else if (result == 2) {
                            System.out.println("Spiel beendet.");
                            break;
                        }
                        
                    } else {
                        System.out.println("Unerwartete Nachricht nach 'shot': " + answerMsg);
                    }
                } else if (command.equals("save") && parts.length == 2) {
                    sendMessage(out, input); // S: save id
                    // Empfange 'ok'
                    if (!receiveMessage(in).equals("ok")) { // C: ok
                         System.out.println("Fehler: Keine 'ok' Bestätigung auf 'save'.");
                    }
                } else if (command.equals("pass")) {
                    sendMessage(out, input); // S: pass
                    // Warte auf Shot vom Client
                    String clientShotMsg = receiveMessage(in); // C: shot row col
                    System.out.println("Client hat geschossen: " + clientShotMsg);
                    // Der Server müsste nun eine Antwort (answer) eingeben.
                } else {
                    System.out.println("Ungültiges Kommando oder Format.");
                }
            }

            if (s != null) {
                s.shutdownOutput();
                System.out.println("Verbindung ordnungsgemäß geschlossen.");
            }

        } catch (NumberFormatException e) {
             System.err.println("Fehler: Unerwartetes Zahlenformat in Protokollnachricht.");
        } catch (IOException e) {
            System.err.println("Netzwerk- oder I/O-Fehler: " + e.getMessage());
        } finally {
            try {
                if (s != null) s.close();
                if (ss != null) ss.close();
            } catch (IOException e) {
                System.err.println("Fehler beim Schließen der Ressourcen: " + e.getMessage());
            }
        }
    }
}
