package src.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.logic.*;

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
     * @param DIR       Richtung (0 -> horizontal, 1 -> vertikal) des jeweiligen Schiffs
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

    /**
     * Konstruiert den Schiffplatzierungsscreen ('gamescreen')
     * 
     * @param frame         Hauptfensterscreen, der alle Screens enthält und verwaltet
     * @param inShips       Übergebenes Schiffsfeld Bsp. {5, 5, 4, 3, 3, 3, 2} von {@link hostpregamescreen}
     * @param inGridSize    Übergebene Spielfeldgröße von {@link hostpregamescreen}
     */
    public gamescreen(mainframe frame, int[] inShips, int inGridSize) {
        /*--Speichern der übergebenen Input-Parameter--*/
        this.gridSize = inGridSize;
        int totalShips = inShips.length;

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

        /*--Titel--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Sans Serif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        this.add(title, BorderLayout.NORTH);

        /*--Erstellung des Spielboards--*/
        JPanel board = new JPanel(new GridLayout(1, 2, 20, 0));
        board.setBackground(Color.black);

        /*--Spielerseite (links) Panel--*/
        JPanel pSide = new JPanel(new BorderLayout());
        pSide.setBackground(Color.black);

        /*--Spielerfelderstellung (linke Zellen)--*/
        pCells = new JButton[gridSize][gridSize];
        pField = createField(gridSize, gridSize, pCells);
        pSide.add(pField, BorderLayout.CENTER);

        /*--Feld, das Schiffskoordinaten speichert--*/
        occupied = new boolean[gridSize][gridSize];

        /*--Klone Schiffsfeld--*/
        shipsLeft = ships.clone();

        /*--Zusätzliches Spielerfeld-Panel auf Spielerseite (links) für saveButton--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);

        /*--Kombobox Textfeld--*/
        String[] shipNames = new String[ships.length];
        for (int i = 0; i < ships.length; i++) {
            shipNames[i] = (i + 2) + "-ships: " + ships[i] + "x";
        }

        /*--Erstellung 'shipSelector' Kombobox--*/
        shipSelector = new JComboBox<>(shipNames);
        shipSelector.setSelectedIndex(0);
        currentShipSize = shipSelector.getSelectedIndex() + 2;
        pFieldPanel.add(shipSelector);

        /*--Start Button--*/
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> frame.startBattle());
        pFieldPanel.add(startButton);

        /*--Fügt Spielerfeld-Panel auf Spielerseite (links) hinzu--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--Gegnerseite (rechts) Panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);

        /*--Gegnerfelderstellung (rechte Zellen)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells);
        eSide.add(eField, BorderLayout.CENTER);

        /*--Load/Exit button--*/
        JButton loadButton = new JButton("Load Game");
        loadButton.setEnabled(false);
        JButton exitButton = new JButton("Exit Game");
        exitButton.setEnabled(false);

        /*--Zusätzliches Gegnerfeld-Panel auf Gegnerseite (rechts) für loadButton und exitButton--*/
        JPanel eFieldPanel = new JPanel();
        eFieldPanel.setBackground(Color.BLACK);
        eFieldPanel.add(loadButton);
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
        JPanel field = new JPanel(new GridLayout(x, y));
        field.setBackground(Color.black);

        for (int r = 0; r < x; r++) {
            for (int c = 0; c < y; c++) {
                JButton cell = new JButton();
                cell.setBackground(Color.DARK_GRAY);
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
                        placeShip(row, col);
                    }
                });
            }
        }
    }

    /**
     * Fügt interaktives Tastatursteuerung hinzu, um Schiffe
     * mit 'r' oder 'R' drehen zu können
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

    private int findNextAvailableIndex(int startIndex) {
        for (int i = startIndex; i < shipsLeft.length; i++) {
            if (shipsLeft[i] > 0) return i;
        }
        for (int i = startIndex - 1; i >= 0; i--) {
            if (shipsLeft[i] > 0) return i;
        }
        return -1;
    }
}
