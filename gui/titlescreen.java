
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class titlescreen extends JPanel {

    public titlescreen(mainframe frame) {
        /*--set layout manager--*/
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.black);

        /*--label--*/
        JLabel title = new JLabel("Tidebreaker");
        title.setFont(new Font("Sans Serif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        /*--button--*/
        JButton singleplayer = new JButton("Singleplayer");
        singleplayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        singleplayer.addActionListener(e -> frame.showScreen("singleplayer"));
        JButton multiplayer = new JButton("Multiplayer");
        multiplayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        multiplayer.addActionListener(e -> frame.showScreen("multiplayer"));
        JButton settings = new JButton("Settings");
        settings.setAlignmentX(Component.CENTER_ALIGNMENT);
        settings.addActionListener(e -> frame.showScreen("settings"));

        /*--add to panel--*/
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(30));
        add(singleplayer);
        add(Box.createVerticalStrut(15));
        add(multiplayer);
        add(Box.createVerticalStrut(15));
        add(settings);
        add(Box.createVerticalGlue());
    }
}
