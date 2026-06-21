package board;

import static board.initBoard.BOARD_SIZE;

public class PrintBoard {
    public void printBoard(initBoard board){
        for (int i = 0; i < BOARD_SIZE; i++) {
            // 打印每一行的列
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(" " + board.getBoard(i, j) + " ");

                // 如果不是最后一列，打印竖线分隔符
                if (j < BOARD_SIZE - 1) {
                    System.out.print("|");
                }
            }
            System.out.println(); // 换行

            // 如果不是最后一行，打印水平分隔线
            if (i < BOARD_SIZE - 1) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                    System.out.print("---");
                    if (k < BOARD_SIZE - 1) {
                        System.out.print("+");
                    }
                }
                System.out.println();
            }
        }
    }
}
