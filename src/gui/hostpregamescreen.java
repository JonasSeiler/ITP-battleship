package src.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Screen um die Spieleinstellungen als Host zu treffen
 * Festlegung der Anzahl der Schiffe und der Spielfeldgröße
 * @author Matthias
 */
public class hostpregamescreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private JSpinner ship_size2; // unterschiedliche hoch und runter klickbarer Button für die Anzahl der jeweiligen Schiffe
    private JSpinner ship_size3;
    private JSpinner ship_size4;
    private JSpinner ship_size5;
    private JProgressBar capacityBar; // Balken der Anzeigen soll wie viel Platz man noch mit Schiffen belegen kann
    private JButton start_button;
    private JButton zurueck_button;
    private JSpinner gridSize1; // Dekleration des hoch und runter klickbaren Buttons
    public int gridSize; // wird für den GameScreen gebraucht
    public int[] ships; // // wird für den GameScreen gebraucht
    private mainframe frame; // Referenz auf das Hauptfenster

    /**
     * Erstellt den Screen, um Spieleinstellungen als Host zu treffen und es werden Objekte erstellt und initialisiert
     * @param frame die Referenz auf das Hauptfenster um später Methoden für den Bildschirmwechsel darauf aufrufen zu können
     */
    public hostpregamescreen(mainframe frame) { // mainframe ist das Hauptfenster und hostpregamescreen gibt Befehle an den mainframe
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den hostpregamescreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,2,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        capacityBar = new JProgressBar(0, 100); // 0 = min, 100 = max in Prozent wahrscheinlich
        capacityBar.setValue(100); // Der Balken ist standardmäßig voll, weil es eine Vorauswahl an Schiffen gibt
        capacityBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI()); // verbietet dem Betriebssystem sich in das Design einzumischen, man nimmt die Original JProgressBar
        capacityBar.setForeground(new Color(0,191,255)); // Der Fortschrittsbalken ist in einem "Deep Sky Blue". Es wird ein neues Color Objekt mit diesen Werten erstellt, was diese Farbe ist
        capacityBar.setBackground(new Color(20,30,50)); // Der noch zu füllende Bereich ist in einem sehr dunklen Blau
        capacityBar.setFont(new Font("Times New Roman",Font.PLAIN,16));
        capacityBar.setBorder(BorderFactory.createLineBorder(new Color(20,30,50),3,true)); // es wird ein Rahmen um die capacityBar gelegt der außen ein ganz bisschen abgerundet ist
        SpinnerNumberModel mapSizeModel = new SpinnerNumberModel(7, 5, 30, 1); // legt die Logik fest also startet bei 10 usw.
        SpinnerNumberModel shipSizeModel5 = new SpinnerNumberModel(1,0,6,1);
        SpinnerNumberModel shipSizeModel4 = new SpinnerNumberModel(1,0,7,1);
        SpinnerNumberModel shipSizeModel3 = new SpinnerNumberModel(1,0,10,1);
        SpinnerNumberModel shipSizeModel2 = new SpinnerNumberModel(1,0,15,1); // jeder Button braucht seine eigene Logik sonst springt der Button den man nicht ausgewählt hat auch höher, wenn man die Logik mehreren Button gibt
        start_button = new JButton("Start"); // neuer Button mit Text im Button
        start_button.setBackground(Color.GREEN); // Hintergrund grün
        start_button.setForeground(Color.BLACK); // Schrift weiß
        start_button.setFont(new Font("Times New Roman", Font.PLAIN,16)); // Schriftart
        start_button.setOpaque(true); // Sonst sieht man die Farbe auf dem Mac oft nicht
        start_button.setBorderPainted(false); // nimmt den 3D-Rahmen weg für ein flaches Design
        zurueck_button = new JButton("   <-   ");
        zurueck_button.setForeground(Color.BLACK);
        zurueck_button.setFont(new Font("Times New Roman", Font.BOLD,22));
        gridSize1 = new JSpinner(mapSizeModel); // Erstellt den Button wo man draufklicken kann
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
        BarLabel.setFont(new Font("Times New Roman",Font.BOLD,16));
        sizeLabel.setFont(new Font("Times New Roman", Font.BOLD, 16)); // Schriftart-Objekt wird erstellt und in Schriftart Times New Roman, fett und in größe 16
        shipSizeLabel5.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shipSizeLabel4.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shipSizeLabel3.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shipSizeLabel2.setFont(new Font("Times New Roman", Font.BOLD, 16));
        BarLabel.setForeground(Color.WHITE);
        sizeLabel.setForeground(Color.WHITE); // Textfarbe ist weiß
        shipSizeLabel5.setForeground(Color.WHITE);
        shipSizeLabel4.setForeground(Color.WHITE);
        shipSizeLabel3.setForeground(Color.WHITE);
        shipSizeLabel2.setForeground(Color.WHITE);
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
        gridSize1.addChangeListener(e -> {updateCapacity();}); // wenn der Button verändert wird, wird updateCapacity ausgeführt
        ship_size5.addChangeListener(e -> {updateCapacity();});
        ship_size4.addChangeListener(e -> {updateCapacity();});
        ship_size3.addChangeListener(e -> {updateCapacity();});
        ship_size2.addChangeListener(e -> {updateCapacity();});
        start_button.addChangeListener(e -> {updateCapacity();});
        zurueck_button.addActionListener(e -> {frame.showScreen("hostscreen");});
        start_button.addActionListener(e -> { // Der ActionListener ist ein Objekt der als Zuhörer am Button klebt und eine Methode mit dem Parameter e besitzen muss, um die Klick-Details zu empfangen und daraufhin wird der Code in den {} ausführt
            int occupied = (Integer) capacityBar.getValue();
            int max = (Integer) capacityBar.getMaximum();
            if (max == occupied) {
                start();
                frame.startGamescreen_host();
            }}); // ActionListener, weil dieser dafür konzipiert ist, eine spezifische, einmalige Handlung zu erfassen
        add(contentPanel); // das contentPanel wird auf das hostpregamescreen-Panel gelegt
        updateCapacity(); // Zum Start wird die Anzeige auf den aktuellen Stand gebracht
    }

    /**
     * Methode die aufgerufen wird, wenn man auf den Start Button drückt
     * in der Methode werden die gewählten Einstellungen in einem Array und int gespeichert
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
     * Methode für den Farbverlauf des Screens
     * Methode wird automatisch vom System aufgerufen, wenn die Komponente neu gezeichnet werden muss
     * @param g Das Grafik-Objekt, das vom System bereitgestellt wird, um darauf zu zeichnen
     */
    @Override
    protected void paintComponent(Graphics g) { // Graphics bündelt die notwendigen Werkzeuge und den aktuellen Zeichenzustand(Farbe, Schriftart...) und auf dem Objekt kann man Zeichenbefehle aufrufen
        super.paintComponent(g); // ruft die Basis-Zeichenfunktion auf, also die Logik der Mutterklasse, um einen sauberen Grafik-Kontext für das eigene Zeichnen zu schaffen
        Graphics2D g2d = (Graphics2D) g; // g wird umgewandelt in das Graphics2D Objekt
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Befehl aktiviert die Kantenglättung
        GradientPaint oceanGradient = new GradientPaint(0, 0, new Color(20, 30, 50), 0, getHeight(), new Color(0, 100, 160)); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // der oceanGradient Farbverlauf soll für nachfolgende Füllbefehle verwendet werden
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Festlegung wo und wie groß der Bereich ist, der gefüllt werden soll mit getWidth(),getHeight() bekomme ich die Breite und Höhe vom Titlescreenobjekt
    }

    /**
     * Methode die aufgerufen wird, wenn eine Spieleinstellung verändert worden ist
     * Es wird berechnet wie viel Platz man noch belegen darf und ob man noch ein Schiff mit größe x aktuell hinzufügen kann
     * Zudem wird die Kapazitätsanzeige aktualisiert
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
            capacityBar.setString("Alle Felder belegt");
        }
        capacityBar.setStringPainted(true); // der Text wird in die Bar geschrieben, der in setString gespeichert ist
        model5.setMaximum(freeShipSize5);
        model4.setMaximum(freeShipSize4);
        model3.setMaximum(freeShipSize3);
        model2.setMaximum(freeShipSize2);
        if (occupied == max) {
            start_button.setBackground(Color.GREEN); // Hintergrund grün
            start_button.setForeground(Color.BLACK); // Schrift weiß
            start_button.setFont(new Font("Times New Roman", Font.PLAIN,16)); // Schriftart
            start_button.setOpaque(true); // Sonst sieht man die Farbe auf dem Mac oft nicht
            start_button.setBorderPainted(false); // nimmt den 3D-Rahmen weg für ein flaches Design
        } else {
            start_button.setBackground(Color.GRAY); // Hintergrund grün
            start_button.setForeground(Color.BLACK); // Schrift weiß
            start_button.setFont(new Font("Times New Roman", Font.PLAIN,16)); // Schriftart
            start_button.setOpaque(true); // Sonst sieht man die Farbe auf dem Mac oft nicht
            start_button.setBorderPainted(false); // nimmt den 3D-Rahmen weg für ein flaches Design
        }
    }
}