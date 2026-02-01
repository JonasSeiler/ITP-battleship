package src.gui; // Datei gehört in das Verzeichnis scr.gui
import javax.swing.*; // enthält die Komponenten
import java.awt.*; // Grafik-Werkzeuge für Farben, Schriftarten und die Zeichenbefehle

/**
 * Customized version of the JTextField.
 * It has a glass design and rounded corners.
 * @author Matthias Wiese
 */
public class RoundTextField extends JTextField { // erbt die Klasse JTextField also unter anderem alle Funktionen eines normalen Eingabefeldes
    /**
     * Initializes the RoundTextField.
     * Sets the font, colors, the background not to be painted, and centered text input.
     */
    public RoundTextField() {
        setOpaque(false); // Der Hintergrund wird dadurch nicht gemalt
        setForeground(Color.WHITE); // Buchstaben werden weiß
        setCaretColor(Color.WHITE); // Der blinkende Cursor wird weiß, also man sieht wo man gerade etwas eingibt
        setHorizontalAlignment(JTextField.CENTER); // Der Text startet in der Mitte, also man fängt mittig in dem Feld an zu tippen
        setFont(new Font("Consolas", Font.PLAIN, 18));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // dadurch klebt der Text nicht direkt am Rand also 5 Pixel oben, 10 Pixel links, 5 Pixel unten und 10 Pixel rechts
    }
    /**
     * Draws the text field with a glass effect and rounded corners.
     * @param g graphic object provided by the system to display the component on the screen
     */
    @Override // overrides Java's default paint function
    protected void paintComponent(Graphics g) { // ein einfaches graphic Objekt auf dem man Zeichenbefehle ausführen kann
        Graphics2D g2d = (Graphics2D) g.create(); // Grafik Objekt wird in das modernere Graphics2D Objekt umgewandelt und es wird eine Kopie erstellt also g ist immernoch gleich
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // macht die Kurven glatt und den Text glatt
        
        // Hintergrund: Halbdurchsichtiges Weiß
        g2d.setColor(new Color(255, 255, 255, 30)); // die ersten drei Zahlen stehen für Weiß. Die 30 sorgt für den Milchglaseffekt. 0 = unsichtbar, 255 = vollflächig weiß
        g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15); // malt die Fläche in der vorher ausgewählten Farbe. 0,0 = Startpunkt oben links in der Ecke des Textfeldes; getWidth()-1 = Breite des Feldes (-1 weil Computer bei 0 anfangen zu zählen und sonst der rechte Rand abgeschnitten aussehen würde, weil ein Pixel zu viel gezeichnet worden ist, den man aber nicht sieht und es dadurch abgeschnitten aussieht); getHeight()-1 = Höhe des Feldes; 15,15 = erste 15 horizontale Rundung, zweite 15 = vertikale Rundung
        
        // Rahmen: Eine feine Linie
        g2d.setColor(new Color(255, 255, 255, 100)); // gleiche Farbe aber kräftiger, also weniger durchsichtig und mehr Weiß
        g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15); // malt den Rahmen in der vorher ausgewählten Farbe
        
        super.paintComponent(g); // um Fehler zu vermeiden nutzt man hier den Aufruf mit dem originalen g Objekt. Es ist eine Methode, bei der die Mutterklasse den eigentlichen Text, den blinkenden Cursor und die Markierungen zeichnet
        g2d.dispose(); // Kopie es Objekts wird verworfen
    }
}