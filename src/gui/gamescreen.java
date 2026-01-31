package src.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.logic.*;
/**
 * Gamescreen, where the previously selcted user ships
 * are being placed on the game board
 * @author Max Steingräber
 */
public class Gamescreen extends JPanel {
    /**
     * Game Attributes
     * 
     * Field Data
     * @param pCells                player cells
     * @param eCells                enemy cells
     * @param pField                player field
     * @param eField                enemy field
     * @param placedShipCount   
     * 
     * Ship-Processing-Data
     * @param horizontal            direction (0 -> horizontal, 1 -> vertical)
     * @param currentShipSize       currently selected ship-size
     * @param occupied              array, that stores ship-coordinates
     *                              (0 -> cell free  1 -> cell occupied)   
     * @param shipsLeft             remaining ships per size
     * @param hoverRow              rotation preview (row)
     * @param hoverCol              rotation preview (column)
     * @param shipSelector          selection box for ship-size
     * @param frame                 mainframe (window)
     * @param exitButton            exits the game
     * 
     * Shared Data
     * @param COR                   coordinates (x, y) of each ship
     * @param SHIPS                 ship length of each ship
     * @param DIR                   direction (0 -> horizontal, 1 -> vertical) of each ship
     * @param status                saves return value of 'send_shot' method
     *                              (0 -> miss   1 -> hit  2 -> sunk)
     * @param gridSize              saves shared grid size
     * @param gLogic                contains game logic and 'send_shot' method
     * @param startButton           startButton
     * 
     */
    private JButton[][] pCells;
    private JButton[][] eCells;
    public JPanel pField;
    private JPanel eField;
    private int placedShipCount = 0;

    private boolean horizontal = true;
    private int currentShipSize = 2;
    private boolean[][] occupied;
    private int[] shipsLeft;
    private int hoverRow = -1;
    private int hoverCol = -1;
    private JComboBox<String> shipSelector;
    private Mainframe frame;
    private JButton exitButton;
    private JButton startButton;

