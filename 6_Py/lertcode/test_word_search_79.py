class Solution:
    def exist(self, board: list[list[str]], word: str) -> bool:

        x = [1, -1, 0, 0]
        y = [0, 0, 1, -1]

        row_len = len(board)
        col_len = len(board[0])

        marks = [[False] * col_len for _ in range(row_len)]

        def dfs(row: int, col: int, index: int):

            if index == len(word):
                return True

            if row < 0 or row >= row_len:
                return False

            if col < 0 or col >= col_len:
                return False

            if marks[row][col]:
                return False

            if board[row][col] != word[index]:
                return False

            # 使用当前格子
            marks[row][col] = True

            for i in range(4):
                new_row = row + y[i]
                new_col = col + x[i]

                if dfs(new_row, new_col, index + 1):
                    return True

            # 回溯
            marks[row][col] = False

            return False

        for r in range(row_len):
            for c in range(col_len):
                if dfs(r, c, 0):
                    return True

        return False
