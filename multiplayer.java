import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class multiplayer extends JPanel {

    public multiplayer(mainframe frame) {
        /*--set layout manager--*/
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.black);

        /*--label--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setFont(new Font("Sans Serif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        /*--button--*/
        JButton join = new JButton("Join Game");
        join.setAlignmentX(Component.CENTER_ALIGNMENT);
        join.addActionListener(e -> frame.showScreen("joinscreen"));
        JButton host = new JButton("Host Game");
        host.setAlignmentX(Component.CENTER_ALIGNMENT);
        host.addActionListener(e -> frame.showScreen("hostscreen"));
        JButton exit = new JButton("Exit");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.addActionListener(e -> frame.showScreen("titlescreen"));

        /*--add to panel--*/
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(30));
        add(join);
        add(Box.createVerticalStrut(15));
        add(host);
        add(Box.createVerticalStrut(15));
        add(exit);
        add(Box.createVerticalGlue());
    }
}
