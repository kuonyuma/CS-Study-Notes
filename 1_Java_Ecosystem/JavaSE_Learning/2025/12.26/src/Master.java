import board.PrintBoard;
import board.initBoard;
import user.Ai;
import user.Player;

public class Master {
    public static void main(String[] args) {
        //初始棋盘
        initBoard board = new initBoard();
        //打印出来看看
        PrintBoard print = new PrintBoard();
        print.printBoard(board);

        Player player = new Player();
        Ai ai = new Ai();
        while (true) {
            // 人先手
            player.set(board);
            print.printBoard(board);

            // 检查玩家下棋后是否结束
            if (Ai.checkGameOver(board, ai)) {
                break;
            }

            // AI下棋
            ai.set(board);
            print.printBoard(board);

            // 检查AI下棋后是否结束
            if (Ai.checkGameOver(board, ai)) {
                break;
            }
        }

    }

}
