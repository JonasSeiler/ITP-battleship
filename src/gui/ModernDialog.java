
package src.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ModernDialog extends JDialog {

    public ModernDialog(JFrame owner, String title) {
        super(owner, title, true);
        setUndecorated(true);
        setSize(420, 220);
        setLocationRelativeTo(owner);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(25, 35, 55),
                        0, getHeight(), new Color(10, 15, 30)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };

        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        content.setLayout(new BorderLayout(10, 10));
        setContentPane(content);
    }
}
