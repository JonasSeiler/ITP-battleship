package src.gui;
import javax.swing.*; // Werkzeuge für den Button
import java.awt.*; // Werkzeuge für Farben, Schriften und die Grafik-Power

/**
 * A customized version of the JButton.
 * It has a glass design and rounded corners.
 * @author Matthias Wiese
 */
public class RoundButton extends JButton { //vererbt JButton. RoundButton ist ein JButton
    private Color baseColor = new Color(255, 255, 255, 40); // Halbtransparentes Weiß
    private int cornerRadius = 30; // Wie rund der Button sein soll

    /**
     * Initializes the RoundButton.
     * Disables the default Swing theme to use the custom look.
     * @param label the text to be displayed on the button
     */
    public RoundButton(String label) { // Konstruktor
        super(label); // ruft den Konstruktor von JButton auf und der JButton, also die Mutterklasse schreibt den Text rein, aber es wird erstmal nur gespeichert
        setContentAreaFilled(false); // es wird das Standarddesigns des Buttons nicht mehr gemalt
        setOpaque(false); // lässt den Hintergrund durchscheinen
        setBorderPainted(false); // Entfernt den äußeren Rahmen des Buttons
        setFocusPainted(false);  // Verhindert den hässlichen Fokus-Rahmen
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Zeiger-Hand beim Hovern
    }

    /**
     * Draws the button.
     * Overrides the default method to enable rounded corners and the glass effect.
     * @param g graphic object provided by the system to display the component on the screen
     */
    @Override // signalisiert Java benutzt nicht die Standard-Zeichenmethode, sondern die hier
    protected void paintComponent(Graphics g) { // ein einfaches graphic Objekt auf dem man Zeichenbefehle ausführen kann
        Graphics2D g2d = (Graphics2D) g.create(); // Grafik Objekt wird in das modernere Graphics2D Objekt umgewandelt und es wird eine Kopie erstellt also g ist immernoch gleich
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // macht die Kurven glatt

        // Hover-Effekt: Farbe ändern, wenn Maus drüber ist oder gedrückt wird
        if (getModel().isPressed()) { // wenn der Button gedrückt wird
            g2d.setColor(baseColor.darker()); // vorher festgelegte Farbe wird dunkler gemacht
        } else if (getModel().isRollover()) { // wenn die Maus über den Button bewegt wird
            g2d.setColor(new Color(255, 255, 255, 80));
        } else {
            g2d.setColor(baseColor);
        }

        // Das abgerundete Rechteck zeichnen
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius); // dadurch wird gemalt Startpunkt oben links (0,0), getWidth() und getHeight() füllt die komplette Fläche aus und der cornerRadius rundet die Ecken ab

        // Den Text zeichnen (muss nach dem Hintergrund kommen!)
        super.paintComponent(g2d); // dadurch wird der Text gezeichnet in der Oberklassenmethode paintComponent. Es wird das g2d Objekt genommen, damit auch der Text von der Kantenglättung profitiert
        g2d.dispose(); // Kopie es Objekts wird verworfen
    }
}