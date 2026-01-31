package src.gui;
import javax.swing.*;

import src.coms.Server;

import java.awt.*;
import javax.swing.KeyStroke; // Ein Objekt, dass einen spezifischen Tastendruck definiert
import javax.swing.AbstractAction; // man kann Aktionen erstellen, welche die Logik enthalten, was passieren soll wenn man den Button drückt oder auf eine spezielle Taste drückt
import java.awt.event.KeyEvent; // Enthält die Namen für alle Tasten (z.B. VK_Escape)
import java.awt.event.ActionEvent; // enthält die Struktur für die Daten, die Java für das Action Event liefern muss, welches Java erwartet für die Methode actionPerformed

/**
 * Screen in multiplayer mode, where you can join a game
 * @author Max Steingräber, Matthias Wiese
 */
public class Joinwaitscreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
    private RoundButton exit;
    private JButton hamburger;
    JLabel waitMsg;

    /**
     * Creates the screen for joining a game and creates and initializes objects.
     * @param frame the reference to the main window so that methods for changing screens can be called on it later
     */
    public Joinwaitscreen(mainframe frame) { // mainframe ist das Hauptfenster und der waitingscreen gibt Befehle an den mainframe
        this.frame = frame;
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den waitingscreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing

        AbstractAction exitAction = new AbstractAction() { // Objekt welches die Logik für eine Aktion definiert
            @Override
            public void actionPerformed(ActionEvent e) { // Methode des Objekts wird überschrieben mit der Logik
                frame.showScreen("joinscreen");
            try {
            frame.coms.close();
            } catch(Exception ex) {
                System.err.println("Failed closing connection: " + ex);
            }
            frame.coms = null;
            }
        };
        KeyStroke exitTaste = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0); // erstellt ein fertiges KeyStroke Objekt mit dem Kriterum, dass es die esc Taste speichert
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitTaste, "exit"); // überwacht, ob die Exit Taste gedrückt wurde
        this.getActionMap().put("exit", exitAction); // führt die Aktion aus, wenn die InputMap die Taste erkannt hat

        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        JLabel title = new JLabel("Battleship");
        JLabel waitMsg = new JLabel("waiting for host to start the game ...");
        title.setForeground(Color.WHITE);
        exit = new RoundButton("Exit");
        title.setFont(new Font("Times New Roman", Font.BOLD,40));
        // exit.setFont(new Font("Times New Roman", Font.BOLD,35));
        waitMsg.setFont(new Font("Times New Roman", Font.PLAIN,20));
        waitMsg.setForeground(Color.WHITE);


        hamburger = new JButton("\u2261");
        hamburger.setFont(new Font("Times New Roman", Font.BOLD,38));
        hamburger.setForeground(Color.WHITE);
        hamburger.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das ≡ Symbol
        hamburger.setFocusPainted(false); // Entfernt den Rand beim Anklicken
        hamburger.setOpaque(false); // damit die Ecken durchsichtig bleiben
        hamburger.setContentAreaFilled(false); // Damit Java nicht sein Design darein malt
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Reservierung der allerersten Zelle oben links (Spalte 0)
        gbc.gridy = 0; // Reservierung der allerersten Zelle oben links (Zeile 0)
        gbc.weightx = 1.0; // Diese Zelle soll horizontal den gesamten verfügbaren Platz beanspruchen
        gbc.weighty = 0.001; // Diese Zelle soll vertikal 0,1 des gesamten verfügbaren Platz beanspruchen
        gbc.anchor = GridBagConstraints.FIRST_LINE_END; // Die Komponente, die hinzugefügt wird kommt in die obere rechte Ecke
        gbc.insets = new Insets(50, 50, 50, 50); // 50 Pixel Abstand (oben, links, unten, rechts)

        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den Joinwaitscreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt

        contentPanel.add(title);
        contentPanel.add(new JLabel(""));
        contentPanel.add(waitMsg);
        contentPanel.add(new JLabel(""));
        contentPanel.add(exit);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das Joinwaitscreen-Panel gelegt

        exit.addActionListener(exitAction);

        hamburger.addActionListener(e -> {
                frame.lastscreen = "joinwaitscreen";
                frame.showScreen("settings");
        });
    }


    /**
     * Method for the color gradient of the screen
     * Method is automatically called by the system when the component needs to be redrawn.
     * @param g A graphic object provided by the system to display the component on the screen.
     */
    @Override
    protected void paintComponent(Graphics g) { // Graphics bündelt die notwendigen Werkzeuge und den aktuellen Zeichenzustand(Farbe, Schriftart...) und auf dem Objekt kann man Zeichenbefehle aufrufen
        super.paintComponent(g); // ruft die Basis-Zeichenfunktion auf, also die Logik der Mutterklasse, um einen sauberen Grafik-Kontext für das eigene Zeichnen zu schaffen
        Graphics2D g2d = (Graphics2D) g; // g wird umgewandelt in das Graphics2D Objekt
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Befehl aktiviert die Kantenglättung
        GradientPaint oceanGradient = new GradientPaint(0, 0, frame.colorsheme.color1, 0, getHeight(), frame.colorsheme.color2); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // Dadurch wird gesagt womit gezeichnet wird
        g2d.fillRect(0, 0, getWidth(), getHeight()); // dadurch wird gemalt. Festlegung wo und wie groß der Bereich ist, der gefüllt werden soll mit getWidth(),getHeight() bekomme ich die Breite und Höhe vom singleplayerobjekt
    }
}
