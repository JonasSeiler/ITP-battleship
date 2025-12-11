import javax.swing.*; // Importiert die gesamte Swing Bibliothek, moderne und plattformunabhängige GUI-Komponenten
import java.awt.*; // Importiert das Abstract Window Toolkit (AWT) liefert grundlegende Grafik- und Farbobjekte und einige Layout Manager
import javax.swing.JButton; // aus der Java Swing Bibliothek wird die JButton Klasse bekannt und verfügbar gemacht

public class pregamescreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private JSpinner gridSizeSpinner; // Dekleration des hoch und runter klickbaren Buttons
    private JSpinner ship_size2; // unterschiedliche hoch und runter klickbarer Button für die Anzahl der jeweiligen Schiffe
    private JSpinner ship_size3;
    private JSpinner ship_size4;
    private JSpinner ship_size5;
    private JProgressBar capacityBar; // Balken der Anzeigen soll wie viele Platz man noch mit Schiffen belegen kann
    private JButton start_button;
    private mainframe frame; // Referenz auf das Hauptfenster

    public pregamescreen(mainframe frame) { // mainframe ist das Hauptfenster und pregame gibt Befehle an den mainframe
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden
        setOpaque(false); // Erlaubt der paintComponent-Methode den Hintergrund zu zeichnen
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,2,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        capacityBar = new JProgressBar(0, 100); // 0 = min, 100 = max in Prozent wahrscheinlich
        capacityBar.setValue(100); // Der Balken ist standardmäßig voll, weil es eine Vorauswahl an Schiffen gibt
        capacityBar.setStringPainted(true); // Zeigt %-Zahl als Text im Balken an
        SpinnerNumberModel mapSizeModel = new SpinnerNumberModel(7, 5, 15, 1); // legt die Logik fest also startet bei 10 usw.
        SpinnerNumberModel shipSizeModel5 = new SpinnerNumberModel(1,0,6,1);
        SpinnerNumberModel shipSizeModel4 = new SpinnerNumberModel(1,0,7,1);
        SpinnerNumberModel shipSizeModel3 = new SpinnerNumberModel(1,0,10,1);
        SpinnerNumberModel shipSizeModel2 = new SpinnerNumberModel(1,0,15,1); // jeder Button braucht seine eigene Logik sonst springt der Button den man nicht ausgewählt hat auch höher, wenn man die Logik mehreren Button gibt
        start_button = new JButton("Start"); // neuer Button mit Text im Button
        start_button.setBackground(Color.GREEN); // Hintergrund grün
        start_button.setForeground(Color.BLACK); // Schrift weiß
        start_button.setFont(new Font("SansSerif", Font.BOLD,16)); // Schriftart
        start_button.setOpaque(true); // Sonst sieht man die Farbe auf dem Mac oft nicht
        start_button.setBorderPainted(false); // nimmt den 3D-Rahmen weg für ein flaches Design
        gridSizeSpinner = new JSpinner(mapSizeModel); // Erstellt den Button wo man draufklicken kann
        gridSizeSpinner.setPreferredSize(new Dimension(100, 30)); // legt die größe fest in Pixel
        ship_size5 = new JSpinner(shipSizeModel5);
        ship_size4 = new JSpinner(shipSizeModel4);
        ship_size3 = new JSpinner(shipSizeModel3);
        ship_size2 = new JSpinner(shipSizeModel2);
        JLabel BarLabel = new JLabel("verfügbarer Platz");
        JLabel sizeLabel = new JLabel("Spielfeldgröße"); // Textfeld
        JLabel shipSizeLabel5 = new JLabel("Flugzeugträger (Größe 5)");
        JLabel shipSizeLabel4 = new JLabel("Schlachtschiff (Größe 4)");
        JLabel shipSizeLabel3 = new JLabel("U-Boot (Größe 3)");
        JLabel shipSizeLabel2 = new JLabel("Zerstörer (Größe 2)");
        BarLabel.setFont(new Font("SansSerif",Font.BOLD,16));
        sizeLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // Schriftart-Objekt wird erstellt und in Schriftart Sans..., fett und in größe 16
        shipSizeLabel5.setFont(new Font("SansSerif", Font.BOLD, 16));
        shipSizeLabel4.setFont(new Font("SansSerif", Font.BOLD, 16));
        shipSizeLabel3.setFont(new Font("SansSerif", Font.BOLD, 16));
        shipSizeLabel2.setFont(new Font("SansSerif", Font.BOLD, 16));
        BarLabel.setForeground(Color.WHITE);
        sizeLabel.setForeground(Color.WHITE); // Textfarbe ist weiß
        shipSizeLabel5.setForeground(Color.WHITE);
        shipSizeLabel4.setForeground(Color.WHITE);
        shipSizeLabel3.setForeground(Color.WHITE);
        shipSizeLabel2.setForeground(Color.WHITE);
        contentPanel.add(BarLabel);
        contentPanel.add(capacityBar);
        contentPanel.add(sizeLabel); // fügt die Objekte auf die innere Leinwand Schritt für Schritt also erst das erste, dann das zweite...
        contentPanel.add(gridSizeSpinner);
        contentPanel.add(shipSizeLabel5);
        contentPanel.add(ship_size5);
        contentPanel.add(shipSizeLabel4);
        contentPanel.add(ship_size4);
        contentPanel.add(shipSizeLabel3);
        contentPanel.add(ship_size3);
        contentPanel.add(shipSizeLabel2);
        contentPanel.add(ship_size2);
        contentPanel.add(new JLabel(""));
        contentPanel.add(start_button);
        gridSizeSpinner.addChangeListener(e -> {updateCapacity();}); // wenn der Button verändert wird, wird updateCapacity ausgeführt
        ship_size5.addChangeListener(e -> {updateCapacity();});
        ship_size4.addChangeListener(e -> {updateCapacity();});
        ship_size3.addChangeListener(e -> {updateCapacity();});
        ship_size2.addChangeListener(e -> {updateCapacity();});
        start_button.addActionListener(e -> {frame.showScreen("gamescreen");}); // ActionListener, weil dieser dafür konzipiert ist, eine spezifische, einmalige Handlung zu erfassen
        add(contentPanel); // das contentPanel wird auf das pregame-Panel gelegt
        updateCapacity(); // Zum Start wird die Anzeige auf den aktuellen Stand gebracht
    }

    @Override
    protected void paintComponent(Graphics g) { // Graphics bündelt die notwendigen Werkzeuge und den aktuellen Zeichenzustand(Farbe, Schriftart...) und auf dem Objekt kann man Zeichenbefehle aufrufen
        super.paintComponent(g); // alte Inhalt der Komponente wird gelöscht
        Graphics2D g2d = (Graphics2D) g; // g wird umgewandelt in das Graphics2D Objekt
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Befehl aktiviert die Kantenglättung
        GradientPaint oceanGradient = new GradientPaint(0, 0, new Color(20, 30, 50), 0, getHeight(), new Color(0, 100, 160)); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // der oceanGradient Farbverlauf soll für nachfolgende Füllbefehle verwendet werden
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Festlegung wo und wie groß der Bereich ist, der gefüllt werden soll mit getWidth(),getHeight() bekomme ich die Breite und Höhe
    }
    private void updateCapacity() {
        int gridSize = (Integer) gridSizeSpinner.getValue(); // Wert des eingestellten gridSize Buttons wird gespeichert
        int max = (int) (gridSize * gridSize * 0.3); // maximale Flächenfelder die mit Schiffen belegt werden darf wird berechnet. Man muss Cast (Integer) machen, weil man ein Objekt zurück bekommt und man muss sagen, was es ist, in diesem Fall ein Integer
        int shipSize5 = (Integer) ship_size5.getValue();
        int shipSize4 = (Integer) ship_size4.getValue();
        int shipSize3 = (Integer) ship_size3.getValue();
        int shipSize2 = (Integer) ship_size2.getValue();
        SpinnerNumberModel model5 = (SpinnerNumberModel) ship_size5.getModel();
        SpinnerNumberModel model4 = (SpinnerNumberModel) ship_size4.getModel();
        SpinnerNumberModel model3 = (SpinnerNumberModel) ship_size3.getModel();
        SpinnerNumberModel model2 = (SpinnerNumberModel) ship_size2.getModel();
        int occupied = 5 * shipSize5 + 4 * shipSize4 + 3 * shipSize3 + 2 * shipSize2;
        if (max < occupied) { // wenn man die GridSize runterstellt und die Felder der belegten Schiffe größer ist als man eigentlich darf, wird der Value von allen Schiffen auf 0 gesetzt
            model5.setValue(0);
            model4.setValue(0);
            model3.setValue(0);
            model2.setValue(0);
        }
        int free = max - occupied;
        int freeShipSize5 = (int) (free / 5) + shipSize5; // Berechnet wie viele 5er Schiffe bei der Größe der map aktuell noch ausgewählt werden dürften
        int freeShipSize4 = (int) (free / 4) + shipSize4;
        int freeShipSize3 = (int) (free / 3) + shipSize3;
        int freeShipSize2 = (int) (free / 2) + shipSize2;
        capacityBar.setMaximum(max); // der Wert was 100% bei der Anzeige ist
        capacityBar.setValue(occupied); // der Wert der gerade belegt ist also der aktuell gefüllte Balken
        model5.setMaximum(freeShipSize5);
        model4.setMaximum(freeShipSize4);
        model3.setMaximum(freeShipSize3);
        model2.setMaximum(freeShipSize2);
    }
}