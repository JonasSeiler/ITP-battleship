package src.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.KeyStroke; // Ein Objekt, dass einen spezifischen Tastendruck definiert
import javax.swing.AbstractAction; // man kann Aktionen erstellen, welche die Logik enthalten, was passieren soll wenn man den Button drückt oder auf eine spezielle Taste drückt
import java.awt.event.KeyEvent; // Enthält die Namen für alle Tasten (z.B. VK_Escape)
import java.awt.event.ActionEvent; // enthält die Struktur für die Daten, die Java für das Action Event liefern muss, welches Java erwartet für die Methode actionPerformed

/**
 * Screen for adjusting game settings in singleplayer mode.
 * Determining the number of ships and the size of the playing field.
 * @author Matthias Wiese
 */
public class pregamescreen2 extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private JSpinner ship_size2; // unterschiedliche hoch und runter klickbarer Button für die Anzahl der jeweiligen Schiffe
    private JSpinner ship_size3;
    private JSpinner ship_size4;
    private JSpinner ship_size5;
    private JProgressBar capacityBar; // Balken der Anzeigen soll wie viele Platz man noch mit Schiffen belegen kann
    private RoundButton start_button;
    private RoundButton zurueck_button;
    private JButton hamburger;
    private JSpinner gridSize1; // Dekleration des hoch und runter klickbaren Buttons
    private JRadioButton easy;
    private JRadioButton medium;
    private JRadioButton hard;
    /**
     * The size of the playing field grid.
     */
    public int gridSize; // wird für den GameScreen gebraucht
    /**
     * The array containing the size of the selected ships.
     */
    public int[] ships; // // wird für den GameScreen gebraucht
    private Mainframe frame; // Referenz auf das Hauptfenster

    /**
     * Creates the screen for the game settings and initializes buttons and also a capacity bar.
     * @param frame the reference to the main window used for screen transitions
     */
    public pregamescreen2(Mainframe frame) { // mainframe ist das Hauptfenster und pregamescreen2 gibt Befehle an den mainframe
        this.frame = frame;
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den pregamescreen2 gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing

        AbstractAction exitAction = new AbstractAction() { // Objekt welches die Logik für eine Aktion definiert
            @Override
            public void actionPerformed(ActionEvent e) { // Methode des Objekts wird überschrieben mit der Logik
                frame.showScreen(frame.lastscreen2);
            }
        };
        KeyStroke exitTaste = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0); // erstellt ein fertiges KeyStroke Objekt mit dem Kriterum, dass es die esc Taste speichert
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitTaste, "exit"); // überwacht, ob die Exit Taste gedrückt wurde
        this.getActionMap().put("exit", exitAction); // führt die Aktion aus, wenn die InputMap die Taste erkannt hat

        AbstractAction startAction = new AbstractAction() { // Objekt welches die Logik für eine Aktion definiert
            @Override
            public void actionPerformed(ActionEvent e) { // Methode des Objekts wird überschrieben mit der Logik
                int occupied = (Integer) capacityBar.getValue();
                int max = (Integer) capacityBar.getMaximum();
                if (max == occupied) {
                    start();
                    start2();
                    frame.startGamescreen();
                }
            }
        };
        KeyStroke startTaste = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0); // erstellt ein fertiges KeyStroke Objekt mit dem Kriterum, dass es die Enter Taste speichert
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(startTaste, "start"); // überwacht, ob die Start Taste gedrückt wurde
        this.getActionMap().put("start", startAction); // führt die Aktion aus, wenn die InputMap die Taste erkannt hat

        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,2,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        capacityBar = new JProgressBar(0, 100); // 0 = min, 100 = max in Prozent wahrscheinlich
        capacityBar.setValue(100); // Der Balken ist standardmäßig voll, weil es eine Vorauswahl an Schiffen gibt
        capacityBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI()); // verbietet dem Betriebssystem sich in das Design einzumischen, man nimmt die Original JProgressBar
        capacityBar.setForeground(new Color(0,191,255)); // Der Fortschrittsbalken ist in einem "Deep Sky Blue". Es wird ein neues Color Objekt mit diesen Werten erstellt, was diese Farbe ist
        capacityBar.setBackground(new Color(20,30,50)); // Der noch zu füllende Bereich ist in einem sehr dunklen Blau
        Font font = new Font("Times New Roman", Font.PLAIN, 16);
        capacityBar.setFont(font);
        capacityBar.setBorder(BorderFactory.createLineBorder(new Color(20,30,50),3,true)); // es wird ein Rahmen um die capacityBar gelegt der außen ein ganz bisschen abgerundet ist
        SpinnerNumberModel mapSizeModel = new SpinnerNumberModel(7, 5, 30, 1); // legt die Logik fest also startet bei 10 usw.
        SpinnerNumberModel shipSizeModel5 = new SpinnerNumberModel(1,0,6,1);
        SpinnerNumberModel shipSizeModel4 = new SpinnerNumberModel(1,0,7,1);
        SpinnerNumberModel shipSizeModel3 = new SpinnerNumberModel(1,0,10,1);
        SpinnerNumberModel shipSizeModel2 = new SpinnerNumberModel(1,0,15,1); // jeder Button braucht seine eigene Logik sonst springt der Button den man nicht ausgewählt hat auch höher, wenn man die Logik mehreren Button gibt
        start_button = new RoundButton("Start"); // neuer Button mit Text im Button
        JPanel difficulty = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // ordnet alles nebeneinander an
        difficulty.setOpaque(false);
        easy = new JRadioButton("Easy");
        medium = new JRadioButton("Medium", true); // der Punkt wird mit true gesetzt
        hard = new JRadioButton("Hard");
        easy.setContentAreaFilled(false);
        medium.setContentAreaFilled(false);
        hard.setContentAreaFilled(false);
        easy.setOpaque(false);
        medium.setOpaque(false);
        hard.setOpaque(false);
        ButtonGroup difficulty_group = new ButtonGroup(); // dadurch kann immer nur ein JRadioButton gleichzeitig ausgewählt sein
        difficulty_group.add(easy);
        difficulty_group.add(medium);
        difficulty_group.add(hard);
        easy.setFont(font);
        medium.setFont(font);
        hard.setFont(font);
        easy.setForeground(Color.WHITE);
        medium.setForeground(Color.WHITE);
        hard.setForeground(Color.WHITE);
        zurueck_button = new RoundButton("Exit");
        gridSize1 = new JSpinner(mapSizeModel); // Erstellt den Button wo man draufklicken kann
        ship_size5 = new JSpinner(shipSizeModel5);
        ship_size4 = new JSpinner(shipSizeModel4);
        ship_size3 = new JSpinner(shipSizeModel3);
        ship_size2 = new JSpinner(shipSizeModel2);
        JLabel difficulty_label = new JLabel("Difficulty (Bot)");
        JLabel BarLabel = new JLabel("Available Space");
        JLabel sizeLabel = new JLabel("Field Size"); // Textfeld
        JLabel shipSizeLabel5 = new JLabel("Aircraft Carrier (Size 5)");
        JLabel shipSizeLabel4 = new JLabel("Battleship (Size 4)");
        JLabel shipSizeLabel3 = new JLabel("Submarine (Size 3)");
        JLabel shipSizeLabel2 = new JLabel("Destroyer (Size 2)");
        difficulty_label.setFont(new Font("Times New Roman", Font.BOLD, 16));
        BarLabel.setFont(new Font("Times New Roman",Font.BOLD,16));
        sizeLabel.setFont(new Font("Times New Roman", Font.BOLD, 16)); // Schriftart-Objekt wird erstellt und in Schriftart Times New Roman fett und in größe 16
        shipSizeLabel5.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shipSizeLabel4.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shipSizeLabel3.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shipSizeLabel2.setFont(new Font("Times New Roman", Font.BOLD, 16));
        difficulty_label.setForeground(Color.WHITE);
        BarLabel.setForeground(Color.WHITE);
        sizeLabel.setForeground(Color.WHITE); // Textfarbe ist weiß
        shipSizeLabel5.setForeground(Color.WHITE);
        shipSizeLabel4.setForeground(Color.WHITE);
        shipSizeLabel3.setForeground(Color.WHITE);
        shipSizeLabel2.setForeground(Color.WHITE);
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
        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den pregamescreen2 aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
        difficulty.add(easy);
        difficulty.add(medium);
        difficulty.add(hard);
        contentPanel.add(difficulty_label);
        contentPanel.add(difficulty);
        contentPanel.add(BarLabel);
        contentPanel.add(capacityBar);
        contentPanel.add(sizeLabel); // fügt die Objekte auf die innere Leinwand Schritt für Schritt also erst das erste, dann das zweite...
        contentPanel.add(gridSize1);
        contentPanel.add(shipSizeLabel5);
        contentPanel.add(ship_size5);
        contentPanel.add(shipSizeLabel4);
        contentPanel.add(ship_size4);
        contentPanel.add(shipSizeLabel3);
        contentPanel.add(ship_size3);
        contentPanel.add(shipSizeLabel2);
        contentPanel.add(ship_size2);
        contentPanel.add(zurueck_button);
        contentPanel.add(start_button);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das pregamescreen2-Panel gelegt
        gridSize1.addChangeListener(e -> {updateCapacity();}); // wenn der Button verändert wird, wird updateCapacity ausgeführt
        ship_size5.addChangeListener(e -> {updateCapacity();});
        ship_size4.addChangeListener(e -> {updateCapacity();});
        ship_size3.addChangeListener(e -> {updateCapacity();});
        ship_size2.addChangeListener(e -> {updateCapacity();});
        start_button.addChangeListener(e -> {updateCapacity();});
        zurueck_button.addActionListener(exitAction);
        start_button.addActionListener(startAction);
        hamburger.addActionListener(e -> {
                frame.lastscreen = "pregamescreen2";
                frame.showScreen("settings");
        });
        updateCapacity(); // Zum Start wird die Anzeige auf den aktuellen Stand gebracht
    }

    /**
     * Stores the selected settings when the start button is pressed.
     * They are stored in an array and integer.
     */
    private void start() {
        int shipSize5 = (Integer) ship_size5.getValue();
        int shipSize4 = (Integer) ship_size4.getValue();
        int shipSize3 = (Integer) ship_size3.getValue();
        int shipSize2 = (Integer) ship_size2.getValue();
        int total = shipSize5 + shipSize4 + shipSize3 + shipSize2;
        ships = new int[total];
        int shipSizes4_5 = shipSize4 + shipSize5;
        int shipSizes3_4_5 = shipSize3 + shipSize4 + shipSize5;
        for (int i = 0; i < shipSize5; i++) {
            ships[i] = 5;
        }
        for (int i = shipSize5; i < shipSizes4_5; i++) {
            ships[i] = 4;
        }
        for (int i = shipSizes4_5; i < shipSizes3_4_5; i++) {
            ships[i] = 3;
        }
        for (int i = shipSizes3_4_5; i < total; i++) {
            ships[i] = 2;
        }
        gridSize = (Integer) gridSize1.getValue();
    }

    /**
     * Writes the selected difficulty level of the bot to a variable in the mainframe.
     */
    public void start2() {
        if (easy.isSelected()) {
            frame.difficulty = 1;
        } else if (medium.isSelected()) {
            frame.difficulty = 2;
        } else if (hard.isSelected()) {
            frame.difficulty = 3;
        }
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

    /**
     * Updates the component settings and calculates the remaining ship capacity.
     * It calculates how much space you can still occupy and whether you can currently add another ship with size x.
     * In addition, the capacity display is updated.
     */
    private void updateCapacity() {
        int gridSize = (Integer) gridSize1.getValue(); // Wert des eingestellten gridSize Buttons wird gespeichert
        int max = (int) (gridSize * gridSize * 0.3); // maximale Flächenfelder die mit Schiffen belegt werden darf wird berechnet. Man muss Cast (Integer) machen, weil man ein Objekt zurück bekommt und man muss sagen, was es ist, in diesem Fall ein Integer
        int shipSize5 = (Integer) ship_size5.getValue(); // aktueller Wert der gerade in dem JSpinner drin steht wird in einem int gespeichert
        int shipSize4 = (Integer) ship_size4.getValue();
        int shipSize3 = (Integer) ship_size3.getValue();
        int shipSize2 = (Integer) ship_size2.getValue();
        SpinnerNumberModel model5 = (SpinnerNumberModel) ship_size5.getModel(); // Mit model5 ändert man das ursprüngliche Objekt
        SpinnerNumberModel model4 = (SpinnerNumberModel) ship_size4.getModel();
        SpinnerNumberModel model3 = (SpinnerNumberModel) ship_size3.getModel();
        SpinnerNumberModel model2 = (SpinnerNumberModel) ship_size2.getModel();
        int occupied = 5 * shipSize5 + 4 * shipSize4 + 3 * shipSize3 + 2 * shipSize2;
        if (max < occupied) { // wenn man die GridSize runterstellt und die Felder der belegten Schiffe größer ist als man eigentlich darf, wird der Value von allen Schiffen auf 0 gesetzt
            model5.setValue(0);
            model4.setValue(0);
            model3.setValue(0);
            model2.setValue(0);
            occupied = 0;
            shipSize5 = 0;
            shipSize4 = 0;
            shipSize3 = 0;
            shipSize2 = 0;
        }
        int free = max - occupied;
        int freeShipSize5 = (int) (free / 5) + shipSize5; // Berechnet wie viele 5er Schiffe bei der Größe der map aktuell noch ausgewählt werden dürften
        int freeShipSize4 = (int) (free / 4) + shipSize4;
        int freeShipSize3 = (int) (free / 3) + shipSize3;
        int freeShipSize2 = (int) (free / 2) + shipSize2;
        capacityBar.setMaximum(max); // der Wert was 100% bei der Anzeige ist
        capacityBar.setValue(occupied); // der Wert der gerade belegt ist also der aktuell gefüllte Balken
        occupied = (Integer) capacityBar.getValue(); // Man bekommt ein Objekt zurück und man muss sagen, dass es sich dabei um einen Integer handelt
        max = (Integer) capacityBar.getMaximum();
        if (occupied < max) {
            capacityBar.setString(occupied + "/" + max);
        } else {
            capacityBar.setString("All Fields Occupied");
        }
        capacityBar.setStringPainted(true); // der Text wird in die Bar geschrieben, der in setString gespeichert ist
        model5.setMaximum(freeShipSize5);
        model4.setMaximum(freeShipSize4);
        model3.setMaximum(freeShipSize3);
        model2.setMaximum(freeShipSize2);
    }
}
