// Server verwenden
Server server = new Server();
server.start();

// Setup
if (server.sendSize(10)) {
    int[] ships = {5, 4, 4, 3, 2};
    if (server.sendShips(ships)) {
        if (server.sendReady()) {
            System.out.println("Spiel gestartet");
        }
    }
}

// Gemeinsame Methoden von NetworkPlayer
int result = server.sendShot(5, 5);
if (result == 0) {
    server.sendPass();
    NetworkPlayer.MessageType msg = server.receiveMessageWithSaveHandling();
    if (msg.isShot()) {
        int[] shot = msg.getShotCoords();
        server.sendAnswer(1);
    }
}

// Client verwenden
Client client = new Client();
client.setServerAddress("127.0.0.1");
client.start();

// Setup empfangen
NetworkPlayer.GameConfig config = client.receiveSetup();

// Gemeinsame Methoden von NetworkPlayer
NetworkPlayer.MessageType msg = client.receiveMessageWithSaveHandling();
if (msg.isShot()) {
    int[] shot = msg.getShotCoords();
    client.sendAnswer(0);
    client.sendPass();
}



// Kann mit NetworkPlayer-Referenz arbeiten
NetworkPlayer player = isServer ? new Server() : new Client();
// Gemeinsame Methoden verfügbar
player.sendShot(1, 1);
player.sendAnswer(0);