    public Coordinate[] COR;
    public int[] SHIPS;
    public boolean[] DIR;
    public int gridSize;
    public int[] ships;
    /**
     * Constructor 'gamescreen'
     * 
     * @param frame         mainframe (window), which contains and manages every screen
     * @param inShips       passed ship-field e.g. {5, 5, 4, 3, 3, 3, 2} of {@link hostpregamescreen}
     * @param inGridSize    passed grid size of {@link hostpregamescreen}
     */
    public Gamescreen(Mainframe f, int[] inShips, int inGridSize) {
        /*--save passed class parameters--*/
        this.gridSize = inGridSize;
        int totalShips = inShips.length;
        this.frame = f;

        COR = new Coordinate[totalShips];
        DIR = new boolean[totalShips];
        SHIPS = new int[totalShips];

        /*--converts to ship-edit-format--
        --ships[0] = 2-sized--
        --ships[1] = 3-sized--
        --ships[2] = 4-sized--
        --ships[3] = 5-sized--*/
        ships = convertShipArray(inShips);

        /*--layoutmanager 'this'-Panel--*/
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        this.setOpaque(false);

        /*--title--*/
        JLabel title = new JLabel("Battleship");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        makeLabelScalable(title, this, 0.05f);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        this.add(title, BorderLayout.NORTH);

        /*--creating game board--*/
        JPanel board = new JPanel(new GridLayout(1, 2, 20, 0));
        board.setBackground(Color.black);
        board.setOpaque(false);

        /*--player side (left) panel--*/
        JPanel pSide = new JPanel(new BorderLayout());
        pSide.setBackground(Color.black);
        pSide.setOpaque(false);

        /*--creating player field (left cells)--*/
        pCells = new JButton[gridSize][gridSize];
        pField = createField(gridSize, gridSize, pCells);

        /*--player title--*/
        JLabel playerTitle = new JLabel("Your Side");
        playerTitle.setHorizontalAlignment(SwingConstants.CENTER);
        playerTitle.setForeground(Color.WHITE);
        playerTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));
        makeLabelScalable(playerTitle, pSide, 0.08f);
        playerTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        /*--grid wrapper (player)--*/
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

        /*--title-grid-wrapper (player)--*/
        JPanel pCenterWrapper = new JPanel(new BorderLayout());
        pCenterWrapper.setBackground(Color.black);
        pCenterWrapper.add(playerTitle, BorderLayout.NORTH);
        pCenterWrapper.add(pCenter, BorderLayout.CENTER);
        pCenterWrapper.setOpaque(false);

        pSide.add(pCenterWrapper, BorderLayout.CENTER);

        /*--array, that saves coordinates of placed ships--*/
        occupied = new boolean[gridSize][gridSize];

        /*--clone ships-field--*/
        shipsLeft = ships.clone();

        /*--additional player field panel (left) for saveButton--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);
        pFieldPanel.setOpaque(false);
        pFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        /*--combobox textfield--*/
        String[] shipNames = new String[ships.length];
        for (int i = 0; i < ships.length; i++) {
            shipNames[i] = (i + 2) + "-ships: " + ships[i] + "x";
        }

        /*--creating 'shipSelector' combobox--*/
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
        startButton.addActionListener(e -> {if (allShipsPlaced())
            startButton.setText("waiting...");
            frame.startBattle();
        });
        pFieldPanel.add(startButton);

        /*--adds player field panel onto player side panel (left)--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--enemy side (right) panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);
        eSide.setOpaque(false);

        /*--creating enemy field (right cells)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells);

        /*--enemyTitle--*/
        JLabel enemyTitle = new JLabel("Enemy Side");
        enemyTitle.setHorizontalAlignment(SwingConstants.CENTER);
        enemyTitle.setForeground(Color.WHITE);
        enemyTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));
        makeLabelScalable(enemyTitle, eSide, 0.08f);
        enemyTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        /*--grid wrapper (enemy)--*/
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

        /*--title-grid wrapper (enemy)--*/
        JPanel eCenterWrapper = new JPanel(new BorderLayout());
        eCenterWrapper.setBackground(Color.black);
        eCenterWrapper.setOpaque(false);
        eCenterWrapper.add(enemyTitle, BorderLayout.NORTH);
        eCenterWrapper.add(eCenter, BorderLayout.CENTER);

        eSide.add(eCenterWrapper, BorderLayout.CENTER);

        /*--exit button--*/
        exitButton = new RoundButton("Exit Game");
        exitButton.setEnabled(true);
        exitButton.addActionListener(e -> handleExitGame());

        /*--additional panel on enemy side (right) for ConfirmShotButton and exitButton--*/
        JPanel eFieldPanel = new JPanel();
        eFieldPanel.setBackground(Color.BLACK);
        eFieldPanel.setOpaque(false);
        eFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        eFieldPanel.add(exitButton);
        eSide.add(eFieldPanel, BorderLayout.SOUTH);

        /*--add player side and enemy side to game board--*/
        board.add(pSide);
        board.add(eSide);
        this.add(board, BorderLayout.CENTER);

        /*--adds interactive game components--*/
        addPlacementListeners();
        setupKeyBindings();
        setFocusable(true);
    }
    /**
     * Creates playground (grid)
     * 
     * @param x         number of rows
     * @param y         number of columns
     * @param array     button array (cells)
     * @return          created playground
     */
    private JPanel createField(int x, int y, JButton[][] array) {
        JPanel field = new SquareGridPanel(x, y);
        field.setOpaque(false);

        for (int r = 0; r < x; r++) {
            for (int c = 0; c < y; c++) {
                JButton cell = new JButton();
                cell.setBackground(Color.BLUE);
                cell.setMargin(new Insets(0, 0, 0, 0));
                cell.setFocusPainted(false);
                array[r][c] = cell;
                field.add(cell);
            }
        }
        return field;
    }
    /**
     * Adds interactive mouse controls (hover, exit, click)
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
     * Adds interactive keyboard controls
     * 'r' or 'R' rotates ship 90 degrees
     * 'z' or 'Z' removes previously placed ship
     * 'ctrl' + 'z' removes all previously placed ships
     * 'esc' presses the 'exitButton'
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

        // ENTER → Confirm Shot
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmShot");
        am.put("confirmShot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startButton.isEnabled()) {
                    startButton.doClick();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitGame");
        am.put("exitGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitButton.doClick();
            }
        });
    }
    /**
     * Ship preview
     * 
     * @param r     selected row
     * @param c     selected column
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
     * Ship placement
     * 
     * @param r     selected row
     * @param c     selected column
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

        /*--place ship, color it dark gray and save it in 'occupied'-array--*/
        for (int i = 0; i < currentShipSize; i++) {
            /*--calculates tile positions of the ship--*/
            int rr = r + (horizontal ? i : 0);
            int cc = c + (horizontal ? 0 : i);

            occupied[rr][cc] = true;
            pCells[rr][cc].setBackground(Color.DARK_GRAY);
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
     * Changes ship selection automatically to the next bigger ship-size,
     * after current ship size is 'empty'
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
     * Resets ship-preview
     */
    private void clearPreview() {
        for (int r = 0; r < pCells.length; r++) {
            for (int c = 0; c < pCells[r].length; c++) {
                if (!occupied[r][c]) {
                    pCells[r][c].setBackground(Color.BLUE);
                }
            }
        }
    }
    /**
     * Checks whether all ships have been placed
     * 
     * @return  true, if all ships have been placed
     *          false, if not all ships have placed
     */
    private boolean allShipsPlaced() {
        for (int v : shipsLeft) {
            if (v > 0) return false;
        }
        return true;
    }
    /**
     * Checks whether coordinates are still within the grid
     * 
     * @param r     x-coordinate
     * @param c     y-coordinate
     * @return      true, if it's inside of the grid
     *              false, if not
     */
    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 &&
               r < pCells.length &&
               c < pCells[0].length;
    }
    /**
     * Checks if the adjacent cell (including diagonally opposite cells) is already occupied
     * 
     * @param r         x-coordinate
     * @param c         y-coordinate
     * @param board     occupied ship-coordinates
     * @return          true, if occupied
     *                  false, if not
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
     * Converts the passed ship-field format to ship-editing format
        --ships[0] = 2-sized--
        --ships[1] = 3-sized--
        --ships[2] = 4-sized--
        --ships[3] = 5-sized--
     * e.g.: {5, 4, 4, 3, 3, 3, 2, 2, 2, 2}  ->  [4, 3, 2, 1]
     *
     * @param ships     passed ship-field format {5, 4, 4, 3, 3, 3, 2, 2, 2, 2}
     * @return          ship-editing format [4, 3, 2, 1]
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
     * Finds next available ship-size selection
     * within the combobox
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
     * Exits the game. If the has not been saved, 
     * the user gets a confirm message
    */
    private void handleExitGame() {
            frame.showScreen("titlescreen");
            try {
                frame.coms.close();
            } catch(Exception ex) {
                System.err.println("Failed closing connection: " + ex);
            }
            frame.coms = null;
            return;
    }
    /**
     * Removes all previously placed ships
     */
    private void undoAllShips() {
        while (placedShipCount > 0) {
            undoLastShip();
        }
    }
    /**
     * Removes the previously (last) placed ship
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
            pCells[rr][cc].setBackground(Color.BLUE);
        }

        shipsLeft[size - 2]++;

        COR[lastIndex] = null;

        placedShipCount--;

        shipSelector.setEnabled(true);
        updateComboBoxDisplayAutoSkip();
    }
    /**
     * Removes ship at specific coordinate
     * 
     * @param r     row (x-coordinate)
     * @param c     column (y-coordinate)
     */
    private void removeShipAt(int r, int c) {
        int index = findShipIndexAt(r, c);
        if (index == -1) return;

        int size = SHIPS[index];
        boolean dir = DIR[index];
        Coordinate start = COR[index];

        // clear board + occupied
        for (int i = 0; i < size; i++) {
            int rr = start.x + (dir ? i : 0);
            int cc = start.y + (dir ? 0 : i);

            occupied[rr][cc] = false;
            pCells[rr][cc].setBackground(Color.BLUE);
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
     * Finds ship-index at specific coordinate
     * 
     * @param r     row (x-coordinate)
     * @param c     column (y-coordinate)
     * @return      index
     */
    private int findShipIndexAt(int r, int c) {
        for (int i = 0; i < placedShipCount; i++) {
            Coordinate start = COR[i];
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
     * Makes label stretchable when frame(window) size changes
     * 
     * @param label         label
     * @param reference     reference panel/component
     * @param factor        scale factor
     */
    private void makeLabelScalable(JLabel label, JComponent reference, float factor) {
        reference.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.max(16, (int)(reference.getWidth() * factor));
                label.setFont(label.getFont().deriveFont((float) size));
            }
        });
    }
    /**
     * Method for color gradient, which is called automatically
     * when new components have to be drawn
     * 
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
