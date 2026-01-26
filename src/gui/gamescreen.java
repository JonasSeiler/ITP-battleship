package src.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.logic.*;
/**
 * Spielfeldscreen, wo die bereits ausgewählten Schiffe
 * auf das Spielfeld platziert werden und schließlich
 * das Spiel gestartet wird.
 * @author Max Steingräber
 */
public class gamescreen extends JPanel {
    /**
     * Spielattribute
     * 
     * Felddaten
     * @param pCells    Spielerzellen
     * @param eCells    Gegnerzellen
     * @param pField    Spielerfeld
     * @param eField    Gegnerfeld
     * 
     * Schiffbearbeitungsdaten
     * @param horizontal        Richtung (0 -> horizontal, 1 -> vertikal)
     * @param currentShipSize   Aktuelle ausgewählte Schiffsgröße
     * @param occupied          Feld, das Schiffskoordinaten speichert
     *                          (0 -> Zelle frei  1 -> Zelle belegt)   
     * @param shipsLeft         Übrige Schiffe pro Größe
     * @param hoverRow          Rotationsvorschau (Reihe)
     * @param hoverCol          Rotationsvorschau (Spalte)
     * @param shipSelector      Auswahlbox für Schiffsgröße
     * 
     * Gemeinsame Daten
     * @param COR       Koordinaten (x, y) des jeweiligen Schiffs
     * @param SHIPS     Schiffslänge des jeweilgen Schiffs
     * @param DIR       Richtung (false -> vertikal, true -> horizontal) des jeweiligen Schiffs
     * @param status    Speichert Rückgabewert von 'send_shot' Methode
     *                  (0 -> Daneben   1 -> Getroffen  2 -> Versunken)
     * @param gridSize  Speichert die übergebene Spielfeldgröße
     * @param gLogic    Enthält Spielelogik und 'send_shot' Methode
     * 
     */
    private JButton[][] pCells;
    private JButton[][] eCells;
    public JPanel pField;
    private JPanel eField;
    private int placedShipCount = 0;

    public coordinate[] COR;
    public int[] SHIPS;
    public boolean[] DIR;
    public int gridSize;
    public int[] ships;

    private boolean horizontal = true;
    private int currentShipSize = 2;
    private boolean[][] occupied;
    private int[] shipsLeft;
    private int hoverRow = -1;
    private int hoverCol = -1;
    private JComboBox<String> shipSelector;
    private boolean gameSaved = false;
    private mainframe frame;

