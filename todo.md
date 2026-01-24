# need do 
- connect all the classes
    - init NetworkPlayer depending on the screen
    v/
    - uninitialize classes when the user goes far enough back
    - use SwingWorkers everywhere, where it is necessary
    - wait for Client to be updated to not use GameConfig
    - connect all the screens in a logical manner
    - use NetworkPlayer.close() everywhere
- add ui control to game for turn order control
v/
- set 1. or 2. turn depending on if coms isnt Client or not 
v/
- fix big send_shot() issue
- learn how to connect with a game
    - try locally firstv/, then go across pcs, should work fine 
    on different systems because JVM
- try what save_game() does wrong probably
- make save_game() put files in a savestates/ dir
- make load_game() start the gui good
- make load_game() for Bot
- go bug hunting
- write a comprehensable manual
- add @author tags for swag
- UML-Class diagram (eeewww)
- add waiting indicators for all the times you need to wait 
    - host waiting for connect 
    - Client waiting for host to pick parameters
    - Host or Client placing ships

javac -d out src/gui/mainframe.java
java -cp out src.gui.mainframe

