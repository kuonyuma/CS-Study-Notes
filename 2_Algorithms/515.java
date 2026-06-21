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
    public List<Integer> largestValues(TreeNode root) {
        //创建一个一维数组存储每层的最大值
        List<Integer> result = new ArrayList<>();
        
        //队列
        Queue<TreeNode> heap = new LinkedList<>();

        if(root == null) return result;

        heap.add(root);
        while(!heap.isEmpty()){

            Integer curMax = Integer.MIN_VALUE;
            
            int size = heap.size();

            for(int i = 0;i < size;i++){
                
                TreeNode node = heap.poll();
                curMax =  Math.max(curMax,node.val);

                if(node.left != null){
                    heap.add(node.left);
                }
                if(node.right != null){
                    heap.add(node.right);
                }
            }
            result.add(curMax);
        }
        return result;
    }
}