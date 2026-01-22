package src.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Screen im Multiplayermodus, bei dem man einem Spiel joinen kann
 * @author Max, Matthias
 */
public class waitingscreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
    private RoundButton exit;
    private JButton hamburger;

    /**
     * Erstellt den Screen um einem Spiel zu joinen und erstellt und initialisiert Objekte
     * @param frame die Referenz auf das Hauptfenster um später Methoden für den Bildschirmwechsel darauf aufrufen zu können
     */
    public waitingscreen(mainframe frame) { // mainframe ist das Hauptfenster und der waitingscreen gibt Befehle an den mainframe
        this.frame = frame;
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den waitingscreen gepackt
        setOpaque(false); // Deaktiviert die automatische Hintergrundfüllung von Swing
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        JLabel title = new JLabel("Tidebreaker");
        JLabel waitingscreen = new JLabel("waiting for connection...");
        title.setForeground(Color.WHITE);
        exit = new RoundButton("Exit");
        title.setFont(new Font("Times New Roman", Font.BOLD,40));
        // exit.setFont(new Font("Times New Roman", Font.BOLD,35));
        waitingscreen.setFont(new Font("Times New Roman", Font.PLAIN,20));
        waitingscreen.setForeground(Color.WHITE);


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

        add(hamburger, gbc); // Packe den Button mit dieser Bauanleitung auf den Titlescreen aber es wird das GridBagLayout vom Anfang genommen und gbc aber berücksichtigt

        contentPanel.add(title);
        contentPanel.add(new JLabel(""));
        contentPanel.add(waitingscreen);
        contentPanel.add(new JLabel(""));
        contentPanel.add(exit);
        gbc.gridy = 1;
        gbc.weighty = 0.999;
        gbc.anchor = GridBagConstraints.NORTH;
        add(contentPanel, gbc); // das contentPanel wird auf das titlescreen-Panel gelegt
        exit.addActionListener(e -> {frame.showScreen("multiplayer");});

        hamburger.addActionListener(e -> {
                frame.lastscreen = "waitingscreen";
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
        GradientPaint oceanGradient = new GradientPaint(0, 0, frame.colorsheme.color1, 0, getHeight(), frame.colorsheme.color2); // es wird ein Objekt initialisiert das den Farbverlauf definieren soll. Struktur der Initialisierung: Startpunkt,Startfarbe,Endpunkt,Endfarbe
        g2d.setPaint(oceanGradient); // Dadurch wird gesagt womit gezeichnet wird
        g2d.fillRect(0, 0, getWidth(), getHeight()); // dadurch wird gemalt. Festlegung wo und wie groß der Bereich ist, der gefüllt werden soll mit getWidth(),getHeight() bekomme ich die Breite und Höhe vom singleplayerobjekt
    }
}