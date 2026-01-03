package gui;
import javax.swing.*;
import java.awt.*;

public class pregamescreen extends JPanel {

    private JSpinner gridSizeSpinner;
    private JTextArea shipInfoArea;

    public pregamescreen(mainframe frame) {

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        /* ------- LEFT SIDE: GRID SIZE INPUT ------- */
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.BLACK);

        JLabel gridLabel = new JLabel("Grid Size (5-30):");
        gridLabel.setForeground(Color.WHITE);
        gridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        gridSizeSpinner = new JSpinner(new SpinnerNumberModel(5, 5, 30, 1));
        gridSizeSpinner.setMaximumSize(new Dimension(120, 30));
        gridSizeSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(gridLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(gridSizeSpinner);
        leftPanel.add(Box.createVerticalGlue());

        add(leftPanel, BorderLayout.WEST);


        /* ------- RIGHT SIDE: SHIP OUTPUT ------- */
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.BLACK);

        JLabel shipsLabel = new JLabel("Ships Used:");
        shipsLabel.setForeground(Color.WHITE);
        shipsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        shipInfoArea = new JTextArea();
        shipInfoArea.setEditable(false);
        shipInfoArea.setBackground(Color.DARK_GRAY);
        shipInfoArea.setForeground(Color.WHITE);
        shipInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        rightPanel.add(shipsLabel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(shipInfoArea), BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);


        /* ------- BOTTOM BUTTONS ------- */
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> frame.showScreen("gamescreen"));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> frame.showScreen("singleplayer"));

        bottomPanel.add(startButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);


        /* ------- GRID SIZE LISTENER ------- */
        gridSizeSpinner.addChangeListener(e -> updateShipInfo());

        /* ------- INITIAL SHIP CALCULATION ------- */
        updateShipInfo();
    }



    /** Calculates ship distribution based on grid size */
    private void updateShipInfo() {
        int size = (int) gridSizeSpinner.getValue();
        int totalCells = size * size;

        int shipCellsTarget = (int) (totalCells * 0.30);  // 30% ship coverage

        // ship sizes: 5, 4, 3, 2
        int[] shipSizes = {5, 4, 3, 2};
        int[] counts = new int[shipSizes.length];

        int used = 0;
        int i = 0;

        while (i < shipSizes.length && used < shipCellsTarget) {
            int shipSize = shipSizes[i];
            if (used + shipSize <= shipCellsTarget) {
                counts[i]++;
                used += shipSize;
            } else {
                i++; // move to next smaller ship
            }
        }

        shipInfoArea.setText(
            "Grid Size: " + size + " x " + size + "\n\n" +
            "Total Cells: " + totalCells + "\n" +
            "Target Ship Cells: " + shipCellsTarget + " (30%)\n\n" +
            "Ships:\n" +
            "  Size 5 : " + counts[0] + "\n" +
            "  Size 4 : " + counts[1] + "\n" +
            "  Size 3 : " + counts[2] + "\n" +
            "  Size 2 : " + counts[3] + "\n\n" +
            "Total Ship Cells Used: " + used + "\n" +
            "Remaining: " + (shipCellsTarget - used)
        );
    }
}
