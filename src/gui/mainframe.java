package src.gui;
import javax.swing.*;

import src.coms.*;
import src.logic.game;

import java.awt.*;
/**
 * Fensteroberfläche, die alle Menübildschirme(Panels) enthält
 * @author Max Steingräber, Matthias Wiese
 */
public class mainframe extends JFrame {
    private CardLayout cLayout;
    private JPanel cPanel;
    /*--shared data--*/
    public gamescreen GameScreen;
    public battlescreen BattleScreen;
    public pregamescreen PreGameScreen;
    public pregamescreen2 PreGameScreen2;
    public String lastscreen;
    public game logic;
    public NetworkPlayer coms;
    int[] ships = null;
    int size = 0;
    public mainframe frame = this;

    private String color = "navy";
    public String lastscreen2;
    public colorpair navy = new colorpair(new Color(20,30,50), new Color(0,100,160));
    public colorpair beige = new colorpair(new Color(235, 220, 180), new Color(40, 30, 20));
    public colorpair deep_ocean = new colorpair(new Color(15,32,39), new Color(32,58,67));
    public colorpair colorsheme = new colorpair(navy.color1, navy.color2);
    /*
    verschiedene Farben:
    new Color(20, 30, 50) new Color(220, 200, 190));
    */

   public int difficulty; // 1 = Easy, 2 = Medium, 3 = Hard
   
    /**
     * Konstruiert Fensteroberfläche
     */
    public mainframe() {
        this.setTitle("Tidebreaker"); // title of frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits program when hitting the close button
        this.setSize(640, 640); // sets x- and y-dimension

        colorsheme.color1 = navy.color1;
        colorsheme.color2 = navy.color2;

        /*--game logo--*/
        ImageIcon logo = new ImageIcon(System.getProperty("user.dir") + "/img/TidebreakerLogo.jpg");
        this.setIconImage(logo.getImage());

        /*--create card layout (for multiple screens)--*/
        cLayout = new CardLayout();
        cPanel = new JPanel(cLayout);

        /*--create different screens--*/
        titlescreen titlescreen = new titlescreen(this);
        settingsscreen settingsscreen = new settingsscreen(this);
        game_instructions game_instructions = new game_instructions(this);
        singleplayer singleplayer = new singleplayer(this);
        multiplayer multiplayer = new multiplayer(this);
        joinscreen joinscreen = new joinscreen(this);
        hostscreen hostscreen = new hostscreen(this);
        waitingscreen waitingscreen = new waitingscreen(this);
        joinwaitscreen joinwaitscreen = new joinwaitscreen(this);
        PreGameScreen = new pregamescreen(this);
        PreGameScreen2 = new pregamescreen2(this);

        /*--add to cPanel--*/
        cPanel.add(titlescreen, "titlescreen");
        cPanel.add(settingsscreen, "settings");
        cPanel.add(game_instructions, "game_instructions");
        cPanel.add(singleplayer, "singleplayer");
        cPanel.add(multiplayer, "multiplayer");
        cPanel.add(waitingscreen, "waitingscreen");
        cPanel.add(PreGameScreen, "pregamescreen");
        cPanel.add(PreGameScreen2, "pregamescreen2");
        cPanel.add(joinscreen, "joinscreen");
        cPanel.add(hostscreen, "hostscreen");
        cPanel.add(joinwaitscreen, "joinwaitscreen");
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

    public void setupComs() {

        if (coms instanceof Server) {
            ships = PreGameScreen.ships;
            size = PreGameScreen.gridSize;
            try {
                Server host = (Server) coms;
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
                b.setdifficulty(3);
                b.sendSize(size);
                b.sendShips(ships);
            } catch(Exception e) {
                System.err.println("Bot couldnt receive parameters: " + e);
            }
        }

    }

    public void startGamescreen() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                setupComs();
                return null;
            }
            protected void done() {

                GameScreen = new gamescreen(frame, ships, size);

                cPanel.add(GameScreen, "gamescreen");
                cLayout.show(cPanel, "gamescreen");
                cPanel.revalidate();
                cPanel.repaint();

            }
        }.execute();


    }

    /**
    * GameScreen Objekt wird mit den Daten, die der Benutzer ausgewählt hat erstellt
    */
    public void startGamescreen2() {
        if (GameScreen != null) cPanel.remove(GameScreen);

        GameScreen = new gamescreen(this, PreGameScreen2.ships, PreGameScreen2.gridSize);

        /*if (coms instanceof Server) {
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
        }*/

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
     * ändert die aktuellen Hintergrundfarben auf zwei neue
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
    Handles loading the game.
    */
   
    public void handleLoadGame() {
    /*  JFileChooser fileChooser = new JFileChooser(); // Objekt wird erstellt, dass das typische Fenster öffnet, in dem man Ordner durchsuchen kann

        // Optional: restrict to text files
        fileChooser.setFileFilter( // es werden nur txt Dateien angezeigt
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Save Files (*.txt)", "txt"
            )
        );

        int result = fileChooser.showOpenDialog(this); // Das Programm pausiert bis der Nutzer etwas ausgewählt hat

        if (result == JFileChooser.APPROVE_OPTION) { // prüft ob der Nutzer eine Datei ausgewählt hat
            File selectedFile = fileChooser.getSelectedFile(); // es wird sich das Datei Objekt geholt, welches der Nutzer angeklickt hat. Es enthält Informationen wie Dateiname, Größe und Pfad, aber nicht über den Inhalt

        String path = selectedFile.getAbsolutePath();

        // Pass filename or path to game logic
        gLogic.load_game(selectedFile.getAbsolutePath()); // Pfad zur Datei wird in die gLogic Methode gegeben

        gameSaved = true; // or false, depending on your logic
        } else {
            gameSaved = false;
        }
    */}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(mainframe::new);
    }
}

