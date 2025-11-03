public class ship {
    int length;
    coordinate[] coordinates;
    int i = 0;
    public ship(int length) {
        this.length = length;
        coordinates = new coordinate[length];
    }
    public void place(coordinate coordinate) {
        if (i < length) {
            this.coordinates[i] = coordinate;
            i++;
        }
    }
    public void show() {
        for (int i = 0; i < coordinates.length; i++) {
            coordinate kor = coordinates[i];
            if (kor != null) {
                System.out.print("x = " + kor.x + "\t");
                System.out.println("y = " + kor.y);
            }
        }
    }
    public boolean destroyed () {
        for (int i = 0; i < coordinates.length; i++) {
            int j = 0;
            coordinate coordinate = coordinates[i];
            if (coordinate.lives) {
                j++;
            }
            if (j == 0) {
                return true;
            }
        }
        return false;
    }
    public boolean hit (coordinate coordinate) {
        for (int i = 0; i < coordinates.length; i++) {
            coordinate cor = coordinates[i];
            if (cor.x == coordinate.x && cor.y == coordinate.y) {
                return true;
            }
        }
        return false;
    }
}
