#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define BOARD_SIZE 3
#define EMPTY ' '
#define PLAYER 'X'
#define COMPUTER 'O'

typedef struct {
    char board[BOARD_SIZE][BOARD_SIZE];
    int moves;
} Game;

// 初始化棋盘
void initBoard(Game *game) {
    for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
            game->board[i][j] = EMPTY;
        }
    }
    game->moves = 0;
}

// 显示棋盘
void displayBoard(Game *game) {
    printf("\n");
    for (int i = 0; i < BOARD_SIZE; i++) {
        printf(" %c | %c | %c \n", game->board[i][0], game->board[i][1], game->board[i][2]);
        if (i < BOARD_SIZE - 1) {
            printf("-----------\n");
        }
    }
    printf("\n");
}

// 显示位置提示
void displayPositions() {
    printf("\n位置编号（1-9）：\n");
    printf(" 1 | 2 | 3 \n");
    printf("-----------\n");
    printf(" 4 | 5 | 6 \n");
    printf("-----------\n");
    printf(" 7 | 8 | 9 \n\n");
}

// 检查位置是否有效且为空
int isValidMove(Game *game, int pos) {
    if (pos < 1 || pos > 9) return 0;
    int row = (pos - 1) / 3;
    int col = (pos - 1) % 3;
    return game->board[row][col] == EMPTY;
}

// 放置棋子
void makeMove(Game *game, int pos, char player) {
    int row = (pos - 1) / 3;
    int col = (pos - 1) % 3;
    game->board[row][col] = player;
    game->moves++;
}

// 检查是否有赢家
char checkWinner(Game *game) {
    // 检查行
    for (int i = 0; i < BOARD_SIZE; i++) {
        if (game->board[i][0] == game->board[i][1] && 
            game->board[i][1] == game->board[i][2] && 
            game->board[i][0] != EMPTY) {
            return game->board[i][0];
        }
    }

    // 检查列
    for (int j = 0; j < BOARD_SIZE; j++) {
        if (game->board[0][j] == game->board[1][j] && 
            game->board[1][j] == game->board[2][j] && 
            game->board[0][j] != EMPTY) {
            return game->board[0][j];
        }
    }

    // 检查对角线
    if (game->board[0][0] == game->board[1][1] && 
        game->board[1][1] == game->board[2][2] && 
        game->board[0][0] != EMPTY) {
        return game->board[0][0];
    }

    if (game->board[0][2] == game->board[1][1] && 
        game->board[1][1] == game->board[2][0] && 
        game->board[0][2] != EMPTY) {
        return game->board[0][2];
    }

    return EMPTY;
}

// 简单的AI策略
int computerMove(Game *game) {
    // 首先检查是否能赢
    for (int i = 1; i <= 9; i++) {
        if (isValidMove(game, i)) {
            makeMove(game, i, COMPUTER);
            if (checkWinner(game) == COMPUTER) {
                return i;
            }
            // 撤销移动
            int row = (i - 1) / 3;
            int col = (i - 1) % 3;
            game->board[row][col] = EMPTY;
            game->moves--;
        }
    }

    // 检查是否需要阻止玩家
    for (int i = 1; i <= 9; i++) {
        if (isValidMove(game, i)) {
            makeMove(game, i, PLAYER);
            if (checkWinner(game) == PLAYER) {
                // 撤销
                int row = (i - 1) / 3;
                int col = (i - 1) % 3;
                game->board[row][col] = EMPTY;
                game->moves--;
                return i;
            }
            // 撤销移动
            int row = (i - 1) / 3;
            int col = (i - 1) % 3;
            game->board[row][col] = EMPTY;
            game->moves--;
        }
    }

    // 优先选择中心
    if (isValidMove(game, 5)) return 5;

    // 选择角落
    int corners[] = {1, 3, 7, 9};
    for (int i = 0; i < 4; i++) {
        if (isValidMove(game, corners[i])) return corners[i];
    }

    // 随机选择剩余位置
    for (int i = 1; i <= 9; i++) {
        if (isValidMove(game, i)) return i;
    }

    return -1;
}

// 玩家输入
int playerMove(Game *game) {
    int pos;
    while (1) {
        printf("请输入位置（1-9）：");
        if (scanf("%d", &pos) != 1) {
            // 清空输入缓冲
            while (getchar() != '\n');
            printf("输入无效，请重试。\n");
            continue;
        }
        
        if (!isValidMove(game, pos)) {
            printf("该位置无效或已被占用，请重试。\n");
            continue;
        }
        
        return pos;
    }
}

// 主游戏循环
void playGame() {
    Game game;
    char winner;

    printf("欢迎来到三子棋游戏！\n");
    printf("你是 X，电脑是 O。\n");
    displayPositions();

    initBoard(&game);
    displayBoard(&game);

    while (game.moves < 9) {
        // 玩家移动
        printf("你的回合：\n");
        int pos = playerMove(&game);
        makeMove(&game, pos, PLAYER);
        displayBoard(&game);

        winner = checkWinner(&game);
        if (winner != EMPTY) {
            printf("恭喜！你赢了！\n");
            return;
        }

        if (game.moves == 9) {
            printf("平局！\n");
            return;
        }

        // 电脑移动
        printf("电脑思考中...\n");
        pos = computerMove(&game);
        makeMove(&game, pos, COMPUTER);
        printf("电脑选择了位置 %d\n", pos);
        displayBoard(&game);

        winner = checkWinner(&game);
        if (winner != EMPTY) {
            printf("游戏结束！电脑赢了！\n");
            return;
        }
    }

    printf("平局！\n");
}

// 主函数
int main() {
    srand((unsigned)time(NULL));

    char choice;
    do {
        playGame();

        printf("\n是否再玩一局？(y/n)：");
        scanf(" %c", &choice);
    } while (choice == 'y' || choice == 'Y');

    printf("感谢游玩！再见！\n");
    return 0;
}
