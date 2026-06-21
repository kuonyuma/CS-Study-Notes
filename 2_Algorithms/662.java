/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */

 class Pair{
    TreeNode node;
    Integer index;
    public Pair(){

    }
    public Pair(TreeNode node,Integer index){
        this.node = node;
        this.index = index;
    }
}

class Solution {
    public int widthOfBinaryTree(TreeNode root) {

        if(root == null) return 0;

        // 创建一个队列
        Queue<Pair> queue = new LinkedList<>();
        //存储最大宽度
        int maxWidth = 1;
        queue.add(new Pair(root,1));

        while(!queue.isEmpty()){
            //获取当前层数的元素
            int size = queue.size();
            //最左端与最右端
            int left = 0;
            int right = 0;
            //遍历当前层数
            for(int i = 0;i < size;i++){

                Pair pair = queue.poll();
                if(i == 0){
                    left = pair.index;
                }
                if(i == size - 1){
                    right = pair.index;
                }

                // 把自己的孩子节点传入队列
                if(pair.node.left != null){
                    TreeNode node = pair.node.left;
                    int index = pair.index * 2;
                    queue.add(new Pair(node,index));
                }
                
                if(pair.node.right != null){
                    TreeNode node = pair.node.right;
                    int index = pair.index * 2 + 1;
                    queue.add(new Pair(node,index));
                }
            }
            //比较当前层数的宽度
            maxWidth = Math.max(maxWidth,right-left + 1);
        }
        return maxWidth;
    }
}