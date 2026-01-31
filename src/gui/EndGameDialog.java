package src.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EndGameDialog extends JDialog {

    /**
     * Shows a modal dialog with a winning or losing message.
     *
     * @param frame     Reference to the mainframe, used for colors and returning to titlescreen
     * @param message   The message to display, e.g., "You Win!" or "You Lose!"
     */
    public EndGameDialog(Mainframe frame, String message) {
        super(frame, true); // modal
        setUndecorated(true);
        setSize(320, 140);
        setLocationRelativeTo(frame);

        // Rounded panel with gradient
        JPanel panel = new RoundedPanel(20, frame.colorsheme.color1, frame.colorsheme.color2);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Message label
        JLabel text = new JLabel("<html><div style='text-align:center;'>" + message + "</div></html>");
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Sans Serif", Font.BOLD, 14));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(text, BorderLayout.CENTER);

        // OK button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton okButton = new RoundButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener(e -> {
            dispose();
            frame.showScreen("titlescreen"); // back to main menu
        });
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        setBackground(new Color(0, 0, 0, 0));
        add(panel);
        setVisible(true);
    }

    /**
     * Inner class for rounded panel with gradient
     */
    private static class RoundedPanel extends JPanel {
        private int radius;
        private Color color1;
        private Color color2;

        public RoundedPanel(int radius, Color color1, Color color2) {
            super();
            this.radius = radius;
            this.color1 = color1;
            this.color2 = color2;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clear fully transparent background
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw gradient rounded rectangle
            g2.setComposite(AlphaComposite.SrcOver);
            GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
