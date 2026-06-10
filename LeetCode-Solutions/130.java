class Solution {
    //依旧向量数组
    int[] dr = {0,0,1,-1};
    int[] dc = {1,-1,0,0};

    //数组长度
    int rlen;
    int clen;

    //准备一个标记数组
    boolean[][] mark;
    public void solve(char[][] board) {
        rlen = board.length;
        clen = board[0].length;

        mark = new boolean[rlen][clen];

        //遍历第一排
        for(int i = 0, j =0;j <clen;j++){
            //使用bfs开始填充
            if(board[i][j] == 'O' && !mark[i][j]){
                
                bfs(board,i,j);
            }
        }
        //遍历最后一排
        for(int i = rlen - 1, j = 0;j< clen;j++){
            if(board[i][j] == 'O' && !mark[i][j]){
               
                bfs(board,i,j);
            }
        }
        //遍历第一列
        for(int i = 1, j = 0;i < rlen - 1;i++){
            if(board[i][j] == 'O' && !mark[i][j]){
                
                bfs(board,i,j);
            }
        }
        //遍历最后一列
        for(int j = clen - 1, i = 1;i < rlen - 1;i++){
            if(board[i][j] == 'O' && !mark[i][j]){
              
                bfs(board,i,j);
            }
        }

        //开始填充
        for(int i = 1;i < rlen - 1;i++){
            for(int j = 1;j < clen - 1;j++){
                if(board[i][j] == 'O' && !mark[i][j]){
                    board[i][j] = 'X';
                }   
            }
        }
    }

    public void bfs(char[][] board,int i,int j){

        //创建队列
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{i,j});

        while(!queue.isEmpty()){

            int[] tmp = queue.poll();
            int r = tmp[0];
            int c = tmp[1];
            mark[r][c] = true;
            //宽搜的核心逻辑
            for(int k = 0; k < 4;k++){
                int newr = r + dr[k];
                int newc = c + dc[k];

                if(newc >= 0&& newc < clen && newr >= 0&& newr < rlen
                    && board[newr][newc] == 'O'&& !mark[newr][newc]){
                        mark[newr][newc] = true;
                        queue.add(new int[]{newr,newc});
                }
            }

        }

    }
}