
class Solution {
    public int sumNumbers(TreeNode root) {
        return dfs(root, val);
    }

    private int dfs(TreeNode root, int val) {
        val = val * 10 + root.val;
        if (root.left == null && root.right == null)
            return 0;
    }
}