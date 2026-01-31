package src.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.KeyStroke; // Ein Objekt, dass einen spezifischen Tastendruck definiert
import javax.swing.AbstractAction; // man kann Aktionen erstellen, welche die Logik enthalten, was passieren soll wenn man den Button drückt oder auf eine spezielle Taste drückt
import java.awt.event.KeyEvent; // Enthält die Namen für alle Tasten (z.B. VK_Escape)
import java.awt.event.ActionEvent; // enthält die Struktur für die Daten, die Java für das Action Event liefern muss, welches Java erwartet für die Methode actionPerformed

/**
 * Screen where the user can read the game instructions and learn about shortcuts.
 * @author Matthias
 */

public class Game_instructions extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private Mainframe frame; // Referenz auf das Hauptfenster
    private JButton hamburger;
    private JLabel game_instructions_label;
    private JTextArea gameInstructions;

    /**
     * Initializes the screen with the game instructions.
     * Sets up the layout, creates the game instruction text, and describes the navigation shortcuts.
     * @param frame the reference to the main window used for screen transitions
     */

    public Game_instructions(Mainframe frame) { // mainframe ist das Hauptfenster und game_instructions gibt Befehle an den mainframe
        this.frame = frame;
        this.setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den game_instructionsscreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing

        AbstractAction exitAction = new AbstractAction() { // Objekt welches die Logik für eine Aktion definiert
            @Override
            public void actionPerformed(ActionEvent e) { // Methode des Objekts wird überschrieben mit der Logik
                frame.showScreen("settings");
            }
        };
        KeyStroke exitTaste = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0); // erstellt ein fertiges KeyStroke Objekt mit dem Kriterum, dass es die esc Taste speichert
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitTaste, "exit"); // überwacht, ob die Exit Taste gedrückt wurde
        this.getActionMap().put("exit", exitAction); // führt die Aktion aus, wenn die InputMap die Taste erkannt hat

        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        game_instructions_label = new JLabel("Game Instructions                              ");
        game_instructions_label.setFont(new Font("Times New Roman", Font.BOLD, 40));
        game_instructions_label.setForeground(Color.WHITE);
        game_instructions_label.setHorizontalAlignment(SwingConstants.CENTER); // horizontale Ausrichtung in der Mitte
        gameInstructions = new JTextArea();
        gameInstructions.setText("General & Start\nHome screen: Access game settings and instructions via a hamburger menu.\nMode selection: Choose between singleplayer and multiplayer.\n\nSingleplayer mode\nStarting the game: Choose between loading an old save game or starting a new game.\nConfiguration: When starting a new game, you can set the field size, number/type of ships, and bot difficulty level.\n\nMultiplayer mode\nJoin: Enter the host's IP address and click “Connect”.\nHosting: Open a game via “Host Game” Once a partner is connected, you can create a new game or load an old one.\n\nGameplay\nPlacement phase: Manually position ships with validation check (indicates whether the space is occupied or invalid). Players can also rotate the ship with r and remove it again by clicking on the placed ship.\nBattle phase: Alternate selection of target fields to fire at the enemy fleet.\nSaving: The current progress can be saved at any time during the game.\n\nVictory condition: The player who completely destroys all enemy ships first wins.\n\nShortcuts\nESC: Exit (one Screen)\nEnter: Start (in the ship selection screen), Connect, Confirm Shot.");
        gameInstructions.setLineWrap(true); // dadurch werden Zeilenumbrüche gemacht
        gameInstructions.setWrapStyleWord(true); // dadurch geschieht der Zeilenumbruch nicht mitten im Wort sondern erst am Ende eines Wortes
        gameInstructions.setEditable(false); // Spieler kann den Text nicht verändern
        gameInstructions.setOpaque(false); // dadurch wird der Hintergrund durchsichtig
        gameInstructions.setForeground(Color.WHITE);
        gameInstructions.setFont(new Font("Times New Roman", Font.PLAIN, 17));
        hamburger = new JButton("X");
        hamburger.setFont(new Font("Arial", Font.PLAIN,25));
        hamburger.setForeground(Color.WHITE);
        hamburger.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das X Symbol
        hamburger.setFocusPainted(false); // Entfernt den blauen Rand beim Anklicken
        hamburger.setCursor(new Cursor(Cursor.HAND_CURSOR)); // wenn man drüber geht wird der Cursor geändert
        hamburger.setOpaque(false); // damit die Ecken durchsichtig bleiben
        hamburger.setContentAreaFilled(false); // Damit Java nicht sein Design darein malt
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Reservierung der allerersten Zelle oben links (Spalte 0)
        gbc.gridy = 0; // Reservierung der allerersten Zelle oben links (Zeile 0)
        gbc.weightx = 1.0; // Diese Zelle soll horizontal den gesamten verfügbaren Platz beanspruchen
        gbc.weighty = 0.001; // Diese Zelle soll vertikal 0,1 des gesamten verfügbaren Platz beanspruchen
        gbc.anchor = GridBagConstraints.FIRST_LINE_END; // Die Komponente, die hinzugefügt wird kommt in die obere rechte Ecke
        gbc.insets = new Insets(50, 100, 10, 100); // 50 Pixel Abstand (oben, links, unten, rechts)
        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den gameinstructionscreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
        contentPanel.setLayout(new BorderLayout(0, 10)); // Bereich wird in NORTH, SOUTH, EAST, WEST eingeteilt und vertikal wird immer 10 Pixel abstand gelassen und horizontal 0 Pixel
        contentPanel.add(game_instructions_label, BorderLayout.NORTH);
        contentPanel.add(gameInstructions, BorderLayout.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE; // Der Manager lässt dem Panel seine Standardgröße, die sich aus der Breite des breitesten und der Summe der Höhen aller Inhalte berechnet.
        gbc.anchor = GridBagConstraints.CENTER;
        add(contentPanel, gbc); // das contentPanel wird auf das gameinstructionsscreen-Panel gelegt
        hamburger.addActionListener(exitAction);
    }

    /**
     * Draws the color gradient background of the screen.
     * Method is automatically called by the system when the component needs to be redrawn.
     * @param g the graphics object provided by the system for drawing on
     */
    @Override
    protected void paintComponent(Graphics g) { // Graphics bündelt die notwendigen Werkzeuge und den aktuellen Zeichenzustand(Farbe, Schriftart...) und auf dem Objekt kann man Zeichenbefehle aufrufen
        super.paintComponent(g); // ruft die Basis-Zeichenfunktion auf, also die Logik der Mutterklasse, um einen sauberen Grafik-Kontext für das eigene Zeichnen zu schaffen
        Graphics2D g2d = (Graphics2D) g; // g wird umgewandelt in das Graphics2D Objekt
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Befehl aktiviert die Kantenglättung
        GradientPaint oceanGradient = new GradientPaint(0, 0, frame.colorsheme.color1, 0, getHeight(), frame.colorsheme.color2); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // Dadurch wird gesagt womit gezeichnet wird
        g2d.fillRect(0, 0, getWidth(), getHeight()); // dadurch wird gemalt
    }
}