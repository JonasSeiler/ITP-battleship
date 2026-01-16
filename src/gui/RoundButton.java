package src.gui;
import javax.swing.*; // Werkzeuge für den Button
import java.awt.*; // Werkzeuge für Farben, Schriften und die Grafik-Power

public class RoundButton extends JButton { //vererbt JButton. RoundButton ist ein JButton
    private Color baseColor = new Color(255, 255, 255, 40); // Halbtransparentes Weiß
    private int cornerRadius = 30; // Wie rund der Button sein soll

    public RoundButton(String label) { // Konstruktor
        super(label); // ruft den Konstruktor von JButton auf und der JButton, also die Mutterklasse schreibt den Text rein
        // setContentAreaFilled(false); // Fläche im inneren wird entfernt (brauch man vielleicht bei Windows)
        setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons
        setFocusPainted(false);  // Verhindert den hässlichen Fokus-Rahmen
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Zeiger-Hand beim Hovern
    }

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
        super.paintComponent(g2d); // dadurch wird der Text gezeichnet in der Oberklassenmethode paintComponent
        g2d.dispose(); // Kopie es Objekts wird verworfen
    }
}