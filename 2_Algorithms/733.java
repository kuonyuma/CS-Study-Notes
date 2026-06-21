class Solution {
    //准备两个方向向量
    int[] dx = {0,0,1,-1};
    int[] dy = {1,-1,0,0};

    public int[][] floodFill(int[][] image, int sr, int sc, int color) {

        int oldColor = image[sr][sc];
        if(oldColor == color) return image;

        //准备一个队列实现宽搜
        Queue<int[]> que = new LinkedList<>();

        int rowLength = image.length;
        int colLength = image[0].length;

        que.add(new int[]{sr,sc});
        while(!que.isEmpty()){

            int[] tmp = que.poll();
            int r = tmp[0];
            int c = tmp[1];

            image[r][c] = color;
            //宽搜

            for(int i = 0;i < 4;i++){

                int newR = r + dx[i];
                int newC = c + dy[i];

                if(newR >=0 && newR < rowLength &&
                 newC < colLength && newC >=0 && image[newR][newC] == oldColor){
                    que.add(new int[]{newR,newC});
                }
            }

        }     
        return image;
    }
}