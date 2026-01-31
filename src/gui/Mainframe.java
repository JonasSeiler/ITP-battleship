package src.gui;
import java.io.File; // für das File-Objekt
import javax.swing.JFileChooser; // für das JFileChooser Objekt
// import javax.swing.filechooser.FileNameExtensionFilter; // für den .txt Filter
import javax.swing.*;

import src.coms.*;
import src.logic.Game;

import java.awt.*;
/**
 * Main window, which contains every screen of the game
 * @author Max Steingräber, Matthias Wiese
 */
public class Mainframe extends JFrame {
    private CardLayout cLayout;
    private JPanel cPanel;
    /*--shared data--*/
    public Gamescreen GameScreen;
    public Battlescreen BattleScreen;
    public Pregamescreen PreGameScreen;
    public Pregamescreen2 PreGameScreen2;
    public String lastscreen;
    public Game logic;
    public NetworkPlayer coms;
    int[] ships = null;
    int size = 0;
    public Mainframe frame = this;

    private String color = "navy";
    public String lastscreen2;
    public Colorpair navy = new Colorpair(new Color(20,30,50), new Color(0,100,160));
    public Colorpair beige = new Colorpair(new Color(235, 220, 180), new Color(40, 30, 20));
    public Colorpair deep_ocean = new Colorpair(new Color(15,32,39), new Color(32,58,67));
    public Colorpair colorsheme = new Colorpair(navy.color1, navy.color2);
    /*
    verschiedene Farben:
    new Color(20, 30, 50) new Color(220, 200, 190));
    */
   public int difficulty; // 1 = Easy, 2 = Medium, 3 = Hard
   
    /**
     * Constructor of Mainframe(window), where all screens get added to
     */
    public Mainframe() {
        this.setTitle("Battleship"); // title of frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits program when hitting the close button
        this.setSize(640, 640); // sets x- and y-dimension

        colorsheme.color1 = navy.color1;
        colorsheme.color2 = navy.color2;

        /*--Game logo--*/
        ImageIcon logo = new ImageIcon(System.getProperty("user.dir") + "/img/TidebreakerLogo.jpg");
        this.setIconImage(logo.getImage());

        /*--create card layout (for multiple screens)--*/
        cLayout = new CardLayout();
        cPanel = new JPanel(cLayout);

        /*--create different screens--*/
        Titlescreen Titlescreen = new Titlescreen(this);
        Settingsscreen Settingsscreen = new Settingsscreen(this);
        Game_instructions Game_instructions = new Game_instructions(this);
        Singleplayer Singleplayer = new Singleplayer(this);
        Multiplayer Multiplayer = new Multiplayer(this);
        Joinscreen Joinscreen = new Joinscreen(this);
        Hostscreen Hostscreen = new Hostscreen(this);
        Waitingscreen Waitingscreen = new Waitingscreen(this);
        Joinwaitscreen Joinwaitscreen = new Joinwaitscreen(this);
        PreGameScreen = new Pregamescreen(this);
        PreGameScreen2 = new Pregamescreen2(this);

        /*--add to cPanel--*/
        cPanel.add(Titlescreen, "titlescreen");
        cPanel.add(Settingsscreen, "settings");
        cPanel.add(Game_instructions, "game_instructions");
        cPanel.add(Singleplayer, "singleplayer");
        cPanel.add(Multiplayer, "multiplayer");
        cPanel.add(Waitingscreen, "waitingscreen");
        cPanel.add(PreGameScreen, "pregamescreen");
        cPanel.add(PreGameScreen2, "pregamescreen2");
        cPanel.add(Joinscreen, "joinscreen");
        cPanel.add(Hostscreen, "hostscreen");
        cPanel.add(Joinwaitscreen, "joinwaitscreen");
        add(cPanel);
        setVisible(true);
        pack();
    }
    /**
     * Shows certain screen
     * 
     * @param name      name of specific screen
     */
    public void showScreen(String name) {
        cLayout.show(cPanel, name);
    }

    /**
     * Sets the communication for multiplayer
     */
    public void setupComs() {
        if (coms instanceof Server) {
            ships = PreGameScreen.ships;
            size = PreGameScreen.gridSize;
            Server host = (Server) coms;
                try {
                    host.sendSize(size);
                    host.sendShips(ships);
                } catch (Exception e) {
                    System.err.println("failed transmitting the setup variables: " + e);
                }
        } else if (coms instanceof Client) {
            Client joinee = (Client) coms;
            try {
                joinee.receiveSetup();
            } catch (Exception e) {
                System.err.println("Failed to recieve Setup variables: " + e);
            }
            ships = joinee.ships;
            size = joinee.size;
        } else {
            try {
                ships = PreGameScreen2.ships;
                size = PreGameScreen2.gridSize;
                Bot b = (Bot) coms;
                b.setdifficulty(difficulty);
                b.sendSize(size);
                b.sendShips(ships);
            } catch(Exception e) {
                System.err.println("Bot couldnt receive parameters: " + e);
            }
        }
    }

