package board;

public class initBoard {
    //棋盘的大小
    public static final int BOARD_SIZE = 3;
    //用户下棋
    public static final char USER = '*';
    //电脑下棋
    public static final char COMPUTER = '#';
    //空
    public static final char SPACE = ' ';

    //定义棋盘
    private char[][] board = new char[BOARD_SIZE][BOARD_SIZE];

    //初始化棋盘
    public initBoard(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = SPACE;
            }
        }
    }

    //提供棋盘
    public char getBoard(int row,int col) {
        return board[row][col];
    }
    //提供下棋的接口
    public void setBoard(int row,int col,char x){
        if(x==COMPUTER||x==USER||x==SPACE){
            board[row][col] = x;
        }else{
            System.out.println("输入不合法");
        }
    }
}
