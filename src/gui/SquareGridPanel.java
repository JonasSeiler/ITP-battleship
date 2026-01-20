package src.gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

public class SquareGridPanel extends JPanel {
    /**
    * Klasse, die ein quadratisches Spielfeld erstellt
    * 
    * Attribute
    * @param rows      Reihe
    * @param cols      Spalte
    * @param gap       Abstand
    */
    private final int rows;
    private final int cols;
    private final int gap = 2;

    public SquareGridPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setLayout(null);
        setBackground(Color.black);
    }

    @Override
    public void doLayout() {
        int width = getWidth();
        int height = getHeight();

        // size of one square cell
        int cellSize = Math.min(
            (width - (cols - 1) * gap) / cols,
            (height - (rows - 1) * gap) / rows
        );

        int gridWidth = cols * cellSize + (cols - 1) * gap;
        int gridHeight = rows * cellSize + (rows - 1) * gap;

        int startX = (width - gridWidth) / 2;
        int startY = (height - gridHeight) / 2;

        int i = 0;
        for (Component c : getComponents()) {
            int r = i / cols;
            int col = i % cols;

            int x = startX + col * (cellSize + gap);
            int y = startY + r * (cellSize + gap);

            c.setBounds(x, y, cellSize, cellSize);
            i++;
        }
    }
}
