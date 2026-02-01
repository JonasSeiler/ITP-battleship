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
v/
- make load_game() for Bot
v/
- check if load_game() works for multiplayer
- go bug hunting
v/
- write a comprehensable manual
v/
- add @author tags for swag
v/
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

- make hard, not target surrounding tiles
v/

jar --create --file=battleship.jar --main-class=src.gui.Mainframe -C out/ .
