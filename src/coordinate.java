package src;
public class coordinate {
    public int x, y;
    public coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        coordinate other = (coordinate) obj;
        return x == other.x && y == other.y;
    }
}

/*
coordinate[] cor;
cor[0].x = 3;
cor[0].y = 6;
3 arrays pro Schiff: 1. coordinate[] (Koordinaten x, y), 2. ships[] (länge), 3. Richtung[] (0 -> rechts, 1-> unten) bsp.: (0, 0, 0, 1, 1)
Jeder Index [0], [1], ... ist ein seperates Schiff
Schießen: Zellenkoordinate wo er hinschießt -> cor.x und cor.y
Jonas Methode mit Parameter (coordinate) und Rückgabewerte 0, 1 oder 2
 -> coordinate Parameter enthält x und y-Koordinate, wo hingeschossen wird
 -> ich bekomme zurück: 0: verfehlt(rot), 1: getroffen(gelb), 2: versenkt(grün)
Ich brauche noch eine Schießmethode getroffen werden. Parameter: (cor, int a) -> a: 0: verfehlt, 1: getroffen, 2:versenkt Rückgabewert: void
 -> linkes Feld (player field) richtig einfärben 0: verfehlt(rot), 1: getroffen(gelb), 2: versenkt(grün) 
Matthias Klasse: int gridSize; int ships[] = {5, 5, 4, 3, 2, 2}

Jonas Methode: 
*/