package src.gui; // Klasse gehört zu src.gui
import javax.swing.*;
import java.awt.*;
import javax.swing.KeyStroke; // Ein Objekt, dass einen spezifischen Tastendruck definiert
import javax.swing.AbstractAction; // man kann Aktionen erstellen, welche die Logik enthalten, was passieren soll wenn man den Button drückt oder auf eine spezielle Taste drückt
import java.awt.event.KeyEvent; // Enthält die Namen für alle Tasten (z.B. VK_Escape)
import java.awt.event.ActionEvent; // enthält die Struktur für die Daten, die Java für das Action Event liefern muss, welches Java erwartet für die Methode actionPerformed

/**
 * Screen where the user can change the settings.
 * @author Matthias Wiese
 */
public class Settingsscreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private Mainframe frame; // Referenz auf das Hauptfenster
    private JButton hamburger;
    private RoundButton changeColor;
    private RoundButton game_instructions;
    /**
     * Creates the screen and adds the setting buttons.
     * @param frame the reference to the main window used for screen transitions
     *
     */
    public Settingsscreen(Mainframe frame) { // Mainframe ist das Hauptfenster und Settingsscreen gibt Befehle an den Mainframe
        this.frame = frame;
        this.setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den Settingsscreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, eine Spalte und die Abstände sind 10
        
        AbstractAction exitAction = new AbstractAction() { // Objekt welches die Logik für eine Aktion definiert
            @Override
            public void actionPerformed(ActionEvent e) { // Methode des Objekts wird überschrieben mit der Logik
                frame.showScreen(frame.lastscreen);
            }
        };
        KeyStroke exitTaste = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0); // erstellt ein fertiges KeyStroke Objekt mit dem Kriterum, dass es die esc Taste speichert
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitTaste, "exit"); // überwacht, ob die Exit Taste gedrückt wurde
        this.getActionMap().put("exit", exitAction); // führt die Aktion aus, wenn die InputMap die Taste erkannt hat

        changeColor = new RoundButton("Change Background Color");
        game_instructions = new RoundButton("Game Instructions");

        hamburger = new JButton("X");
        hamburger.setFont(new Font("Arial", Font.PLAIN,25));
        hamburger.setForeground(Color.WHITE);
        hamburger.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das X Symbol
        hamburger.setFocusPainted(false); // Entfernt den blauen Rand beim Anklicken
        hamburger.setCursor(new Cursor(Cursor.HAND_CURSOR)); // wenn man drüber geht wird der Cursor geändert
        hamburger.setOpaque(false); // damit die Ecken durchsichtig bleiben, also der Hintergrund des Buttons wird nicht gemalt
        hamburger.setContentAreaFilled(false); // Damit Java nicht sein Design darein malt
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Reservierung der allerersten Zelle oben links (Spalte 0)
        gbc.gridy = 0; // Reservierung der allerersten Zelle oben links (Zeile 0)
        gbc.weightx = 1.0; // Diese Zelle soll horizontal den gesamten verfügbaren Platz beanspruchen
        gbc.weighty = 0.001; // Diese Zelle soll vertikal 0,1 des gesamten verfügbaren Platz beanspruchen
        gbc.anchor = GridBagConstraints.FIRST_LINE_END; // Die Komponente, die hinzugefügt wird kommt in die obere rechte Ecke
        gbc.insets = new Insets(50, 50, 50, 50); // 50 Pixel Abstand (oben, links, unten, rechts)

        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den settingsscreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
        contentPanel.add(new JLabel("")); // Erzeugt eine leere Zelle als Platzhalter
        contentPanel.add(new JLabel(""));
        contentPanel.add(changeColor);
        contentPanel.add(game_instructions);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das settingsscreen-Panel gelegt
        hamburger.addActionListener(exitAction);
        changeColor.addActionListener(e -> {frame.changeColor(); repaint();});
        game_instructions.addActionListener(e -> {frame.showScreen("game_instructions");});
    }
    /**
     * Draws the color gradient background of the screen.
     * Method is automatically called by the system when the component needs to be redrawn.
     * @param g the graphics object provided by the system for drawing on
     */
    @Override
    protected void paintComponent(Graphics g) { // Graphics bündelt die notwendigen Werkzeuge und den aktuellen Zeichenzustand(Farbe, Schriftart...) und auf dem Objekt kann man Zeichenbefehle aufrufen
        super.paintComponent(g); // löschen des alten Inhalts
        Graphics2D g2d = (Graphics2D) g; // g wird umgewandelt in das Graphics2D Objekt
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Befehl aktiviert die Kantenglättung
        GradientPaint oceanGradient = new GradientPaint(0, 0, frame.colorsheme.color1, 0, getHeight(), frame.colorsheme.color2); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // Dadurch wird gesagt womit gezeichnet wird
        g2d.fillRect(0, 0, getWidth(), getHeight()); // dadurch wird gemalt
    }
}
