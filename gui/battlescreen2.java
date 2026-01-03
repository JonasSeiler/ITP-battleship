package gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.coordinate;

public class battlescreen2 extends JPanel {

    /*--field data--*/
    private JButton[][] pCells;
    private JButton[][] eCells;
    private JPanel pField;
    private JPanel eField;
    /*--shared data--*/
    public coordinate[] COR;
    public int[] SHIPS;
    public boolean[] DIR;
    /*--battle state--*/
    private boolean[][] shots;   // where has been shot
    private int[] shipHits;      // hits per ship   

    /*--string combo box ship selector--*/
    private JComboBox<String> shipSelector;

    // 1. ships -> Länge abwärts sortiert bsp. [5, 5, 4, 3, 3, 2] -> X
    // 2. Richtung: 0 -> rechts, 1 -> unten -> X
    // 3. Koordinate von Zellenposition (x und y-Position/Indizies) -> coordinate class verwenden

    /*--constructor--*/
    public battlescreen2(mainframe frame) {
        int gridSize = 15;
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
        // int a = prebattlescreen.gridSize();
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
        shipSelector.setEnabled(false);
        currentShipSize = shipSelector.getSelectedIndex() + 2;
        pFieldPanel.add(shipSelector);

        /*--build start button--*/
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> frame.showScreen("titlescreen"));
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

    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 &&
               r < pCells.length &&
               c < pCells[0].length;
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
}

