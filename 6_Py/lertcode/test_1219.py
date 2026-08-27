class Solution:
    def getMaximumGold(self, grid: list[list[int]]) -> int:

        x = [1, -1, 0, 0]
        y = [0, 0, 1, -1]
        starts = []

        row_len = len(grid)
        col_len = len(grid[0])
        marked = [[False] * col_len for _ in range(row_len)]

        for r in range(row_len):
            for c in range(col_len):
                if grid[r][c] != 0:
                    starts.append([r, c])
        max_val = 0

        # 返回以该坐标搜索到的做大数值
        def dfs(row, col) -> int:

            marked[row][col] = True
            beast = 0
            for i in range(4):
                new_row = row + y[i]
                new_col = col + x[i]

                if new_col < 0 or new_col >= col_len:
                    continue
                if new_row < 0 or new_row >= row_len:
                    continue
                if marked[new_row][new_col]:
                    continue
                if grid[new_row][new_col] == 0:
                    continue
                beast = max(beast, dfs(new_row, new_col))
            marked[new_row][new_col] = False
            return beast

        for i in range(len(starts)):
            xy = starts[i]
            tmp = dfs(xy[0], xy[1])
            max_val = max(max_val, tmp)
        return max_val
