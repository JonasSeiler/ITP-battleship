package src.gui;
import javax.swing.*; // importiert alle GUI Komponenten aus dem Swing-Packet (JButton...)
import java.awt.*; // Basis Werkzeuge für Grafik (Grafik, Schriften)
import java.awt.geom.RoundRectangle2D; // Berechnet die mathematisch exakte Form der Rundungen für das Zeichnen

/**
 * Modern JSpinner in glass design with rounded corners.
 * Matching the appearance of RoundTextField and RoundButton.
 */
public class RoundSpinner extends JSpinner {
    /**
     * Initializes the RoundSpinner with a specific model.
     * Disables the default Swing theme to apply the custom glas design.
     * @param model the spinner model providing the value range and logic
     */
    public RoundSpinner(SpinnerModel model) {
        super(model); // Oberklasse initialisiert den JSpinner auch mit seiner Logik
        setOpaque(false); // damit der Hintergrund durchsichtig ist

        JComponent editor = getEditor(); // holt das Bauteil, in dem die Zahl steht
        if (editor instanceof JSpinner.DefaultEditor) { // DefaultEditor sorgt für das Layout und dort ist ein Textfield drin
            JTextField textfield = ((JSpinner.DefaultEditor)editor).getTextField(); // man wandelt das in ein DefaultEditor um, damit man die Methode aufrufen kann, und holt sich das Textfield
            ((JSpinner.DefaultEditor)editor).setOpaque(false); // macht den Hintergrund für das ganze Bauteil indem auch das Textfield ist durchsichtig
            textfield.setOpaque(false); // macht den Hintergrund für die innere Komponente durchsichtig vom RoundSpinner
            textfield.setForeground(Color.WHITE); // macht die Zahlen weiß
            textfield.setBackground(new Color(0, 0, 0, 0)); // malt den Hintergrund durchsichtig
            textfield.setHorizontalAlignment(JTextField.CENTER); // damit die Zahlen in der Mitte stehen

            for (Component c : getComponents()) {
                if (c instanceof JButton) {
                    JButton button = (JButton) c; // die Komponente wird in ein JButton gespeichert
                    button.setOpaque(false); // Buttonhintergrund wird durchsichtig
                    button.setContentAreaFilled(false); // Die Standard Farbe des Buttons entfernen
                    button.setBorderPainted(false); // entfernt den Standard Rahmen
                    button.setForeground(Color.WHITE); // Pfeile weiß färben
                }
            }
        }
    }
    /**
     * Draws the RoundSpinner.
     * Overrides the default method to enable rounded corners and the glass effect.
     * @param g The graphics object used for all painting operations of this component.
     */
    @Override
    protected void paintComponent(Graphics g) { // Objekt auf dem Zeichenbefehle ausgeführt werden können
        Graphics2D g2d = (Graphics2D) g.create(); // wir erstellen eine Kopie und nutzen ein moderneres Grafik Objekt mit mehr Funktionen
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // dadurch werden später beim zeichnen die Kanten glatt, sonst hätten die Rundungen einen Treppeneffekt
        g2d.setColor(new Color(255, 255, 255, 45)); // weiß mit einer geringen Deckkraft
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // 20 ist der Wert wie stark die Rundung sein soll

        g2d.setColor(new Color(255, 255, 255, 120)); // weiß mit höherer Deckkraft
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // malt die Kante des Spinners
        g2d.dispose(); // damit löscht man die Kopie des g Objekts
    }
}