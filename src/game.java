public class game {
    player player1;
    player player2;
    int x;
    int y;
    int battleships;
    int cruisers;
    int destroyers;
    int submarines;
    int[][] map;
    public game(int x, int y) {
        map = new int[x][y];
        this.x = x;
        this.y = y;
    }
    void calculate_ships () {
        int anzahl = ((x * y) / 3);
        boolean nochmal = true;
        int anzahl2 = anzahl;
        anzahl /= 30;
        this.battleships = anzahl * 1;
        this.cruisers = anzahl * 2;
        this.destroyers = anzahl * 3;
        this.submarines = anzahl * 4;
        switch (anzahl2 % 30) {
            case 2:
            case 3:
                this.submarines ++;
                break;
            case 4:
            case 5:
                this.submarines++;
                this.destroyers++;
                break;
            case 6:
            case 7:
                this.submarines += 2;
                this.destroyers++;
                break;
            case 8:
            case 9:
                this.cruisers++;
                this.destroyers++;
                this.submarines++;
                break;
            case 10:
            case 11:
                this.cruisers++;
                this.destroyers++;
                this.submarines += 2;
                break;
            case 12:
            case 13:

                break;
            case 14:
                break;
            case 15:
                break;
            case 16:
                break;
            case 17:
                break;
            case 18:
                break;
            case 19:
                break;
            case 20:
                break;
            case 21:
                break;
            case 22:
                break;
            case 23:
                break;
            case 24:
                break;
            case 25:
                break;
            case 26:
                break;
            case 27:
                break;
            case 28:
                break;
            case 29:
                break;
        }
    }
}
