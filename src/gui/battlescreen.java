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
     * Game Attributes
     * 
     * Field Data
     * @param pCells                player cells
     * @param eCells                enemy cells
     * @param pField                player field
     * @param eField                enemy field
     * @param frame                 mainframe (window)
     * @param gameSaved             true -> game was saved
     *                              false -> game wasn't saved
     * @param saveButton            save button
     * @param confirmShotButton     confirms shot
     * @param selectedX             selected x-coordinate
     * @param selectedY             selected y-coordinate
     * @param selectedEnemyCell     
     * 
     * Shared Data
     * @param COR                   coordinates (x, y) of each ship
     * @param SHIPS                 ship length of each ship
     * @param DIR                   direction (0 -> horizontal, 1 -> vertical) of each ship
     * @param status                saves return value of 'send_shot' method
     *                              (0 -> miss   1 -> hit  2 -> sunk)
     * @param gridSize              saves shared grid size
     * @param gLogic                contains game logic and 'send_shot' method
     * 
     */
    private JButton[][] pCells;
    private JButton[][] eCells;
    private JPanel pField;
    private JPanel eField;
    private mainframe frame;
    private boolean gameSaved = false;
    private JButton saveButton;
    private JButton confirmShotButton;
    private int selectedX = -1;
    private int selectedY = -1;
    private JButton selectedEnemyCell = null;
    
    public coordinate[] COR;
    public int[] SHIPS;
    public boolean[] DIR;
    public int status = 0;
    public int gridSize;
    public game gLogic;
    /**
     * Constructor of 'battlescreen'
     * 
     * @param frame         mainframe (window), which contains all screens and manages them
     * @param c             coordinaten (x, y) of each ship
     * @param s             ship length of each ship
     * @param d             direction (false -> vertical, true -> horizontal) of each ship
     * @param inGridSize    shared grid size of {@link hostpregamescreen}
     */
    public battlescreen(mainframe f, coordinate[] c, int[] s, boolean[] d, int inGridSize) {
        /*--saves shared input data--*/
        this.COR = c;
        this.SHIPS = s;
        this.DIR = d;
        this.gridSize = inGridSize;
        this.frame = f;

        /*--layoutmanager of 'this'-panel--*/
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        this.setOpaque(false);

        /*--titel--*/
        JLabel title = new JLabel("Battleship");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
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

        /*--player field (pCells)--*/
        pCells = new JButton[gridSize][gridSize];
        pField = createField(gridSize, gridSize, pCells, false);
        pSide.add(pField, BorderLayout.CENTER);

        /*--playertitle--*/
        JLabel playerTitle = new JLabel("Your Side");
        playerTitle.setHorizontalAlignment(SwingConstants.CENTER);
        playerTitle.setForeground(Color.WHITE);
        playerTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
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

        /*--additional player field panel (left) for saveButton--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);
        pFieldPanel.setOpaque(false);
        pFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        /*--saveButton--*/
        saveButton = new RoundButton("Save Game");
        saveButton.setEnabled(true);
        saveButton.addActionListener(e -> {
            gameSaved = true;
            new SaveGameDialog(frame);
        });
        saveButton.addActionListener(e -> gLogic.save_game());
        pFieldPanel.add(saveButton);

        /*--adds player field panel onto player side panel (left)--*/
        pSide.add(pFieldPanel, BorderLayout.SOUTH);

        /*--enemy side (right) panel--*/
        JPanel eSide = new JPanel(new BorderLayout());
        eSide.setBackground(Color.black);
        eSide.setOpaque(false);

        /*--creating enemy field (right cells)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells, true);
        eSide.add(eField, BorderLayout.CENTER);

        /*--enemyTitle--*/
        JLabel enemyTitle = new JLabel("Enemy Side");
        enemyTitle.setHorizontalAlignment(SwingConstants.CENTER);
        enemyTitle.setForeground(Color.WHITE);
        enemyTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
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

        /*--ConfirmShot/Exit button--*/
        confirmShotButton = new RoundButton("Confirm Shot");
        confirmShotButton.setEnabled(true);
        confirmShotButton.addActionListener(e -> confirmShot());
        JButton exitButton = new RoundButton("Exit Game");
        exitButton.setEnabled(true);
        exitButton.addActionListener(e -> handleExitGame());

        /*--additional enemy field panel onto enemy side (right) for ConfirmShotButton and exitButton--*/
        JPanel eFieldPanel = new JPanel();
        eFieldPanel.setBackground(Color.BLACK);
        eFieldPanel.setOpaque(false);
        eFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        eFieldPanel.add(confirmShotButton);
        eFieldPanel.add(exitButton);
        eSide.add(eFieldPanel, BorderLayout.SOUTH);

        /*--adds player side and enemy side onto game board--*/
        board.add(pSide);
        board.add(eSide);
        this.add(board, BorderLayout.CENTER);
        drawPlayerShips();
        setupKeyBindings(exitButton);
        setFocusable(true);
        //new EndGameDialog(frame, "You Lost!");
    }
    /**
     * Creates playground (grid)
     * 
     * @param row           number of rows
     * @param col           number of columns
     * @param array         button array (cells)
     * @param clickable     determines, whether created field is interactive or not
     * @return              playground
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
     * Checks whether coordinates are within the playing field or not
     * 
     * @param r     x-coordinate
     * @param c     y-coordinate
     * @return      true, if within the playing field
     *              false, if outside of the playing field
     */
    private boolean isInBounds(int r, int c) {
        return r >= 0 && c >= 0 &&
               r < pCells.length &&
               c < pCells[0].length;
    }
    /**
     * Draws all player ships into the player field, which
     * are saved in shared data arrays (COR, SHIPS, DIR)
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
     * Colors player-ships
     * 
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param i     status code (0: red, 1: yellow, 2: green)
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
                if (isInBounds(x-1, y) && pCells[x-1][y].getBackground() == Color.yellow) {
                    colorPlayerShip(x-1, y, i);
                }
                if (isInBounds(x+1, y) && pCells[x+1][y].getBackground() == Color.yellow) {
                    colorPlayerShip(x+1, y, i);
                }
                if (isInBounds(x, y-1) && pCells[x][y-1].getBackground() == Color.yellow) {
                    colorPlayerShip(x, y-1, i);
                }
                if (isInBounds(x, y+1) && pCells[x][y+1].getBackground() == Color.yellow) {
                    colorPlayerShip(x, y+1, i);
                }
                break;
        }
    }
    /**
     * Colors enemy-ships
     * 
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param i     status code (0: red, 1: yellow, 2: green)
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
     * Checks what enemy cell was clicked and marks it
     * 
     * @param x     x-coordinate
     * @param y     y-coordinate
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
        if(gLogic.u_turn == 1) 
            confirmShotButton.setEnabled(true);

        eCells[x][y].setEnabled(false);
    }
    /**
     * Sets game logic
     * 
     * @param g     game logic object
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
            try {
                frame.coms.close();
            } catch(Exception ex) {
                System.err.println("Failed closing connection: " + ex);
            }
            frame.coms = null;
            return;
        }
    }
    /**
     * Disables user interface (saveButton and confirmShotButton)
     */
    public void disableUI() {
        saveButton.setEnabled(false);
        confirmShotButton.setEnabled(false);
    }
    /**
     * Enables user interface (saveButton and confirmShotButton)
     */
    public void enableUI() {
        saveButton.setEnabled(true);
        confirmShotButton.setEnabled(true);
    }
    /**
     * Hit confirmation of ship
     */
    private void confirmShot() {
        if (selectedEnemyCell == null) {
            return;
        }

        gLogic.send_shot(selectedX, selectedY);
    }

    public void shot_answer(int response) {
        colorEnemyShip(selectedX, selectedY, response);

        // Reset selection
        selectedEnemyCell = null;
        selectedX = -1;
        selectedY = -1;
    }
    /**
     * 
     * @param exitButton
     */
    private void setupKeyBindings(JButton exitButton) {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        // ENTER → Confirm Shot
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmShot");
        am.put("confirmShot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (confirmShotButton.isEnabled()) {
                    confirmShotButton.doClick();
                }
            }
        });

        // ESC → Exit Game
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitGame");
        am.put("exitGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitButton.doClick();
            }
        });
    }
    /**
     * Method for color gradient, which is called automatically
     * when new components have to be drawn
     * 
     * @param g     grafic object
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
    -> enter uses confirm shot button
    -> esc uses exit button
    -> selection bug after ship hit
    -> kommentieren -> english
    -> battlescreen Reihenfolge von Schiffen passt nicht so wie gamescreen (COR[], SHIPS[], DIR[])

    when somebody wins/loses user msg
*/
