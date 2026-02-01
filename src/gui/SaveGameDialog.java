package src.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Pop-up dialog, when user saves the game
 */
public class SaveGameDialog extends JDialog {

    public SaveGameDialog(Mainframe frame) {
        super(frame, true); // modal dialog
        setUndecorated(true); // remove default window borders
        setSize(320, 140); // slightly bigger for button
        setLocationRelativeTo(frame); // center on main frame

        // Panel with rounded corners
        JPanel panel = new RoundedPanel(20, frame.colorsheme.color1, frame.colorsheme.color2);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel text = new JLabel("Your game has been successfully saved.");
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Sans Serif", Font.BOLD, 11));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(text, BorderLayout.CENTER);

        // OK button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton okButton = new RoundButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener(e -> dispose()); // close dialog
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        add(panel);
        setVisible(true);
    }
    /**
     * Rounded Panel, where save-pop-up-message sits on
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

            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setComposite(AlphaComposite.SrcOver);
            GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
