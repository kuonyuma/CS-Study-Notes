class Solution:
    def solveSudoku(self, board: list[list[str]]) -> None:
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxs = [set() for _ in range(9)]

        for r in range(9):
            for c in range(9):
                if board[r][c] == ".":
                    continue
                number = board[r][c]
                rows[r].add(number)
                cols[c].add(number)
                box_id = (r // 3) * 3 + (c // 3)
                boxs[box_id].add(number)

        def dfs(index: int) -> bool:
            if index == 81:
                return True
            r = index // 9
            c = index % 9
            number = board[r][c]
            if number != ".":
                return dfs(index + 1)
            box_id = (r // 3) * 3 + (c // 3)
            for i in "123456789":
                if i in rows[r]:
                    continue
                if i in cols[c]:
                    continue
                if i in boxs[box_id]:
                    continue

                board[r][c] = i
                rows[r].add(i)
                cols[c].add(i)
                boxs[box_id].add(i)

                if dfs(index + 1):
                    return True

                board[r][c] = "."
                rows[r].remove(i)
                cols[c].remove(i)
                boxs[box_id].remove(i)

            return False

        dfs(0)
