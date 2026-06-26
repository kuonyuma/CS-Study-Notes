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
    public TreeNode pruneTree(TreeNode root) {
        return dfs(root);
    }
    private TreeNode dfs(TreeNode root){
        if(root == null) return null;

        TreeNode left = dfs(root.left);
        TreeNode right = dfs(root.right);

        root.left = left;
        root.right =right;

        if(root.left == null && root.right == null && root.val == 0)return null;

        return root;
    }
}