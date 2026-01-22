package src.gui;
import java.awt.Color;

/**
 * Klasse zum verwalten von zwei Farbobjekten
 * @author Matthias Wiese
 */
class colorpair {
    public Color color1;
    public Color color2;
    /**
     * Erstellt ein neues Farbpaar für einen Farbverlauf.
     * @param color1 erstes Farbobjekt welches im Konstruktor initialisiert wird
     * @param color2 zweites Farbobjekt welches im Konstruktor initialisiert wird
     */
    colorpair(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }
}