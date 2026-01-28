# need do 
- learn how to connect with a game
    - try locally firstv/, then go across pcs, should work fine 
    on different systems because JVM
- try what save_game() does wrong probably
    - UPDATE: very wrong and throw tons of errors
    v/
    - fix it
    v/
- make save_game() put files in a savestates/ dir
- make load_game() start the gui good
- make load_game() for Bot
- go bug hunting
- write a comprehensable manual
- add @author tags for swag
- UML-Class diagram (eeewww)
- add waiting indicators for all the times you need to wait 
    - host waiting for connect 
    v/
    - Client waiting for host to pick parameters
    v/
    - Host or Client placing ships
    v/
v/
- i think there is a exception thrown when you win
v/
- board.game_over() does not work (meaning either won() or lost() dont work)
v/
- include win/loss pop ups
v/


jar --create --file=battleship.jar --main-class=src.gui.mainframe -C out/ .
