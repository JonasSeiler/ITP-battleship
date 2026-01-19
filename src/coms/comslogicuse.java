package src.coms;

/**
 * Beispielklasse für die Verwendung der Kommunikationsklassen
 */
public class comslogicuse {
    public static void main(String[] args) {
        try {
            // Polymorphe Verwendung
            NetworkPlayer player;
            
            if (args.length > 0) {
                switch (args[0]) {
                    case "server":
                        player = new Server();
                        player.start();
                        // Setup mit Größe und Schiffen
                        if (((Server) player).sendSize(10)) {
                            int[] ships = {5, 4, 4, 3, 2};
                            if (((Server) player).sendShips(ships)) {
                                if (((Server) player).sendReady()) {
                                    System.out.println("Server: Spiel gestartet");
                                }
                            }
                        }
                        break;
                        
                    case "client":
                        if (args.length < 2) {
                            System.err.println("Client benötigt Server-Adresse");
                            return;
                        }
                        Client client = new Client();
                        client.setServerAddress(args[1]);
                        player = client;
                        player.start();
                        
                        // Setup empfangen
                        NetworkPlayer.GameConfig config = client.receiveSetup();
                        System.out.println("Client: Spiel konfiguriert");
                        break;
                        
                    case "bot":
                        Bot bot = new Bot();
                        player = bot;
                        player.start();
                        
                        // Bot-Setup
                        if (bot.sendSize(10)) {
                            int[] ships = {5, 4, 4, 3, 2};
                            if (bot.sendShips(ships)) {
                                if (bot.sendReady()) {
                                    System.out.println("Bot: Spiel gegen Computer gestartet");
                                }
                            }
                        }
                        break;
                        
                    default:
                        System.err.println("Ungültiger Modus: server, client oder bot");
                        return;
                }
                
                // Gemeinsame Methoden (polymorph)
                if (player.isGameStarted()) {
                    // Beispiel: Erster Schuss
                    int result = player.sendShot(5, 5);
                    System.out.println("Antwort auf Schuss: " + result);
                    
                    if (result == 0) {
                        player.sendPass();
                    }
                }
                
                player.close();
                
            } else {
                System.out.println("Verwendung:");
                System.out.println("  java comslogicuse server");
                System.out.println("  java comslogicuse client <server-ip>");
                System.out.println("  java comslogicuse bot");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
