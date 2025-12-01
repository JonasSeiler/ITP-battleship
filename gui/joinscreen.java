import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class joinscreen extends JPanel {
    public joinscreen(mainframe frame) {
        /*--set layout manager--*/
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.black);

        /*--label--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setFont(new Font("Sans Serif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel wait = new JLabel("waiting for connection...");
        wait.setFont(new Font("Sans Serif", Font.BOLD, 12));
        wait.setForeground(Color.WHITE);
        wait.setAlignmentX(Component.CENTER_ALIGNMENT);

        /*--button--*/
        JButton exit = new JButton("Back");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.addActionListener(e -> frame.showScreen("multiplayer"));

        /*--add to panel--*/
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(30));
        add(wait);
        add(Box.createHorizontalStrut(15));
        add(exit);
        add(Box.createVerticalGlue());
    }
}