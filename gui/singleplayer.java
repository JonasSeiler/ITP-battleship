import javax.swing.*; // Importiert die gesamte Swing Bibliothek, moderne und plattformunabhängige GUI-Komponenten
import java.awt.*; // Importiert das Abstract Window Toolkit (AWT) liefert grundlegende Grafik- und Farbobjekte und einige Layout Manager
import javax.swing.JButton; // aus der Java Swing Bibliothek wird die JButton Klasse bekannt und verfügbar gemacht

public class singleplayer extends JPanel { // JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
    private mainframe frame; // Referenz auf das Hauptfenster
    private JButton new_game;
    private JButton load_game;
    private JButton exit;

    public singleplayer(mainframe frame) { // mainframe ist das Hauptfenster und der singleplayerscreen gibt Befehle an den mainframe
        setLayout(new GridBagLayout()); // Bestimmt, wie Komponenten angeordnet werden, also das JPannel was erstellt wird, wird von dem GridBagLayout in die Mitte auf den singleplayerscreen gepackt
        setOpaque(false); // Erlaubt der paintComponent-Methode den Hintergrund zu zeichnen
        JPanel contentPanel = new JPanel(); // Erstellt das zentrale Pannel, das alle Steuerelemente bündelt. JPanel ist ein Standard-Container oder Leinwand um Buttons usw. gut zu platzieren
        contentPanel.setOpaque(false); // Content Panel soll durchsichtig sein
        contentPanel.setLayout(new GridLayout(0,1,10,10)); // der Layout Manager legt fest es gibt beliebig viele Zeilen, zwei Spalte und die Abstände sind 10
        JLabel title = new JLabel("Tidebreaker");
        title.setForeground(Color.WHITE);
        new_game = new JButton("New Game");
        load_game = new JButton("Load Game");
        exit = new JButton("   <-   ");
        title.setFont(new Font("SansSerif", Font.BOLD,40));
        new_game.setFont(new Font("SansSerif", Font.BOLD,20));
        load_game.setFont(new Font("SansSerif", Font.BOLD,20));
        exit.setFont(new Font("SansSerif", Font.BOLD,35));
        contentPanel.add(title);
        contentPanel.add(new JLabel(""));
        contentPanel.add(new_game);
        contentPanel.add(load_game);
        contentPanel.add(exit);
        new_game.addActionListener(e -> {frame.showScreen("pregamescreen");});
        load_game.addActionListener(e -> {frame.showScreen("gamescreen");});
        exit.addActionListener(e -> {frame.showScreen("titlescreen");});
        add(contentPanel); // das contentPanel wird auf das singleplayer-Panel gelegt
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