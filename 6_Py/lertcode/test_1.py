class Solution:
    def solveSudoku(self, board: list[list[str]]) -> None:
        # 标记数组
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxs = [set() for _ in range(9)]

        for r in range(9):
            for c in range(9):
                if board[r][c] == ".":
                    continue
                number = board[r][c]
                box_id = (r // 3) * 3 + (c // 3)
                rows[r].add(number)
                cols[c].add(number)
                boxs[box_id].add(number)

        def dfs(index: int) -> bool:

            if index == 81:
                return True

            r = index // 9
            c = index % 9

            if board[r][c] != ".":
                return dfs(index + 1)

            for i in "123456789":
                if i in rows[r]:
                    continue
                if i in cols[c]:
                    continue
                box_id = (r // 3) * 3 + (c // 3)
                if i in boxs[box_id]:
                    continue

                board[r][c] = str(i)
                rows[r].add(i)
                cols[c].add(i)
                boxs[box_id].add(i)

                if dfs(index + 1):
                    return True

                board[r][c] = "."
                cols[c].remove(i)
                rows[r].remove(i)
                boxs[box_id].remove(i)

            return False

        dfs(0)
