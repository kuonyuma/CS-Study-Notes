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
class Solution {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        
        // 创建一个二维数组
        List<List<Integer>> result = new ArrayList<>();
        //创建一个队列
        Queue<TreeNode> queue = new LinkedList<>();
        if(root == null)
            return result;
        queue.add(root);

        boolean mark = false;

        while(!queue.isEmpty()){

            //创建一个一维数组
            List<Integer> tmp = new ArrayList<>();   
            //计算当前队列大小
            int size = queue.size();
            for(int i = 0; i < size ;i++){
                TreeNode node = queue.poll();
                tmp.add(node.val);
                if(node.left != null){
                    queue.add(node.left);
                }
                if(node.right != null){
                    queue.add(node.right);
                }
            }
            //判断这个一维数组是否需要反转
            if(mark == false){
                result.add(tmp);
                mark = true;
            }else{
                 List<Integer> buf = new ArrayList<>();
                 for(int i = tmp.size() - 1;i >= 0;i --){
                    buf.add(tmp.get(i)); 
                 }
                 result.add(buf);
                 mark = false;
            }
        }
        return result;
    }
}