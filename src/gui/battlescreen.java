package src.gui;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

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
        pField = createField(gridSize, gridSize, pCells, false);
        pSide.add(pField, BorderLayout.CENTER);

        /*--Zusätzliches Spielerfeld-Panel auf Spielerseite (links) für saveButton--*/
        JPanel pFieldPanel = new JPanel();
        pFieldPanel.setBackground(Color.black);

        /*--saveButton--*/
        JButton saveButton = new JButton("Save Game");
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

        /*--Gegnerfelderstellung (rechte Zellen)--*/
        eCells = new JButton[gridSize][gridSize];
        eField = createField(gridSize, gridSize, eCells, true);
        eSide.add(eField, BorderLayout.CENTER);

        /*--Load/Exit button--*/
        JButton loadButton = new JButton("Confirm Shot");
        loadButton.setEnabled(true);
        loadButton.addActionListener(e -> handleLoadGame());
        // saveButton.addActionListener(e -> gLogic.load_game("data-name.txt"));
        JButton exitButton = new JButton("Exit Game");
        exitButton.setEnabled(true);
        exitButton.addActionListener(e -> handleExitGame());

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
        JPanel field = new JPanel(new GridLayout(row, col));
        field.setBackground(Color.black);

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
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
     * Checkt Clicks auf die Gegenerzellen des Gegnerspielfelds
     * 
     * @param x     x-Koordinate
     * @param y     y-Koordinate
     */
    private void onEnemyCellClicked(int x, int y) {
        int status = gLogic.send_shot(x, y);

        colorEnemyShip(x, y, status);

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
    * Handles loading the game.
    */
    private void handleLoadGame() {
        JFileChooser fileChooser = new JFileChooser();

        // Optional: restrict to text files
        fileChooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Save Files (*.txt)", "txt"
            )
        );

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

        // Pass filename or path to game logic
        gLogic.load_game(selectedFile.getAbsolutePath());

        gameSaved = true; // or false, depending on your logic
        }
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
    during game 'battlescreen' load game button -> false and save game button -> true
    during 'gamescreen' load game button -> true and save game button -> false

    when somebody wins/loses user msg
    change background colors for each specific color theme (beige, dark, blue)
*/
