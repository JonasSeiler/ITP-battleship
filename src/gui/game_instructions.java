package src.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.KeyStroke; // Ein Objekt, dass einen spezifischen Tastendruck definiert
import javax.swing.AbstractAction; // man kann Aktionen erstellen, welche die Logik enthalten, was passieren soll wenn man den Button drückt oder auf eine spezielle Taste drückt
import java.awt.event.KeyEvent; // Enthält die Namen für alle Tasten (z.B. VK_Escape)
import java.awt.event.ActionEvent; // enthält die Struktur für die Daten, die Java für das Action Event liefern muss, welches Java erwartet für die Methode actionPerformed

/**
 * Screen allowing the user to read the game instructions
 * @author Matthias
 */
public class game_instructions extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
    private JButton hamburger;
    private JLabel game_instructions_label;
    private JTextArea game_instructions;

    /**
     * Creates the start screen and creates and initializes objects
     * @param frame the reference to the main window so that methods for changing screens can be called on it later
     *
     */
    public game_instructions(mainframe frame) { // mainframe ist das Hauptfenster und game_instructions gibt Befehle an den mainframe
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
        game_instructions_label = new JLabel("Game Instructions");
        game_instructions_label.setFont(new Font("Times New Roman", Font.BOLD, 30));
        game_instructions_label.setForeground(Color.WHITE);
        game_instructions = new JTextArea();
        game_instructions.setText("The player can choose between single-player and multiplayer modes. Using the hamburger menu, they can also customize the game's appearance or read the game instructions. In single-player mode, they can play against a bot, while in multiplayer mode, they can play against other people. If the player chooses single-player mode, they can decide whether to start a new game or load an old save file. By clicking on “Load Game,” they can select a previous save file and continue the game from that point. If they click on “New Game,” they can freely choose the field size and ships, but the capacity indicator must be completely filled in order to start the game. When the player presses “Start,” they can place their ships on the field marked “Your Side.” To start the game, the player must have placed all of their ships. The player then presses “Start Game” or “Exit Game” to cancel the game. During the game, the player can attack a field by selecting a field on the “Enemy Side” field and pressing the “Confirm Shot” button. The player who destroys all of their opponent's ships first wins. If they want to save their game score, they can do so by clicking the “Save Game” button. If the user decides to play in multiplayer mode, they can do so by clicking on the “Multiplayer” button on the home screen. There are two options to choose from. First, the player can join a game that someone else has started. The other option is to create a game that another player can join. If the player decides to join a game, they must click on the “Join Game” button and enter the corresponding IP address in the text input field. Finally, they press the “Connect” button.");
        game_instructions.setLineWrap(true); // dadurch werden Zeilenumbrüche gemacht
        game_instructions.setWrapStyleWord(true); // dadurch geschieht der Zeilenumbruch nicht mitten im Wort sondern erst am Ende eines Wortes
        game_instructions.setEditable(false); // Spieler kann den Text nicht verändern
        game_instructions.setOpaque(false); // dadurch wird der Hintergrund durchsichtig
        game_instructions.setForeground(Color.WHITE);
        game_instructions.setFont(new Font("Times New Roman", Font.PLAIN, 17));
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
        gbc.insets = new Insets(50, 50, 50, 50); // 50 Pixel Abstand (oben, links, unten, rechts)

        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den Titlescreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
        contentPanel.setLayout(new BorderLayout(0, 10)); // Bereich wird in NORTH, SOUTH, EAST, WEST eingeteilt und vertikal wird immer 10 Pixel abstand gelassen und horizontal 0 Pixel
        contentPanel.add(game_instructions_label, BorderLayout.NORTH);
        contentPanel.add(game_instructions, BorderLayout.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH; // nimmt den kompletten Platz, den ich bei weightx und weighty reserviert habe
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das titlescreen-Panel gelegt
        hamburger.addActionListener(exitAction);
    }

    /**
     * Method for the color gradient of the screen
     * Method is automatically called by the system when the component needs to be redrawn.
     * @param g The graphics object provided by the system for drawing on
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