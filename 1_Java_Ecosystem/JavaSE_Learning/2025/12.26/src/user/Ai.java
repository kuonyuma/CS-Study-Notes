package user;

import board.initBoard;

import static board.initBoard.*;

public class Ai {
    private static final boolean AI = true;
    private static final boolean USE = false;
    private static final int WIN = 1000;
    private static final int LOSS = -1000;
    private static final int BALANCE = 0;

    //这里三子棋的空间很小所以不用剪枝优化
    private int Minimax(initBoard board,boolean buf,int depth){
        //写出当递结束的条件
        int chek = judge(board);
        if(chek == WIN){
            //这是设置深度的重要，尽量在最少的步骤内胜利
            return WIN -depth;
        }
        if(chek ==LOSS){
            return LOSS +depth;
        }
        if(isFull(board)){
            return BALANCE;
        }

        if(buf){
            int score = -1000;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if(board.getBoard(i,j)==' '){
                        board.setBoard(i,j,COMPUTER);
                        int currScore = Minimax(board,USE,depth+1);
                        board.setBoard(i,j,SPACE);
                        if(currScore>score){
                            score = currScore;
                        }
                    }
                }
            }
            return score;
        }else{
            int scorePlayer = 1000;//保证分数足够大，让人找到最小分数
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if(board.getBoard(i,j)==' '){
                        board.setBoard(i,j,USER);
                        int currScore = Minimax(board,AI,depth+1);
                        board.setBoard(i,j,SPACE);
                        if(scorePlayer>currScore){
                            scorePlayer= currScore;
                        }
                    }
                }
            }
            return scorePlayer;

        }

    }

    public void set(initBoard board){
        //Minimax算法作为ai的大脑
        //暴力循环找空格
        int Score = -1000;//代表分数无线小.Ai必须找到最大的分数
        int row =-1;//
        int col = -1;//
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(board.getBoard(i,j)==' '){
                    //下棋
                    board.setBoard(i,j,COMPUTER);
                    int currScore = Minimax(board,USE,0);
                    //撤销掉，找出这部分分数的多少即可
                    board.setBoard(i,j,SPACE);
                    if(currScore>Score){
                        Score = currScore;
                        row = i;
                        col = j;
                    }
                }
            }
        }
        board.setBoard(row,col,COMPUTER);
    }
    //判断是否结束
    public int judge(initBoard board){
        //行
        for (int i = 0; i < BOARD_SIZE; i++) {
            if((board.getBoard(i,0)==board.getBoard(i,1))&&(board.getBoard(i,1)==board.getBoard(i,2))){
                if(board.getBoard(i,0)!=SPACE){
                    if(board.getBoard(i,0)==COMPUTER){
                        return WIN;
                    }
                    if(board.getBoard(i,0)==USER){
                        return LOSS;
                    }
                }
            }
        }
        //列
        for (int i = 0; i < BOARD_SIZE; i++) {
                if((board.getBoard(0,i)==board.getBoard(1,i))&&(board.getBoard(1,i)==board.getBoard(2,i))){
                    if(board.getBoard(0,i)!=SPACE){
                        if(board.getBoard(0,i)==COMPUTER){
                            return WIN;
                        }
                        if(board.getBoard(0,i)==USER){
                            return LOSS;
                        }
                    }
                }
        }

        //交叉
        if((board.getBoard(0,0)==board.getBoard(1,1))
                &&(board.getBoard(1,1)==board.getBoard(2,2))){
            if(board.getBoard(0,0)==COMPUTER){
                return WIN;
            }
            if(board.getBoard(0,0)==USER){
                return LOSS;
            }
        }
        if((board.getBoard(0,2)==board.getBoard(1,1))
                &&(board.getBoard(1,1)==board.getBoard(2,0))){
            if(board.getBoard(0,2)==COMPUTER){
                return WIN;
            }
            if(board.getBoard(0,2)==USER){
                return LOSS;
            }
        }

        return BALANCE;

    }
    //检测期盼是否已经满了
    private boolean isFull(initBoard board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.getBoard(i, j) == SPACE) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkGameOver(initBoard board, Ai ai) {
        int result = ai.judge(board);
        if (result == 1000) {
            System.out.println("AI 获胜！");
            return true;
        }if (result == -1000) {
            System.out.println("玩家获胜！");
            return true;
        }if (ai.isFull(board)) {
            System.out.println("平局！");
            return true;
        }
        return false;
    }

}

