package src.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Screen, wodurch der User die Einstellungen verändern kann
 * @author Max, Matthias
 */
public class settingsscreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
<<<<<<< HEAD
<<<<<<< HEAD
    private JButton hamburger;
=======
    private JButton hamburgermenue;
>>>>>>> 1181386a5c2d547e31228084eece7c5b032709b5
=======
    private JButton hamburger;
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037
    private RoundButton changeColor;

    /**
     * Erstellt den Startbildschirm und erstellt und initialisiert Objekte
     * @param frame die Referenz auf das Hauptfenster um später Methoden für den Bildschirmwechsel darauf aufrufen zu können
     *
     */
    public settingsscreen(mainframe frame) { // mainframe ist das Hauptfenster und settingsscreen gibt Befehle an den mainframe
        this.frame = frame;
        this.setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den settingsscreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, eine Spalte und die Abstände sind 10
        
        changeColor = new RoundButton("Change background color");
        // changeColor.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        // changeColor.setForeground(Color.WHITE);
        // changeColor.setBackground(new Color(255,255,255,50));
        // changeColor.setBorderPainted(false); //  Entfernt die Hintergrundfläche des Buttons also man sieht nur noch die Schrift
        // changeColor.setCursor(new Cursor(Cursor.HAND_CURSOR)); // wenn man drüber geht wird der Cursor geändert

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037
        hamburger = new JButton("X");
        hamburger.setFont(new Font("Arial", Font.PLAIN,25));
        hamburger.setForeground(Color.WHITE);
        hamburger.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das X Symbol
        hamburger.setFocusPainted(false); // Entfernt den blauen Rand beim Anklicken
        hamburger.setCursor(new Cursor(Cursor.HAND_CURSOR)); // wenn man drüber geht wird der Cursor geändert
<<<<<<< HEAD
=======
        hamburgermenue = new JButton("X");
        hamburgermenue.setFont(new Font("Arial", Font.PLAIN,25));
        hamburgermenue.setForeground(Color.WHITE);
        hamburgermenue.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das X Symbol
        hamburgermenue.setFocusPainted(false); // Entfernt den blauen Rand beim Anklicken
        hamburgermenue.setCursor(new Cursor(Cursor.HAND_CURSOR)); // wenn man drüber geht wird der Cursor geändert
>>>>>>> 1181386a5c2d547e31228084eece7c5b032709b5
=======
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Reservierung der allerersten Zelle oben links (Spalte 0)
        gbc.gridy = 0; // Reservierung der allerersten Zelle oben links (Zeile 0)
        gbc.weightx = 1.0; // Diese Zelle soll horizontal den gesamten verfügbaren Platz beanspruchen
        gbc.weighty = 0.1; // Diese Zelle soll vertikal 0,1 des gesamten verfügbaren Platz beanspruchen
        gbc.anchor = GridBagConstraints.FIRST_LINE_END; // Die Komponente, die hinzugefügt wird kommt in die obere rechte Ecke
        gbc.insets = new Insets(50, 50, 50, 50); // 50 Pixel Abstand (oben, links, unten, rechts)
<<<<<<< HEAD
<<<<<<< HEAD
        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den Titlescreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
=======
        add(hamburgermenue, gbc); // Packe den Button mit dieser Bauanleitung auf den Titlescreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
>>>>>>> 1181386a5c2d547e31228084eece7c5b032709b5
=======
        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den Titlescreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037

        contentPanel.add(changeColor);
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das titlescreen-Panel gelegt

<<<<<<< HEAD
<<<<<<< HEAD
        hamburger.addActionListener(e -> {frame.showScreen(frame.lastscreen);});
=======
        hamburgermenue.addActionListener(e -> {frame.showScreen(frame.lastscreen);});
>>>>>>> 1181386a5c2d547e31228084eece7c5b032709b5
=======
        hamburger.addActionListener(e -> {frame.showScreen(frame.lastscreen);});
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037
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
        GradientPaint oceanGradient = new GradientPaint(0, 0, frame.color1, 0, getHeight(), frame.color2); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // Dadurch wird gesagt womit gezeichnet wird
        g2d.fillRect(0, 0, getWidth(), getHeight()); // dadurch wird gemalt. Festlegung wo und wie groß der Bereich ist, der gefüllt werden soll mit getWidth(),getHeight() bekomme ich die Breite und Höhe vom singleplayerobjekt
    }
}