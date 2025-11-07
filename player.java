public class player {
    int number_of_ships;
    ship[] ships; // muss man im Konstruktor initialisieren, da erst die Attribute initialisiert werden bevor der Konstruktor ausgeführt wird
    public player(int number_of_ships) {
        this.number_of_ships = number_of_ships;
        ships = new ship[number_of_ships];
    }
    int i = 0;
    public void save_ships(ship ship) {
        if (i < number_of_ships) {
            ships[i] = ship;
            i++;
        }
    }
    public void ships_show () {
        for (ship ship : ships) {
            ship.show();
        }
    }

    public boolean check_loose() {
        for (ship ship : ships) {
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
        for (ship ship : ships) {
            if (ship.hit(coordinate)) {
                return true;
            }
        }
        return false;
    }
    public coordinate attack (coordinate coordinate) {
        return coordinate;
    }
}
