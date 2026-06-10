class Solution {
    //向量组
    int[] dr = new int[]{0,0,1,-1};
    int[] dc = new int[]{1,-1,0,0};

    //标记数组
    boolean[][]mark;
    int rlen;
    int clen;
    public int maxAreaOfIsland(int[][] grid) {

        int result = 0;
        //获取数组长度
        rlen = grid.length;
        clen = grid[0].length;
        mark = new boolean[rlen][clen];

        //寻找岛屿
        for(int i = 0;i < rlen;i++){
            for(int j = 0;j < clen;j++){

                if(grid[i][j] == 1 && !mark[i][j]){
                     mark[i][j] = true;
                    int buf =  act(grid,i,j);
                     
                    result = Math.max(buf,result);
                    
                }
            }
        }
        return result;
    }

    //统计岛屿大小
    public int act(int[][]board,int i,int j){

        //使用队列实现宽搜
        Queue<int[]> que = new LinkedList<>();
        //count计数
        int count = 1;
      

        que.add(new int[]{i,j});
        //宽搜
        while(!que.isEmpty()){
            int[] tmp = que.poll();
            int r = tmp[0];
            int c = tmp[1];

            for(int k = 0;k < 4;k++){
                int newr = r +dr[k];
                int newc = c + dc[k];
                if(newr >= 0&& newc >=0&&newr < rlen&& newc<clen&&
                    board[newr][newc] == 1  && !mark[newr][newc]){
                    count++;
                    mark[newr][newc] = true;
                    que.add(new int[]{newr,newc});

                }
            }
        }   
        
        return count;
    }
}