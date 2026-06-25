
class Solution {
    public int sumNumbers(TreeNode root) {
        return dfs(root, 0);
    }

    private int dfs(TreeNode root, int val) {
        if (root == null)
            return 0;

        int curVal = val * 10 + root.val;

        if (root.left == null && root.right == null)
            return curVal;

        return dfs(root.left, curVal) + dfs(root.right, curVal);
    }
}