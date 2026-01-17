package src.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Screen im Singleplayermodus, indem man ein Spiel laden oder ein neues erstellen kann
 * @author Max, Matthias
 */
public class singleplayer extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
    private RoundButton new_game;
    private RoundButton load_game;
    private RoundButton exit;
<<<<<<< HEAD
<<<<<<< HEAD
    private JButton hamburger;
=======
    private JButton hamburgermenue;
>>>>>>> 1181386a5c2d547e31228084eece7c5b032709b5
=======
    private JButton hamburger;
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037

    /**
     * Erstellt den Singleplayerscreen und erstellt und initialisiert Objekte
     * @param frame die Referenz auf das Hauptfenster um später Methoden für den Bildschirmwechsel darauf aufrufen zu können
     */
    public singleplayer(mainframe frame) { // mainframe ist das Hauptfenster und der singleplayerscreen gibt Befehle an den mainframe
        this.frame = frame;
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den singleplayerscreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        JLabel title = new JLabel("Tidebreaker");
        title.setForeground(Color.WHITE);
        new_game = new RoundButton("New Game");
        load_game = new RoundButton("Load Game");
        exit = new RoundButton("Exit");
        title.setFont(new Font("Times New Roman", Font.BOLD,40));
<<<<<<< HEAD
<<<<<<< HEAD
        hamburger = new JButton("≡");
=======
        hamburger = new JButton("\u2261");
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037
        hamburger.setFont(new Font("Times New Roman", Font.BOLD,30));
        hamburger.setForeground(Color.WHITE);
        hamburger.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das ≡ Symbol
        hamburger.setFocusPainted(false); // Entfernt den Rand beim Anklicken
<<<<<<< HEAD
=======
        hamburgermenue = new JButton("\u2261");
        hamburgermenue.setFont(new Font("Times New Roman", Font.BOLD,30));
        hamburgermenue.setForeground(Color.WHITE);
        hamburgermenue.setBorderPainted(false); // Entfernt die Hintergrundfläche des Buttons also man sieht nur noch das ≡ Symbol
        hamburgermenue.setFocusPainted(false); // Entfernt den Rand beim Anklicken
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


        contentPanel.add(title);
        contentPanel.add(new JLabel(""));
        contentPanel.add(new_game);
        contentPanel.add(load_game);
        contentPanel.add(exit);
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das titlescreen-Panel gelegt
        new_game.addActionListener(e -> {frame.showScreen("pregamescreen");});
        load_game.addActionListener(e -> {frame.showScreen("gamescreen");});
        exit.addActionListener(e -> {frame.showScreen("titlescreen");});
<<<<<<< HEAD
<<<<<<< HEAD
        hamburger.addActionListener(e -> {
=======
        hamburgermenue.addActionListener(e -> {
>>>>>>> 1181386a5c2d547e31228084eece7c5b032709b5
=======
        hamburger.addActionListener(e -> {
>>>>>>> fe907c599d28d4c5776405cca23b870b1862f037
                frame.lastscreen = "singleplayer";
                frame.showScreen("settings");
        });
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