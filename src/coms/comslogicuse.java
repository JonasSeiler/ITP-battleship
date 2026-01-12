// Server-Seite
Server server = new Server();
server.start();

// Setup mit Größe und Schiffen
if (server.sendSize(10)) {
    int[] ships = {5, 4, 4, 3, 2};
    if (server.sendShips(ships)) {
        if (server.sendReady()) {
            // Spiel beginnt
            
            // Server schießt
            int result = server.sendShot(5, 5);
            
            if (result == -1) {
                // Client hat save gesendet
                String saveId = client.getLastSaveId();
                // Speichern durchführen
            } else if (result == 0) {
                // Wasser - pass senden
                server.sendPass();
            }
            // Bei Treffer (1) oder Versenkt (2): weiter schießen
        }
    }
}

// Client-Seite
Client client = new Client();
client.connect("127.0.0.1");

Client.GameConfig config = client.receiveSetup();

if (config != null && config.isLoad) {
    // Geladenes Spiel
    String loadId = config.loadId;
}

// Im Spiel: Schuss empfangen
int[] shot = client.receiveShot();
if (shot == null) {
    // Server hat save gesendet
    String saveId = client.getLastSaveId();
    // Speichern durchführen
} else {
    // Antwort basierend auf Spiel-Logik senden
    if (isHit(shot[0], shot[1])) {
        client.sendAnswer(1);  // Treffer
        // Client wartet auf nächsten Schuss (kein pass nötig)
    } else {
        client.sendAnswer(0);  // Wasser
        client.sendPass();     // Pass senden
    }
}
