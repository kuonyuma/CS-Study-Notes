class Solution {
    //向量组
    int[] dx = {1,-1,0,0};
    int[] dy = {0,0,1,-1};
    
    //标记数组
    boolean[][] mark;
    int rowLen;
    int colLen;

    public int numIslands(char[][] grid) {

        rowLen = grid.length;
        colLen = grid[0].length;
        mark = new boolean[rowLen][colLen];
        int result = 0;

        for(int i = 0; i<rowLen;i++){
            for(int j = 0; j< colLen;j++){

                if(grid[i][j] == '1' && !mark[i][j]){
                    result++;
                    act(grid,i,j);
                }
            }
        }    
        return result;
    }
    //排除相连的同性质元素
    public void act(char[][]grid,int i, int j){

        Queue<int[]> que = new LinkedList<>();
        que.add(new int[]{i,j});

        while(!que.isEmpty()){
            int[] tmp = que.poll();
            int r = tmp[0];
            int c = tmp[1];
            mark[r][c] = true;

            for(int k = 0 ;k < 4;k++){
                int newR = r + dx[k];
                int newC = c + dy[k];

                if(newR >= 0 && newR < rowLen &&
                    newC >= 0 && newC < colLen && !mark[newR][newC] && grid[newR][newC] =='1'){
                        que.add(new int[]{newR,newC});
                        mark[newR][newC] = true;
                }
            }
        } 
    }
}