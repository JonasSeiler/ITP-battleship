package src;

public class game {
    static board board1;
    static board board2;
    static boolean board_1 = true;
    static boolean board_2 = false;
    static coordinate attack;

    public game(int size, int[] ship_set) {
        board1 = new board(size, ship_set);
        board2 = new board(size, ship_set);
    }

    public static void main(String[] args) {
        while (board1.lost() || board2.lost()) {
            if (board_1 == true) {
                board_1 = false;
                board_2 = true;
                switch (board2.check_hit(attack)) {
                    case 0:
                        // send no hit
                        break;
                    case 1:
                        board2.hit_checked(attack);
                        // send hit
                        board_1 = true;
                        board_2 = false;
                        break;
                    case 2:
                        board2.hit_checked(attack);
                        // send hit
                        if (board2.lost()) {
                            continue;
                        }
                        break;
                }
            } else if (board_2 == true) {
                board_2 = false;
                board_1 = true;
                switch (board1.check_hit(attack)) {
                    case 0:
                        // send no hit
                        break;
                    case 1:
                        board1.hit_checked(attack);
                        // send hit
                        board_2 = true;
                        board_1 = false;
                        break;
                    case 2:
                        board1.hit_checked(attack);
                        // send hit
                        if (board1.lost()) {
                            continue;
                        }
                        break;
                }
            }
        }
    }
}