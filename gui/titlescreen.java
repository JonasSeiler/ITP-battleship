import javax.swing.*; // Importiert die gesamte Swing Bibliothek, moderne und plattformunabhängige GUI-Komponenten
import java.awt.*; // Importiert das Abstract Window Toolkit (AWT) liefert grundlegende Grafik- und Farbobjekte und einige Layout Manager
import javax.swing.JButton; // aus der Java Swing Bibliothek wird die JButton Klasse bekannt und verfügbar gemacht

public class titlescreen extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
    private JButton singleplayer;
    private JButton multiplayer;

    public titlescreen(mainframe frame) { // mainframe ist das Hauptfenster und titlescreen gibt Befehle an den mainframe
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den titlescreen gepackt
        setOpaque(false); // Erlaubt der paintComponent-Methode den Hintergrund zu zeichnen
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        JLabel title = new JLabel("Tidebreaker");
        title.setForeground(Color.WHITE);
        singleplayer = new JButton("singleplayer");
        multiplayer = new JButton("multiplayer");
        title.setFont(new Font("SansSerif", Font.BOLD,40));
        singleplayer.setFont(new Font("SansSerif", Font.BOLD,20));
        multiplayer.setFont(new Font("SansSerif", Font.BOLD,20));
        contentPanel.add(title);
        contentPanel.add(new JLabel(""));
        contentPanel.add(singleplayer);
        contentPanel.add(multiplayer);
        singleplayer.addActionListener(e -> {frame.showScreen("singleplayer");});
        multiplayer.addActionListener(e -> {frame.showScreen("multiplayer");});
        add(contentPanel); // das contentPanel wird auf das titlescreen-Panel gelegt
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
}