public class BinarySearchTree {

     static class TreeNode{
        int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val){
            this.val = val;
        }
    }

    TreeNode root;

     public void insert(int val){
         if(root == null){
             root = new TreeNode(val);
         }
         TreeNode cur = root;
         TreeNode parent = null;

         while(cur != null){
             if(cur.val < val){
                 parent = cur;
                 cur = cur.right;
             }else if(cur.val > val){
                 parent = cur;
                 cur = cur.left;
             }else{
                 //在搜索树中无法存储两个相同的数值
                 return;
             }
         }
         if(parent.val < val){
             parent.right = new TreeNode(val);
         }else{
             parent.left = new TreeNode(val);
         }

     }

    public TreeNode search(int val){
        TreeNode cur = root;
        while(cur != null){
            if(cur.val < val){
                cur = cur.right;
            }else if(cur.val == val){
                return cur;
            }else{
                cur = cur.left;
            }
        }
        return null;
    }

    public void remove(int val){
        TreeNode parent = null;
        TreeNode cur = root;
        while(cur != null){
            if(cur.val > val){
                parent = cur;
                cur = cur.left;
            }else if(cur.val < val){
                parent = cur;
                cur = cur.right;
            }else{
                removeNode(parent,cur);
                return;
            }
        }
    }
    public void removeNode(TreeNode parent,TreeNode cur) {
        if (cur.left == null) {
            if (cur == root) {
                root = root.right;
            } else if (cur == parent.right) {
                parent.right = cur.right;
            } else {
                parent.left = cur.right;
            }
        } else if (cur.right == null) {
            if (cur == root) {
                root = cur.left;
            } else if (cur == parent.left) {
                parent.left = cur.left;
            } else {
                parent.right = cur.left;
            }
        } else {
            TreeNode prev = cur;
            TreeNode next = cur.left;
            boolean flg = false;

            while (next.right != null) {
                prev = next;
                next = next.right;
                flg = true;
            }
            cur.val = next.val;
            if (flg) {
                if (next.left != null) {
                    prev.right = next.left;
                } else {
                    prev.right = null;
                }
            } else {
                prev.left = next.left;
            }

        }
    }
}
