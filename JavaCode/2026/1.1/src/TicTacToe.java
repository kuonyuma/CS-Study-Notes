import java.util.Scanner;

public class TicTacToe {
    private static char[][] board = new char[3][3];
    private static final char PLAYER = 'X';
    private static final char AI = 'O';
    private static final char EMPTY = ' ';

    public static void main(String[] args) {
        initBoard();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Tic-Tac-Toe vs Minimax AI");
        System.out.println("You are " + PLAYER + ", AI is " + AI);
        printBoard();

        while (true) {
            // Player turn
            playerMove(scanner);
            printBoard();
            if (checkWin(PLAYER)) {
                System.out.println("Congratulations! You won!"); // Should not happen with perfect AI
                break;
            }
            if (isBoardFull()) {
                System.out.println("It's a draw!");
                break;
            }

            // AI turn
            System.out.println("AI is thinking...");
            aiMove();
            printBoard();
            if (checkWin(AI)) {
                System.out.println("AI wins!");
                break;
            }
            if (isBoardFull()) {
                System.out.println("It's a draw!");
                break;
            }
        }
        scanner.close();
    }

    private static void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    private static void printBoard() {
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    private static void playerMove(Scanner scanner) {
        int row, col;
        while (true) {
            System.out.print("Enter your move (row [1-3] column [1-3]): ");
            if (scanner.hasNextInt()) {
                row = scanner.nextInt() - 1;
                col = scanner.nextInt() - 1;
            } else {
                System.out.println("Invalid input! Please enter numbers.");
                scanner.next(); // Clear invalid input
                continue;
            }

            if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == EMPTY) {
                board[row][col] = PLAYER;
                break;
            } else {
                System.out.println("This move is not valid");
            }
        }
    }

    private static void aiMove() {
        int bestScore = Integer.MIN_VALUE;
        int moveRow = -1;
        int moveCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = AI;
                    int score = minimax(0, false);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        moveRow = i;
                        moveCol = j;
                    }
                }
            }
        }
        board[moveRow][moveCol] = AI;
    }

    private static int minimax(int depth, boolean isMaximizing) {
        if (checkWin(AI))
            return 10 - depth;
        if (checkWin(PLAYER))
            return depth - 10;
        if (isBoardFull())
            return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = AI;
                        int score = minimax(depth + 1, false);
                        board[i][j] = EMPTY;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        int score = minimax(depth + 1, true);
                        board[i][j] = EMPTY;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private static boolean checkWin(char player) {
        // Check rows, columns, and diagonals
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
