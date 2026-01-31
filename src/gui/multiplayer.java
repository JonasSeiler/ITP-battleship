package src.gui;

import javax.swing.*;
import java.awt.*;
import src.coms.*;
import javax.swing.KeyStroke; // Ein Objekt, dass einen spezifischen Tastendruck definiert
import javax.swing.AbstractAction; // man kann Aktionen erstellen, welche die Logik enthalten, was passieren soll wenn man den Button drückt oder auf eine spezielle Taste drückt
import java.awt.event.KeyEvent; // Enthält die Namen für alle Tasten (z.B. VK_Escape)
import java.awt.event.ActionEvent; // enthält die Struktur für die Daten, die Java für das Action Event liefern muss, welches Java erwartet für die Methode actionPerformed

/**
 * Screen in multiplayer mode, where you can decide whether to join a game or host a game.
 * @author Max Steingräber, Matthias Wiese
 */
public class multiplayer extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private Mainframe frame; // Referenz auf das Hauptfenster
    private RoundButton join_game;
    private RoundButton host_game;
    private RoundButton exit;
    private JButton hamburger;
    /**
     * Initializes the screen for the multiplayer mode.
     * Creates the buttons and the layout.
     * @param frame the reference to the main window used for screen transitions
     */
    public multiplayer(Mainframe frame) { // mainframe ist das Hauptfenster und der multiplayerscreen gibt Befehle an den mainframe
        this.frame = frame;
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den multiplayerscreen gepackt

        AbstractAction exitAction = new AbstractAction() { // Objekt welches die Logik für eine Aktion definiert
            @Override
            public void actionPerformed(ActionEvent e) { // Methode des Objekts wird überschrieben mit der Logik
                frame.showScreen("titlescreen");
            }
        };
        KeyStroke exitTaste = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0); // erstellt ein fertiges KeyStroke Objekt mit dem Kriterum, dass es die esc Taste speichert
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitTaste, "exit"); // überwacht, ob die Exit Taste gedrückt wurde
        this.getActionMap().put("exit", exitAction); // führt die Aktion aus, wenn die InputMap die Taste erkannt hat

        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        JLabel title = new JLabel("Battleship");
        title.setForeground(Color.WHITE);
        join_game = new RoundButton("Join Game");
        host_game = new RoundButton("Host Game");
        exit = new RoundButton("Exit");
        title.setFont(new Font("Times New Roman", Font.BOLD,50));
        
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

        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den multiplayerscreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt

        contentPanel.add(title);
        contentPanel.add(new JLabel(""));
        contentPanel.add(join_game);
        contentPanel.add(host_game);
        contentPanel.add(exit);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das multiplayerscreen-Panel gelegt
        join_game.addActionListener(e -> {frame.showScreen("joinscreen");});
        host_game.addActionListener(e -> {
            frame.lastscreen2 = "multiplayer";
            frame.showScreen("waitingscreen");
            if (frame.coms != null) {
                frame.coms = null;
            }

            frame.coms = new Server();
            new SwingWorker<Void, Void>() {
                protected Void doInBackground() throws Exception {
                    try {
                        frame.coms.start();
                        frame.showScreen("pregamescreen");
                    } catch(Exception ex) {
                        System.err.println("Error starting to host: " + ex);
                    }
                    return null;
                }
            }.execute();
        });

        exit.addActionListener(exitAction);
        hamburger.addActionListener(e -> {
                frame.lastscreen = "multiplayer";
                frame.showScreen("settings");
        });
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
        g2d.fillRect(0, 0, getWidth(), getHeight()); // dadurch wird gemalt.
    }
}