    /**
     * Konstruiert den Schiffplatzierungsscreen ('gamescreen')
     * 
     * @param frame         Hauptfensterscreen, der alle Screens enthält und verwaltet
     * @param inShips       Übergebenes Schiffsfeld Bsp. {5, 5, 4, 3, 3, 3, 2} von {@link hostpregamescreen}
     * @param inGridSize    Übergebene Spielfeldgröße von {@link hostpregamescreen}
     */
    public gamescreen(mainframe f, int[] inShips, int inGridSize) {
        /*--Speichern der übergebenen Input-Parameter--*/
        this.gridSize = inGridSize;
        int totalShips = inShips.length;
        this.frame = f;

        COR = new coordinate[totalShips];
        DIR = new boolean[totalShips];
        SHIPS = new int[totalShips];

        /*--Konvertierung in Schiffbearbeitungsformat--
        --ships[0] = 2-sized--
        --ships[1] = 3-sized--
        --ships[2] = 4-sized--
        --ships[3] = 5-sized--*/
        ships = convertShipArray(inShips);

        /*--Layoutmanager 'this'-Panel--*/
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        this.setOpaque(false);

        /*--Titel--*/
        JLabel title = new JLabel("Battleship");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
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
        pField = createField(gridSize, gridSize, pCells);

        /*--Spielertitel--*/
        JLabel playerTitle = new JLabel("Your Side");
        playerTitle.setHorizontalAlignment(SwingConstants.CENTER);
        playerTitle.setForeground(Color.WHITE);
        playerTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
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

        /*--Feld, das Schiffskoordinaten speichert--*/
        occupied = new boolean[gridSize][gridSize];

        /*--Klone Schiffsfeld--*/
        shipsLeft = ships.clone();

        /*--Zusätzliches Spielerfeld-Panel auf Spielerseite (links) für saveButton--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);
        pFieldPanel.setOpaque(false);
        pFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        /*--Kombobox Textfeld--*/
        String[] shipNames = new String[ships.length];
        for (int i = 0; i < ships.length; i++) {
            shipNames[i] = (i + 2) + "-ships: " + ships[i] + "x";
        }

        /*--Erstellung 'shipSelector' Kombobox--*/
        shipSelector = new JComboBox<>(shipNames);

        int firstAvailable = findNextAvailableIndex(0);
        if (firstAvailable != -1) {
            shipSelector.setSelectedIndex(firstAvailable);
            currentShipSize = firstAvailable + 2; // ship sizes = index + 2
        } else {
            shipSelector.setEnabled(false); // no ships left at all
        }

        pFieldPanel.add(shipSelector);

        /*--Start Button--*/
        JButton startButton = new RoundButton("Start Game");
        startButton.addActionListener(e -> {if (allShipsPlaced()) frame.startBattle();});
        pFieldPanel.add(startButton);

        /*--Fügt Spielerfeld-Panel auf Spielerseite (links) hinzu--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--Gegnerseite (rechts) Panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);
        eSide.setOpaque(false);

        /*--Gegnerfelderstellung (rechte Zellen)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells);

        /*--Gegnertitel--*/
        JLabel enemyTitle = new JLabel("Enemy Side");
        enemyTitle.setHorizontalAlignment(SwingConstants.CENTER);
        enemyTitle.setForeground(Color.WHITE);
        enemyTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
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

        /*--Exit button--*/
        JButton exitButton = new RoundButton("Exit Game");
        exitButton.setEnabled(true);
        exitButton.addActionListener(e -> handleExitGame());

        /*--Zusätzliches Gegnerfeld-Panel auf Gegnerseite (rechts) für loadButton und exitButton--*/
        JPanel eFieldPanel = new JPanel();
        eFieldPanel.setBackground(Color.BLACK);
        eFieldPanel.setOpaque(false);
        eFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        eFieldPanel.add(exitButton);
        eSide.add(eFieldPanel, BorderLayout.SOUTH);

        /*--Fügt Spielerseite und Gegnerseite zu Spielboard hinzu--*/
        board.add(pSide);
        board.add(eSide);
        this.add(board, BorderLayout.CENTER);

        /*--Fügt interaktive Spielfeld-Komponenten hinzu--*/
        addPlacementListeners();
        setupKeyBindings();
        setFocusable(true);
    }
    /**
     * Erstellt ein Spielfeld
     * 
     * @param x         Reihenanzahl
     * @param y         Spaltenanzahl
     * @param array     Button Array (Zellen)
     * @return          Generiertes Spielfeld
     */
    private JPanel createField(int x, int y, JButton[][] array) {
        JPanel field = new SquareGridPanel(x, y);
        field.setOpaque(false);

        for (int r = 0; r < x; r++) {
            for (int c = 0; c < y; c++) {
                JButton cell = new JButton();
                cell.setBackground(Color.DARK_GRAY);
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFocusPainted(false);
                array[r][c] = cell;
                field.add(cell);
            }
        }
        return field;
    }
    /**
     * Fügt interaktive Maussteuerung (hover, exit, click) hinzu
     */
    private void addPlacementListeners() {
        /*--update selected ship size using the combo box index (safe)--*/
        shipSelector.addActionListener(e -> {
            currentShipSize = shipSelector.getSelectedIndex() + 2;
            /*--when user changes selection, reapply preview if hovering--*/
            clearPreview();
            /*--if hovering over a cell -> previewShip--*/
            if (hoverRow != -1 && hoverCol != -1) {
                previewShip(hoverRow, hoverCol);
            }
        });

        /*--going through each cell of the whole player grid--*/
        for (int r = 0; r < pCells.length; r++) {
            for (int c = 0; c < pCells[r].length; c++) {
                /*--ensures each listener remembers its own cell coordinates--*/
                int row = r, col = c;
                /*--add mouse listener for every player cell--*/
                pCells[r][c].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverRow = row;
                        hoverCol = col;
                        clearPreview();
                        previewShip(row, col);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverRow = -1;
                        hoverCol = -1;
                        clearPreview();
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (occupied[row][col]) {
                            removeShipAt(row, col);
                        } else {
                            placeShip(row, col);
                        }
                    }
                });
            }
        }
    }
    /**
     * Fügt interaktives Tastatursteuerung hinzu, um Schiffe
     * mit 'r' oder 'R' drehen zu können, mit 'z' oder 'Z' das
     * zuletzt platzierte Schiff zu entfernen und mit 'strg' + 'z'
     */
    private void setupKeyBindings() {
        /*--build input/action map to use key-bindings--*/
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        /*--assign upper/lower case 'r'--*/
        im.put(KeyStroke.getKeyStroke('r'), "rotate");
        im.put(KeyStroke.getKeyStroke('R'), "rotate");

        /*--when input map says 'rotate'--*/
        am.put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*--flip ship placement direction--*/
                horizontal = !horizontal;
                clearPreview();
                if (hoverRow != -1 && hoverCol != -1) {
                    previewShip(hoverRow, hoverCol);
                }
            }
        });

        im.put(KeyStroke.getKeyStroke('z'), "undoLast");
        im.put(KeyStroke.getKeyStroke('Z'), "undoLast");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undoAll");

        am.put("undoLast", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            undoLastShip();
            clearPreview();
            }
        });

        am.put("undoAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoAllShips();
                clearPreview();
            }
        });
    }
    /**
     * Schiffsvorschau
     * 
     * @param r     Ausgewählte Reihe
     * @param c     Ausgewählte Spalte
     */
    private void previewShip(int r, int c) {
        int index = currentShipSize - 2;

        /*--no preview if this ship type is exhausted--*/
        if (index < 0 || index >= shipsLeft.length || shipsLeft[index] <= 0) {return;}
        /*--default value of valid is set to true--*/
        boolean valid = true;

        /*--check bounds and overlap--*/
        for (int i = 0; i < currentShipSize; i++) {
            /*--calculates tile positions of the ship--*/
            int rr = r + (horizontal ? i : 0);
            int cc = c + (horizontal ? 0 : i);
            /*--invalid if tile is outside of game board or if it's already occupied by another ship--*/
            if (!isInBounds(rr, cc) || occupied[rr][cc] || hasAdjacentOccupied(rr, cc, occupied)) {valid = false;}
        }

        /*--highlight--*/
        for (int i = 0; i < currentShipSize; i++) {
            /*--calculates tile positions of the ship--*/
            int rr = r + (horizontal ? i : 0);
            int cc = c + (horizontal ? 0 : i);
            /*--valid if tile is inside of game board and if it's not occupied by another ship--*/
            if (isInBounds(rr, cc) && !occupied[rr][cc]) {
            pCells[rr][cc].setBackground(valid ? Color.GREEN : Color.RED);
            }
        }
    }
    /**
     * Platziert Spielerschiffe
     * 
     * @param r     Ausgewählte Reihe
     * @param c     Ausgewählte Spalte
     */
    private void placeShip(int r, int c) {
        /*--get index of each ship size--*/
        int index = currentShipSize - 2;

        if (index < 0 || index >= shipsLeft.length) {return;}

        /*--when no ships of index size are left--*/
        if (shipsLeft[index] <= 0) {return;}

        /*--validate placement--*/
        for (int i = 0; i < currentShipSize; i++) {
            /*--calculates tile positions of the ship--*/
            int rr = r + (horizontal ? i : 0);
            int cc = c + (horizontal ? 0 : i);

            if (!isInBounds(rr, cc)) {return;}
            if (occupied[rr][cc] || hasAdjacentOccupied(rr, cc, occupied)) {return;}
        }

        /*--place ship, color it blue and save it in 'occupied'-array--*/
        for (int i = 0; i < currentShipSize; i++) {
            /*--calculates tile positions of the ship--*/
            int rr = r + (horizontal ? i : 0);
            int cc = c + (horizontal ? 0 : i);

            occupied[rr][cc] = true;
            pCells[rr][cc].setBackground(Color.BLUE);
        }

        /*--save placed ship data--*/
        COR[placedShipCount] = new coordinate(r, c);   // starting coordinate
        DIR[placedShipCount] = horizontal;             // direction
        SHIPS[placedShipCount] = currentShipSize;      // ship length
        
        placedShipCount++;
        
        /*--decrement ship count--*/
        shipsLeft[index]--;

        /*--refresh combo box display (shipSelector)--*/
        updateComboBoxDisplayAutoSkip();

        if (allShipsPlaced()) {
            shipSelector.setEnabled(false);
        }

        clearPreview();
    }
    /**
     * Wechselt automatisch die Schiffsauswahl zur nächsten Schiffsgröße
     * in der Kombobox, nachdem ein Schiffstyp 'leer' ist
     */
    private void updateComboBoxDisplayAutoSkip() {
        int previousIndex = shipSelector.getSelectedIndex();

        shipSelector.removeAllItems();

        for (int i = 0; i < shipsLeft.length; i++) {
        shipSelector.addItem((i + 2) + "-ships: " + shipsLeft[i] + "x");
        }

        /*--auto-select next valid ship--*/
        int newIndex = findNextAvailableIndex(previousIndex);

        if (newIndex != -1) {
        shipSelector.setSelectedIndex(newIndex);
        currentShipSize = newIndex + 2;
        }   else {
        shipSelector.setEnabled(false);
        }
    }
    /**
     * Setzt Schiffsvorschau zurück
     */
    private void clearPreview() {
        for (int r = 0; r < pCells.length; r++) {
            for (int c = 0; c < pCells[r].length; c++) {
                if (!occupied[r][c]) {
                    pCells[r][c].setBackground(Color.DARK_GRAY);
                }
            }
        }
    }
    /**
     * Überprüft, ob alle Schiffe platziert wurden
     * 
     * @return  true, wenn alle Schiffe platziert wurden
     *          false, wenn noch nicht alle Schiffe platziert wurden
     */
    private boolean allShipsPlaced() {
        for (int v : shipsLeft) {
            if (v > 0) return false;
        }
        return true;
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
     * Überprüft, ob Zelle neben an (auch diagonal) schon belegt ist
     * 
     * @param r         x-Koordinate
     * @param c         y-Koordinate
     * @param board     Belegte Spielfeldkoordinaten
     * @return          true, wenn belegt
     *                  false, wenn nicht belegt
     */
    private boolean hasAdjacentOccupied(int r, int c, boolean[][] board) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int rr = r + dr;
                int cc = c + dc;
                if (rr >= 0 && cc >= 0 &&
                    rr < board.length && cc < board[0].length &&
                    board[rr][cc]) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Konvertiert übergebenes Schiffsfeldformat zu Schiffbearbeitungsformat
        --ships[0] = 2-sized--
        --ships[1] = 3-sized--
        --ships[2] = 4-sized--
        --ships[3] = 5-sized--
     * Bsp.: {5, 4, 4, 3, 3, 3, 2, 2, 2, 2}  ->  [4, 3, 2, 1]
     *
     * @param ships     Übergebenes Schiffsfeldformat {5, 4, 4, 3, 3, 3, 2, 2, 2, 2}
     * @return          Schiffbearbeitungsformat [4, 3, 2, 1]
     */
    public int[] convertShipArray(int[] ships) {
        int[] shipCount = new int[4];

        for (int size : ships) {
            int index = size - 2;
            if (index >= 0 && index < shipCount.length) {
                shipCount[index]++;
            }
        }
        return shipCount;
    }
    /**
     * Findet nächst verfügbaren
     * Schiffsgrößenauswahl in der Kombobox
     * 
     * @param startIndex    Anfangsindex der Auswahl
     * @return              Nächster Index
     */
    private int findNextAvailableIndex(int startIndex) {
        for (int i = startIndex; i < shipsLeft.length; i++) {
            if (shipsLeft[i] > 0) return i;
        }
        for (int i = startIndex - 1; i >= 0; i--) {
            if (shipsLeft[i] > 0) return i;
        }
        return -1;
    }
    /**
    * Beendet das Spiel.
    * Falls das Spiel nicht gespeichert wurde, wird
    * der Nutzer zur Bestätigung aufgefordert
    */
    private void handleExitGame() {
        if (gameSaved) {
            frame.showScreen("titlescreen");
            try {
                frame.coms.close();
            } catch(Exception ex) {
                System.err.println("Failed closing connection: " + ex);
            }
            frame.coms = null;
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
                try {
                    frame.coms.close();
                } catch(Exception ex) {
                    System.err.println("Failed closing connection: " + ex);
                }
                frame.coms = null;          
            } else if (choice == JOptionPane.NO_OPTION) {
                frame.showScreen("battlescreen");
            }
        }

        new QuitConfirmDialog(frame);
    }
    /**
     * Entfernt alle bereits platzierten Schiffe
     */
    private void undoAllShips() {
        while (placedShipCount > 0) {
            undoLastShip();
        }
    }
    /**
     * Entfernt das zuletzt platzierte Schiff
     */
    private void undoLastShip() {
        if (placedShipCount == 0) return;

        int lastIndex = placedShipCount - 1;

        int r = COR[lastIndex].x;
        int c = COR[lastIndex].y;
        int size = SHIPS[lastIndex];
        boolean dir = DIR[lastIndex];

        // remove ship from board
        for (int i = 0; i < size; i++) {
            int rr = r + (dir ? i : 0);
            int cc = c + (dir ? 0 : i);

            occupied[rr][cc] = false;
            pCells[rr][cc].setBackground(Color.DARK_GRAY);
        }

        shipsLeft[size - 2]++;

        COR[lastIndex] = null;

        placedShipCount--;

        shipSelector.setEnabled(true);
        updateComboBoxDisplayAutoSkip();
    }
    /**
     * Entfernt Schiff an spezifischer Koordinate
     * 
     * @param r     row (x-Koordinate)
     * @param c     column (y-Koordinate)
     */
    private void removeShipAt(int r, int c) {
        int index = findShipIndexAt(r, c);
        if (index == -1) return;

        int size = SHIPS[index];
        boolean dir = DIR[index];
        coordinate start = COR[index];

        // clear board + occupied
        for (int i = 0; i < size; i++) {
            int rr = start.x + (dir ? i : 0);
            int cc = start.y + (dir ? 0 : i);

            occupied[rr][cc] = false;
            pCells[rr][cc].setBackground(Color.DARK_GRAY);
        }

        // restore ship count
        shipsLeft[size - 2]++;

        // compact arrays (IMPORTANT)
        for (int i = index; i < placedShipCount - 1; i++) {
            COR[i] = COR[i + 1];
            SHIPS[i] = SHIPS[i + 1];
            DIR[i] = DIR[i + 1];
        }

        COR[placedShipCount - 1] = null;
        placedShipCount--;

        shipSelector.setEnabled(true);
        updateComboBoxDisplayAutoSkip();
        clearPreview();
    }
    /**
     * Findet Schiff-Index an spezifischer Koordinate
     * @param r     row (x-Koordinate)
     * @param c     column (y-Koordinate)
     * @return      Rückgabewert ist der Index
     */
    private int findShipIndexAt(int r, int c) {
        for (int i = 0; i < placedShipCount; i++) {
            coordinate start = COR[i];
            int size = SHIPS[i];
            boolean dir = DIR[i];

            for (int j = 0; j < size; j++) {
                int rr = start.x + (dir ? j : 0);
                int cc = start.y + (dir ? 0 : j);

                if (rr == r && cc == c) {
                    return i;
                }
            }
        }
        return -1;
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
