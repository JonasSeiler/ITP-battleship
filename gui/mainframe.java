import java.awt.CardLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class mainframe extends JFrame {
    private CardLayout cLayout;
    private JPanel cPanel;

    public mainframe() {
        this.setTitle("Tidebreaker"); // title of frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits program when hitting the close button
        this.setSize(640, 640); // sets x- and y-dimension

        /*--game logo--*/
        ImageIcon logo = new ImageIcon("../img/TidebreakerLogo.jpg");
        this.setIconImage(logo.getImage());

        /*--create card layout (for multiple screens)--*/
        cLayout = new CardLayout();
        cPanel = new JPanel(cLayout);
        
        /*--create different screens--*/
        titlescreen titlescreen = new titlescreen(this);
        singleplayer singleplayer = new singleplayer(this);
        multiplayer multiplayer = new multiplayer(this);
        joinscreen joinscreen = new joinscreen(this);
        hostscreen hostscreen = new hostscreen(this);
        pregamescreen pregamescreen = new pregamescreen(this);
        int gridSize = 15;
        int[] ships = {1,2,3,1};
        gamescreen gamescreen = new gamescreen(this, gridSize, ships);

        
        /*--add to cPanel--*/
        cPanel.add(titlescreen, "titlescreen");
        cPanel.add(singleplayer, "singleplayer");
        cPanel.add(multiplayer, "multiplayer");
        cPanel.add(pregamescreen, "pregamescreen");
        cPanel.add(joinscreen, "joinscreen");
        cPanel.add(hostscreen, "hostscreen");
        cPanel.add(gamescreen, "gamescreen");
        this.add(cPanel);
        this.setVisible(true);
        this.pack();
    }
    /*--public method to switch screens--*/
    public void showScreen(String name) {
        cLayout.show(cPanel, name);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainframe::new);
    }
}

