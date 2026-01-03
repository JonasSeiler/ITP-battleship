package gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.coordinate;

public class gamescreen extends JPanel {

    /*--field data--*/
    private JButton[][] pCells;
    private JButton[][] eCells;
    public JPanel pField;
    private JPanel eField;
    private int placedShipCount = 0;
    /*--shared data--*/
    public coordinate[] COR; // x- and y-coordinate of ships
    public int[] SHIPS; // length of ships
    public boolean[] DIR; // direction of ships 0 -> right; 1 -> down
    public int gridSize;

    /*--ship placement state--*/
    private boolean horizontal = true;
    private int currentShipSize = 2;
    private boolean[][] occupied;

    /*--remaining ships per size--*/
    private int[] shipsLeft;

    /*--hover tracking (for rotation preview)--*/
    private int hoverRow = -1;
    private int hoverCol = -1;

    /*--string combo box ship selector--*/
    private JComboBox<String> shipSelector;

    // 1. ships -> Länge abwärts sortiert bsp. [5, 5, 4, 3, 3, 2] -> X
    // 2. Richtung: 0 -> rechts, 1 -> unten -> X
    // 3. Koordinate von Zellenposition (x und y-Position/Indizies) -> coordinate class verwenden

    /*--constructor--*/
    public gamescreen(mainframe frame) {
        int inGridSize = 15;
        this.gridSize = inGridSize;
        int[] inShips = {5, 4, 4, 3, 3, 3, 2};
        int totalShips = inShips.length;

        COR = new coordinate[totalShips];
        DIR = new boolean[totalShips];
        SHIPS = new int[totalShips];

        /*--ships[0] = 2-sized--
        ----ships[1] = 3-sized--
        ----ships[2] = 4-sized--
        ----ships[3] = 5-sized--*/
        int[] ships = convertShipArray(inShips);
        // int a = pregamescreen.gridSize();
        /*--layout for 'this' panel--*/
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);

        /*--title--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Sans Serif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        this.add(title, BorderLayout.NORTH);

        /*--create game board panel--*/
        JPanel board = new JPanel(new GridLayout(1, 2, 20, 0));
        board.setBackground(Color.black);

        /*--player side (left) panel--*/
        JPanel pSide = new JPanel(new BorderLayout());
        pSide.setBackground(Color.black);

        /*--create player field (left cells)--*/
        pCells = new JButton[gridSize][gridSize];
        pField = createField(gridSize, gridSize, pCells);
        pSide.add(pField, BorderLayout.CENTER);

        /*--boolean player field to know where ships are stored--*/
        occupied = new boolean[gridSize][gridSize];

        /*--clone ships array--*/
        shipsLeft = ships.clone();

        /*--player field panel (left) for combobox and start-button--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);

        /*--build ship selector text--*/
        String[] shipNames = new String[ships.length];
        for (int i = 0; i < ships.length; i++) {
            shipNames[i] = (i + 2) + "-ships: " + ships[i] + "x";
        }

        /*--build ship selector combo-box--*/
        shipSelector = new JComboBox<>(shipNames);
        shipSelector.setSelectedIndex(0);
        currentShipSize = shipSelector.getSelectedIndex() + 2;
        pFieldPanel.add(shipSelector);

        /*--build start button--*/
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> frame.startBattle());
        pFieldPanel.add(startButton);

        /*--add player field panel to player side panel--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--enemy side (right) panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);

        /*--create enemy field (right cells)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells);
        eSide.add(eField, BorderLayout.CENTER);

        /*--build load/exit button--*/
        JButton loadButton = new JButton("Load Game");
        loadButton.setEnabled(false);
        JButton exitButton = new JButton("Exit Game");
        exitButton.setEnabled(false);

        /*--build enemy field panel for buttons--*/
        JPanel eFieldPanel = new JPanel();
        eFieldPanel.setBackground(Color.BLACK);
        eFieldPanel.add(loadButton);
        eFieldPanel.add(exitButton);
        eSide.add(eFieldPanel, BorderLayout.SOUTH);

        /*--add player side and enemy side to game board--*/
        board.add(pSide);
        board.add(eSide);
        this.add(board, BorderLayout.CENTER);

        addPlacementListeners();
        setupKeyBindings();

        setFocusable(true);
    }


    /*--methods--*/
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

    /*--listeners--*/
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

    /*--key bindings for rotation--*/
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


    /*--ship preview (only if ship type still available!)--*/
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


    /*--place ship permanently (only if available!)--*/
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
        
        /*System.out.println(
            "Saved ship #" + placedShipCount +
            " | size=" + SHIPS[placedShipCount] +
            " | dir=" + (DIR[placedShipCount] ? "V" : "H") +
            " | at=(" + COR[placedShipCount].x +
            "," + COR[placedShipCount].y + ")"
        );*/
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


    private void updateComboBoxDisplayAutoSkip() {
        int previousIndex = shipSelector.getSelectedIndex();

        shipSelector.removeAllItems();

        for (int i = 0; i < shipsLeft.length; i++) {
        shipSelector.addItem((i + 2) + "-ships: " + shipsLeft[i] + "x");
        }

    // --- auto-select next valid ship ---
        int newIndex = findNextAvailableIndex(previousIndex);

        if (newIndex != -1) {
        shipSelector.setSelectedIndex(newIndex);
        currentShipSize = newIndex + 2;
        }   else {
        shipSelector.setEnabled(false);
        }
    }


    /*--clear preview (only non-occupied)--*/
    private void clearPreview() {
        for (int r = 0; r < pCells.length; r++) {
            for (int c = 0; c < pCells[r].length; c++) {
                if (!occupied[r][c]) {
                    pCells[r][c].setBackground(Color.DARK_GRAY);
                }
            }
        }
    }

    /*--checks if all ships have been placed yet--*/
    private boolean allShipsPlaced() {
        for (int v : shipsLeft) {
            if (v > 0) return false;
        }
        return true;
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 &&
               r < pCells.length &&
               c < pCells[0].length;
    }

    /*--checks if any adjacent cell (including diagonals) is occupied--*/
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
