public class ship {
    int length;
    String name;
    coordinate[] coordinates;
    int i = 0;
    public ship(int length, String name) {
        this.length = length;
        coordinates = new coordinate[length];
        this.name = name;
    }
    public void place(coordinate coordinate) {
        if (i < length) {
            this.coordinates[i] = coordinate;
            i++;
        }
    }
    public void show() {
        for (coordinate coordinate : coordinates) {
            if (coordinate != null) {
                System.out.println(name);
                System.out.print("x = " + coordinate.x + "\t");
                System.out.println("y = " + coordinate.y);
            }
        }
    }
    public boolean destroyed () {
        for (coordinate coordinate : coordinates) {
            int j = 0;
            if (coordinate.lives) {
                j++;
            }
            if (j == 0) {
                return true;
            }
        }
        return false;
    }
    public boolean hit (coordinate coordinate1) {
        for (coordinate coordinate2 : coordinates) {
            if (coordinate2.x == coordinate1.x && coordinate2.y == coordinate1.y) {
                return true;
            }
        }
        return false;
    }
}
