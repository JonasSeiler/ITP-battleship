package src.gui;
import java.awt.Color;

/**
 * Class for managing two color objects
 * @author Matthias Wiese
 */
class Colorpair {
    public Color color1;
    public Color color2;
    /**
     * Creates a new color pair for a color gradient.
     * @param color1 First color object initialized in the constructor
     * @param color2 Second color object initialized in the constructor
     */
    Colorpair(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }
}
