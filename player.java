public class player {
    int number_of_ships;
    boolean player = false;
    ship[] ships; // muss man im Konstruktor initialisieren, da erst die Attribute initialisiert werden bevor der Konstruktor ausgeführt wird
    public player(int number_of_ships) {
        this.number_of_ships = number_of_ships;
        ships = new ship[number_of_ships];
    }
    int i = 0;
    public player(){}
    public void save_ships(ship ship) {
        if (i < number_of_ships) {
            ships[i] = ship;
            i++;
        }
    }
    public void ships_show () {
        for (int i = 0; i < ships.length; i++) {
            ship ship = ships[i];
            ship.show();
        }
    }
    public boolean check_loose() {
        for (int i = 0; i < ships.length; i++) {
            ship ship = ships[i];
            int j = 0;
            if (ship.destroyed()) {
                j++;
            }
            if (j == number_of_ships) {
                return true;
            }
        }
        return false;
    }
    public boolean hit (coordinate coordinate) {
        player = true;
        for (int i = 0; i < ships.length; i++) {
            ship ship = ships[i];
            if (ship.hit(coordinate)) {
                return true;
            }
        }
        return false;
    }
    public void attack () {

    }
}
