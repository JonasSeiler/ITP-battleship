package src.gui;
import javax.swing.*;

import src.coms.*;
import src.logic.game;

import java.awt.*;
/**
 * Fensteroberfläche, die alle Menübildschirme(Panels) enthält
 * @author Max Steingräber
 */
public class mainframe extends JFrame {
    private CardLayout cLayout;
    private JPanel cPanel;
    /*--shared data--*/
    public gamescreen GameScreen;
    public battlescreen BattleScreen;
    public pregamescreen PreGameScreen;
    public hostpregamescreen Hostpregamescreen;
    public String lastscreen;

    public game logic;
    public NetworkPlayer coms;

    public Color color1 = new Color(20, 30, 50);
    public Color color2 = new Color(0, 100, 160);
    /*
    verschiedene Farben:
    new Color(20, 30, 50) new Color(220, 200, 190));
    */
   
    /**
     * Konstruiert Fensteroberfläche
     */
    public mainframe() {
        this.setTitle("Tidebreaker"); // title of frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits program when hitting the close button
        this.setSize(640, 640); // sets x- and y-dimension

        /*--game logo--*/
        ImageIcon logo = new ImageIcon("../../img/TidebreakerLogo.jpg");
        this.setIconImage(logo.getImage());

        /*--create card layout (for multiple screens)--*/
        cLayout = new CardLayout();
        cPanel = new JPanel(cLayout);

        /*--create different screens--*/
        titlescreen titlescreen = new titlescreen(this);
        settingsscreen settingsscreen = new settingsscreen(this);
        singleplayer singleplayer = new singleplayer(this);
        multiplayer multiplayer = new multiplayer(this);
        joinscreen joinscreen = new joinscreen(this);
        hostscreen hostscreen = new hostscreen(this);
        Hostpregamescreen = new hostpregamescreen(this);
        PreGameScreen = new pregamescreen(this);

        /*--add to cPanel--*/
        cPanel.add(titlescreen, "titlescreen");
        cPanel.add(settingsscreen, "settings");
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
    /**
     * Zeigt ein bestimmtes Menübildschirm an
     * 
     * @param name      Name des Menübildschirms
     */
    public void showScreen(String name) {
        cLayout.show(cPanel, name);
    }

    public boolean setupServer() {
        Server host = (Server) coms;
        try {
            if(host.sendSize(PreGameScreen.gridSize)) {
                if(host.sendShips(PreGameScreen.ships)) {
                    if(host.sendReady()) {
                        return true; 
                    }
                }
            } 
            return false;
            
        } catch (Exception e) {
            System.err.println("failed transmitting the setup variables: " + e);
            return false;
        }
    }

    public void setupClient() {
        Client joinee = (Client) coms;
        try {
            joinee.receiveSetup();
        } catch (Exception e) {
            System.err.println("Failed to recieve Setup variables: " + e);
        }

    }

    public void setupCPU() {
        // setup for the AI opponent
    }

    public void startGamescreen() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        GameScreen = new gamescreen(this, PreGameScreen.ships, PreGameScreen.gridSize);

        if (coms instanceof Server) {
            try {
            setupServer();
            } catch(Exception e) {
                System.err.println(e);
            }
        } else if (coms instanceof Client) {
            try {
                setupClient();
            } catch(Exception e) {
                System.err.println(e);
            }
        } else {
            try {
                setupCPU();
            } catch(Exception e) {
                System.err.println(e);
            }
        }

        cPanel.add(GameScreen, "gamescreen");
        cLayout.show(cPanel, "gamescreen");
        cPanel.revalidate();
        cPanel.repaint();
    }
    /**
     * Startet den Schiffsauswahlbildschirm als Host
     */
    public void startGamescreen_host() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        GameScreen = new gamescreen(this, Hostpregamescreen.ships, Hostpregamescreen.gridSize);

        cPanel.add(GameScreen, "gamescreen");
        cLayout.show(cPanel, "gamescreen");
        cPanel.revalidate();
        cPanel.repaint();
    }
    /**
     * Startet ein Spiel und öffnet den Spielbildschirm
     */
    public void startBattle() {
        if (BattleScreen != null) cPanel.remove(BattleScreen);

        BattleScreen = new battlescreen(this, GameScreen.COR, GameScreen.SHIPS, GameScreen.DIR, GameScreen.gridSize);
        
        if (logic != null) logic = null;
        logic = new game(BattleScreen.gridSize, BattleScreen.SHIPS, BattleScreen, coms);
        BattleScreen.setGame(logic);
        coms.set_game(logic);
        logic.setup_board();

        cPanel.add(BattleScreen, "battlescreen");
        cLayout.show(cPanel, "battlescreen");
        cPanel.revalidate();
        cPanel.repaint();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainframe::new);
    }
}

