package src.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.logic.*;
/**
 * Hauptspielscreen, wo der Spieler auf das 
 * Gegnerfeld schießt.
 * @author Max Steingräber
 */
public class battlescreen extends JPanel {
    /**
     * Spielattribute
     * 
     * Felddaten
     * @param pCells    Spielerzellen
     * @param eCells    Gegnerzellen
     * @param pField    Spielerfeld
     * @param eField    Gegnerfeld
     * 
     * Gemeinsame Daten
     * @param COR       Koordinaten (x, y) des jeweiligen Schiffs
     * @param SHIPS     Schiffslänge des jeweilgen Schiffs
     * @param DIR       Richtung (0 -> horizontal, 1 -> vertikal) des jeweiligen Schiffs
     * @param status    Speichert Rückgabewert von 'send_shot' Methode
     *                  (0 -> Daneben   1 -> Getroffen  2 -> Versunken)
     * @param gridSize  Speichert die übergebene Spielfeldgröße
     * @param gLogic    Enthält Spielelogik und 'send_shot' Methode
     * 
     */
    private JButton[][] pCells;
    private JButton[][] eCells;
    private JPanel pField;
    private JPanel eField;
    private mainframe frame;
    
    public coordinate[] COR;
    public int[] SHIPS;
    public boolean[] DIR;
    public int status = 0;
    public int gridSize;
    public game gLogic;
    private boolean gameSaved = false;
    private JButton saveButton;
    private JButton confirmShotButton;
    private int selectedX = -1;
    private int selectedY = -1;
    private JButton selectedEnemyCell = null;
    /**
     * Konstruiert den Hauptspielscreen.
     * 
     * @param frame         Hauptfensterscreen, der alle Screens enthält und verwaltet
     * @param c             Koordinaten (x, y) des jeweiligen Schiffs
     * @param s             Schiffslänge des jeweilgen Schiffs
     * @param d             Richtung (false -> vertikal, true -> horizontal) des jeweiligen Schiffs
     * @param inGridSize    Übergebene Spielfeldgröße von {@link hostpregamescreen}
     */
    public battlescreen(mainframe f, coordinate[] c, int[] s, boolean[] d, int inGridSize) {
        /*--Speichern der übergebenen Input-Parameter--*/
        this.COR = c;
        this.SHIPS = s;
        this.DIR = d;
        this.gridSize = inGridSize;
        this.frame = f;

        /*--Layoutmanager 'this'-Panel--*/
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        this.setOpaque(false);

        /*--Titel--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Sans Serif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        this.add(title, BorderLayout.NORTH);

        /*--Erstellung des Spielboards--*/
        JPanel board = new JPanel(new GridLayout(1, 2, 20, 0));
        board.setBackground(Color.black);
        board.setOpaque(false);

        /*--Spielerseite (links) Panel--*/
        JPanel pSide = new JPanel(new BorderLayout());
        pSide.setBackground(Color.black);
        pSide.setOpaque(false);

        /*--Spielerfelderstellung (linke Zellen)--*/
        pCells = new JButton[gridSize][gridSize];
        pField = createField(gridSize, gridSize, pCells, false);
        pSide.add(pField, BorderLayout.CENTER);

        /*--Spielertitel--*/
        JLabel playerTitle = new JLabel("Your side");
        playerTitle.setHorizontalAlignment(SwingConstants.CENTER);
        playerTitle.setForeground(Color.WHITE);
        playerTitle.setFont(new Font("Sans Serif", Font.BOLD, 20));
        playerTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        /*--Netzhalter (Spieler)--*/
        JPanel pCenter = new JPanel(new GridBagLayout());
        pCenter.setBackground(Color.black);
        pCenter.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 40, 0, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        pCenter.add(pField, gbc);

        /*--Titel-Netz-Kombination (Spieler)--*/
        JPanel pCenterWrapper = new JPanel(new BorderLayout());
        pCenterWrapper.setBackground(Color.black);
        pCenterWrapper.add(playerTitle, BorderLayout.NORTH);
        pCenterWrapper.add(pCenter, BorderLayout.CENTER);
        pCenterWrapper.setOpaque(false);

        pSide.add(pCenterWrapper, BorderLayout.CENTER);

        /*--Zusätzliches Spielerfeld-Panel auf Spielerseite (links) für saveButton--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);
        pFieldPanel.setOpaque(false);
        pFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        /*--saveButton--*/
        saveButton = new RoundButton("Save Game");
        saveButton.setEnabled(true);
        saveButton.addActionListener(e -> {
            gameSaved = true;
            JOptionPane.showMessageDialog(
                this,
                "The game has been successfully saved",
                "Save Game",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        // saveButton.addActionListener(e -> gLogic.save_game());
        pFieldPanel.add(saveButton);

        /*--Fügt Spielerfeld-Panel auf Spielerseite (links) hinzu--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--Gegnerseite (rechts) Panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);
        eSide.setOpaque(false);

        /*--Gegnerfelderstellung (rechte Zellen)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells, true);
        eSide.add(eField, BorderLayout.CENTER);

        /*--Gegnertitel--*/
        JLabel enemyTitle = new JLabel("Enemy side");
        enemyTitle.setHorizontalAlignment(SwingConstants.CENTER);
        enemyTitle.setForeground(Color.WHITE);
        enemyTitle.setFont(new Font("Sans Serif", Font.BOLD, 20));
        enemyTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        /*--Netzhalter (Gegner)--*/
        JPanel eCenter = new JPanel(new GridBagLayout());
        eCenter.setBackground(Color.black);
        eCenter.setOpaque(false);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0;
        gbc2.weighty = 1.0;
        gbc2.insets = new Insets(0, 20, 0, 40);
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.CENTER;

        eCenter.add(eField, gbc2);

        /*--Titel-Netz-Kombination (Gegner)--*/
        JPanel eCenterWrapper = new JPanel(new BorderLayout());
        eCenterWrapper.setBackground(Color.black);
        eCenterWrapper.setOpaque(false);
        eCenterWrapper.add(enemyTitle, BorderLayout.NORTH);
        eCenterWrapper.add(eCenter, BorderLayout.CENTER);

        eSide.add(eCenterWrapper, BorderLayout.CENTER);

        /*--Load/Exit button--*/
        confirmShotButton = new RoundButton("Confirm Shot");
        confirmShotButton.setEnabled(true);
        confirmShotButton.addActionListener(e -> confirmShot());
        // saveButton.addActionListener(e -> gLogic.load_game("data-name.txt"));
        JButton exitButton = new RoundButton("Exit Game");
        exitButton.setEnabled(true);
        exitButton.addActionListener(e -> handleExitGame());

        /*--Zusätzliches Gegnerfeld-Panel auf Gegnerseite (rechts) für loadButton und exitButton--*/
        JPanel eFieldPanel = new JPanel();
        eFieldPanel.setBackground(Color.BLACK);
        eFieldPanel.setOpaque(false);
        eFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        eFieldPanel.add(confirmShotButton);
        eFieldPanel.add(exitButton);
        eSide.add(eFieldPanel, BorderLayout.SOUTH);

        /*--Fügt Spielerseite und Gegnerseite zu Spielboard hinzu--*/
        board.add(pSide);
        board.add(eSide);
        this.add(board, BorderLayout.CENTER);
        drawPlayerShips();
        setFocusable(true);
    }
    
    /**
     * Erstellt ein Spielfeld
     * 
     * @param row           Reihenanzahl
     * @param col           Spaltenanzahl
     * @param array         Button Array (Zellen)
     * @param clickable     Bestimmt, ob Zellen auf Clicks reagieren sollen
     * @return              Generiertes Spielfeld
     */
    private JPanel createField(int row, int col, JButton[][] array, boolean clickable) {
        JPanel field = new SquareGridPanel(row, col);
        field.setBackground(Color.black);
        field.setOpaque(false);

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                JButton cell = new JButton();
                cell.setBackground(Color.DARK_GRAY);
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFocusPainted(false);
                array[r][c] = cell;

                int x = r;
                int y = c;

                if(clickable) {
                    cell.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            onEnemyCellClicked(x, y);
                        }
                    });
                }

                field.add(cell);
            }
        }
        return field;
    }
    /**
     * Überprüft, ob Koordinaten sich innerhalb des Spielfelds befinden
     * 
     * @param r     x-Koordinate
     * @param c     y-Koordinate
     * @return      true, wenn innerhalb des Spielfelds
     *              false, wenn außerhalb des Spielfelds
     */
    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 &&
               r < pCells.length &&
               c < pCells[0].length;
    }
    /**
     * Zeichnet alle Spielerschiffe in das Spielerfeld ein, die in
     * den gemeinsamen Daten (COR, SHIPS, DIR) gespeichert sind
     */
    private void drawPlayerShips() {
    for (int i = 0; i < COR.length; i++) {
        coordinate start = COR[i];
        int len = SHIPS[i];
        boolean horizontal = DIR[i];

        for (int j = 0; j < len; j++) {
            int r = start.x + (horizontal ? j : 0);
            int c = start.y + (horizontal ? 0 : j);

            pCells[r][c].setBackground(Color.BLUE);
            }
        }
    }
    /**
     * Färbt Spielerschiffe
     * 
     * @param x     x-Koordinate
     * @param y     y-Koordinate
     * @param i     Statuscode (0: rot, 1: gelb, 2: grün)
     */
    public void colorPlayerShip(int x, int y, int i) {
        switch(i) {
            case 0:
                pCells[x][y].setBackground(Color.red);
                break;
            case 1:
                pCells[x][y].setBackground(Color.yellow);
                break;
            case 2:
                pCells[x][y].setBackground(Color.green);
                break;
        }
    }
    /**
     * Färbt Gegnerschiffe
     * 
     * @param x     x-Koordinate
     * @param y     y-Koordinate
     * @param i     Statuscode (0: rot, 1: gelb, 2: grün)
     */
    private void colorEnemyShip(int x, int y, int i) {
        switch(i) {
            case 0:
                eCells[x][y].setBackground(Color.red);
                break;
            case 1:
                eCells[x][y].setBackground(Color.yellow);
                break;
            case 2:
                eCells[x][y].setBackground(Color.green);
                if (isInBounds(x-1, y) && eCells[x-1][y].getBackground() == Color.yellow) {
                    colorEnemyShip(x-1, y, i);
                }
                if (isInBounds(x+1, y) && eCells[x+1][y].getBackground() == Color.yellow) {
                    colorEnemyShip(x+1, y, i);
                }
                if (isInBounds(x, y-1) && eCells[x][y-1].getBackground() == Color.yellow) {
                    colorEnemyShip(x, y-1, i);
                }
                if (isInBounds(x, y+1) && eCells[x][y+1].getBackground() == Color.yellow) {
                    colorEnemyShip(x, y+1, i);
                }
                break;
        }
    }
    /**
     * Checkt Klicks auf die Gegenerzellen des Gegnerspielfelds
     * 
     * @param x     x-Koordinate
     * @param y     y-Koordinate
     */
    private void onEnemyCellClicked(int x, int y) {
        if (selectedEnemyCell != null) {
            selectedEnemyCell.setBorder(UIManager.getBorder("Button.border"));
        }

        // Store new selection
        selectedX = x;
        selectedY = y;
        selectedEnemyCell = eCells[x][y];

        // Visual feedback (selection)
        selectedEnemyCell.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        // Enable confirm button
        confirmShotButton.setEnabled(true);

        eCells[x][y].setEnabled(false);
    }
    /**
     * Setzt Spielelogik
     * 
     * @param g     Spiellogikobjekt
     */
    public void setGame(game g) {
        this.gLogic = g;
    }
    /**
    * Handles exiting the game.
    * If the game is not saved, asks the user for confirmation.
    */
    private void handleExitGame() {
        if (gameSaved) {
        frame.showScreen("titlescreen");
        return;
        } else {
            int choice = JOptionPane.showConfirmDialog(
            this,
            "The game has not been saved.\nDo you really want to quit?",
            "Unsaved Progress",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
            frame.showScreen("titlescreen");
            } else if (choice == JOptionPane.NO_OPTION) {
            frame.showScreen("battlescreen");
            }
        }
    }
    /**
     * Deaktiviert Benutzeroberfläche (saveButton und confirmShotButton)
     */
    public void disableUI() {
        saveButton.setEnabled(false);
        confirmShotButton.setEnabled(false);
    }
    /**
     * Aktiviert Benutzeroberfläche (saveButton und confirmShotButton)
     */
    public void enableUI() {
        saveButton.setEnabled(true);
        confirmShotButton.setEnabled(true);
    }
    /**
     * Überprüft Treffervalidierung und färbt dementsprechend die Gegnerzelle
     */
    private void confirmShot() {
        if (selectedEnemyCell == null) {
            return;
        }

        int result = gLogic.send_shot(selectedX, selectedY);

        colorEnemyShip(selectedX, selectedY, result);

        // Reset selection
        selectedEnemyCell = null;
        selectedX = -1;
        selectedY = -1;
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

/*  
    Grid left side abstand/rahmen
    Cells should always be quadratic
    --Macht Matthias alles----
    Load Game implementieren -> when explorer button 'open' is pressed call Jonas' load_game("data-name.txt") function
    -> remove load game
    --------------------------
    confirm shot button and save game button -> 1 method to disable the button -> 1 method to enable the button
    confirm shot button -> cell has to be selected and then shot button
    during game 'battlescreen' load game button -> false and save game button -> true
    during 'gamescreen' load game button -> true and save game button -> false

    when somebody wins/loses user msg
    change background colors for each specific color theme (beige, dark, blue)
*/
