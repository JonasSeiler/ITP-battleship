import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class singleplayer extends JPanel {

    public singleplayer(mainframe frame) {
        /*--set layout manager--*/
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.black);

        /*--label--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setFont(new Font("Sans Serif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        /*--button--*/
        JButton newgame = new JButton("New Game");
        newgame.setAlignmentX(Component.CENTER_ALIGNMENT);
        newgame.addActionListener(e -> frame.showScreen("pregamescreen"));
        JButton loadgame = new JButton("Load Game");
        loadgame.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadgame.addActionListener(e -> frame.showScreen("gamescreen"));
        JButton exit = new JButton("Exit");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.addActionListener(e -> frame.showScreen("titlescreen"));

        /*--add to panel--*/
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(30));
        add(newgame);
        add(Box.createVerticalStrut(15));
        add(loadgame);
        add(Box.createVerticalStrut(15));
        add(exit);
        add(Box.createVerticalGlue());
    }
}
