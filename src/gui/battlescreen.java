package src.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import src.coordinate;

public class battlescreen extends JPanel {

    /*--field data--*/
    private JButton[][] pCells;
    private JButton[][] eCells;
    private JPanel pField;
    private JPanel eField;
    /*--shared data--*/
    public coordinate[] COR;
    public int[] SHIPS;
    public boolean[] DIR;
    public int status = 0;
    /*--Matthias' Parameter--*/
    public int gridSize;
    /*--battle state--*/
    private boolean[][] shots;   // where has been shot
    private int[] shipHits;      // hits per ship   

    /*--string combo box ship selector--*/
    // private JComboBox<String> shipSelector;

    // 1. ships -> Länge abwärts sortiert bsp. [5, 5, 4, 3, 3, 2] -> X
    // 2. Richtung: 0 -> rechts, 1 -> unten -> X
    // 3. Koordinate von Zellenposition (x und y-Position/Indizies) -> coordinate class verwenden

    /*--constructor--*/
    public battlescreen(mainframe frame, coordinate[] c, int[] s, boolean[] d, int inGridSize) {
        this.COR = c;
        this.SHIPS = s;
        this.DIR = d;
        this.gridSize = inGridSize;

        //int totalShips = inShips.length;

        /*COR = new coordinate[totalShips];
        DIR = new boolean[totalShips];
        SHIPS = new int[totalShips];*/
        
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
        pField = createField(gridSize, gridSize, pCells, false);
        pSide.add(pField, BorderLayout.CENTER);

        /*--player field panel (left) for combobox and start-button--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);

        /*--build start button--*/
        JButton startButton = new JButton("Start Game");
        startButton.setEnabled(false);
        pFieldPanel.add(startButton);

        /*--add player field panel to player side panel--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--enemy side (right) panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);

        /*--create enemy field (right cells)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells, true);
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
        drawPlayerShips();

        setFocusable(true);
    }

    /*--methods--*/
    private JPanel createField(int a, int b, JButton[][] array, boolean clickable) {
        JPanel field = new JPanel(new GridLayout(a, b));
        field.setBackground(Color.black);

        for (int r = 0; r < a; r++) {
            for (int c = 0; c < b; c++) {
                JButton cell = new JButton();
                cell.setBackground(Color.DARK_GRAY);
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

    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 &&
               r < pCells.length &&
               c < pCells[0].length;
    }

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

    /*--i=0 -> verfehlt (rot); i=1 -> getroffen (gelb); i=2 -> versunken (grün) */
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

    private void onEnemyCellClicked(int x, int y) {
        /*--Jonas' Methode kommt hier hin--*/
        //int status = 0;

        if (status != 4) {
            colorEnemyShip(x, y, 1);
        } else {
            colorEnemyShip(x, y, 2);
            status = 0;
        }
        // colorEnemyShip(x, y, status);
        status++;

        eCells[x][y].setEnabled(false);
    }
}

/*--JavaDocs irgendwas--*/
