package src.gui;

import javax.swing.*;
import java.awt.*;

public class mainframe extends JFrame {
    private CardLayout cLayout;
    private JPanel cPanel;
    /*--shared data--*/
    public gamescreen GameScreen;
    public battlescreen BattleScreen;
    public pregamescreen PreGameScreen;
    public hostpregamescreen Hostpregamescreen;

    public mainframe() {
        this.setTitle("Tidebreaker"); // title of frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits program when hitting the close button
        this.setSize(640, 640); // sets x- and y-dimension
        //this.setLayout(new BorderLayout());
        //this.setVisible(true); // makes this visible

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
        Hostpregamescreen = new hostpregamescreen(this);
        PreGameScreen = new pregamescreen(this);


        /*--add to cPanel--*/
        cPanel.add(titlescreen, "titlescreen");
        cPanel.add(singleplayer, "singleplayer");
        cPanel.add(multiplayer, "multiplayer");
        cPanel.add(PreGameScreen, "pregamescreen");
        cPanel.add(joinscreen, "joinscreen");
        cPanel.add(hostscreen, "hostscreen");
        cPanel.add(Hostpregamescreen,"hostpregamescreen");
        add(cPanel);
        setVisible(true);
        pack();
    }
    /*--public method to switch screens--*/
    public void showScreen(String name) {
        cLayout.show(cPanel, name);
    }

    public void startGamescreen() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        GameScreen = new gamescreen(this, PreGameScreen.ships, PreGameScreen.gridSize);

        cPanel.add(GameScreen, "gamescreen");
        cLayout.show(cPanel, "gamescreen");
        cPanel.revalidate();
        cPanel.repaint();
    }

    public void startGamescreen_host() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        GameScreen = new gamescreen(this, Hostpregamescreen.ships, Hostpregamescreen.gridSize);

        cPanel.add(GameScreen, "gamescreen");
        cLayout.show(cPanel, "gamescreen");
        cPanel.revalidate();
        cPanel.repaint();
    }

    public void startBattle() {
        if (BattleScreen != null) cPanel.remove(BattleScreen);

        BattleScreen = new battlescreen(this, GameScreen.COR, GameScreen.SHIPS, GameScreen.DIR, GameScreen.gridSize);

        cPanel.add(BattleScreen, "battlescreen");
        cLayout.show(cPanel, "battlescreen");
        cPanel.revalidate();
        cPanel.repaint();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainframe::new);
    }
}