    /**
     * Starts the 'gamescreen'
     */
    public void startGamescreen() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                setupComs(false);
                return null;
            }
            protected void done() {

                GameScreen = new Gamescreen(frame, ships, size);

                cPanel.add(GameScreen, "gamescreen");
                cLayout.show(cPanel, "gamescreen");
                cPanel.revalidate();
                cPanel.repaint();

            }
        }.execute();
    }
   
    /**
     * Starts the game and opens 'battlescreen'
     */
    public void startBattle(boolean load) {
        if (BattleScreen != null) cPanel.remove(BattleScreen);

        if(!load) { 
            BattleScreen = new Battlescreen(this, GameScreen.COR, GameScreen.SHIPS, GameScreen.DIR, GameScreen.gridSize);
            if (logic != null) logic = null;
            logic = new Game(BattleScreen.gridSize, BattleScreen.SHIPS, coms);
            logic.set_gui(BattleScreen);    
            BattleScreen.setGame(logic);
            coms.set_game(logic);
        } else {
            System.out.println("size: " + logic.size);
            for (int i = 0; i < logic.s_len.length; i++) {
                System.out.println("Ship" + i + " " + logic.s_len[i] + " " + logic.gui_dir[i] + " " + logic.s_heads[i].x + " " + logic.s_heads[i].y);
            }
            BattleScreen = new Battlescreen(this, logic.s_heads, logic.s_len, logic.gui_dir, logic.size);
            logic.set_gui(BattleScreen);
            BattleScreen.setGame(logic);
            logic.load_gui();
            coms.set_game(logic);
        }
         new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                    try {
                        coms.sendReady();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                return null;
            }
            protected void done() {
                logic.setup_board();
                if(coms instanceof Client) {
                    logic.u_turn = 0;
                    logic.start_opp_turn();
                }
                cPanel.add(BattleScreen, "battlescreen");
                cLayout.show(cPanel, "battlescreen");
                cPanel.revalidate();
                cPanel.repaint();
            }
        }.execute();
       
    }

    /**
     * Changes the current background colors to two new ones.
     */
    public void changeColor() {
        if (color == "navy") {
            colorsheme.color1 = beige.color1;
            colorsheme.color2 = beige.color2;
            color = "beige";
        } else if (color == "beige") {
            colorsheme.color1 = deep_ocean.color1;
            colorsheme.color2 = deep_ocean.color2;
            color = "deep ocean";
        } else if (color == "deep ocean") {
            colorsheme.color1 = navy.color1;
            colorsheme.color2 = navy.color2;
            color = "navy";
        }
    }

    /**
     * Allows the player to select an older save file from the “saves” directory.
     * The absolute path of the selected file is stored in a variable.
     * @return {@code true} if a file was successfully selected and {@code false} if the selection was canceled by the player or an error occurred.
     */

    public boolean handleLoadGame() {
        File saveFolder = new File("saves"); // Instanz der Klasse File. Es ist quasi ein Zeiger hinter dem sich ein Ordner oder eine Datei befindet

        if (!saveFolder.exists()) {
            saveFolder.mkdir();
        }
        JFileChooser fileChooser = new JFileChooser(saveFolder); // Objekt wird erstellt, und es wird der saveFolder Ordner als Startpunkt gesetzt, von wo aus der User später eine Datei auswählen kann
        int result = fileChooser.showOpenDialog(this); //öffnet das Fenster, indem der User eine Datei auswählen kann
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile(); // jetzt ist selectedFile ein Zeiger auf die ausgewählte Datei 
            String path = selectedFile.getPath(); // der Pfad zur Datei wird in path gespeichert
            // do all the behind the scenes startGamescreen() thingys
            if (coms instanceof Server) {
                try {
                    Server host = (Server) coms;
                    host.sendLoad(path);
                } catch (Exception e) {

                }
            } else {
                try {
                    Bot comp = (Bot) coms;
                    comp.sendLoad(path);
                } catch(Exception e) {

                }
            }
            if (logic != null) {
                logic = null;
            }
            logic = new Game(1, new int[1], coms);
            logic.load_game(path);  

            startBattle(true);

            System.out.println("Dateiauswahl erfolgreich" + path);

            return true;
        } else if (result == JFileChooser.CANCEL_OPTION){
            System.out.println("Vorgang wurde vom Spieler abgebrochen");
            return false;
        } else if (result == JFileChooser.ERROR_OPTION) {
            System.out.println("Es ist ein Fehler aufgetreten");
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mainframe::new);
    }
}
