package demo3;


import javax.swing.tree.TreeCellRenderer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MyBInaryTree {
    static int size = 0;
    static int nodesize = 0;

    static class TreeNode {
        private int val;
        private TreeNode left;
        private TreeNode right;


        public TreeNode(int val) {
            this.val = val;
        }

    }

    public TreeNode creatTree() {
        TreeNode A = new TreeNode(1);
        TreeNode B = new TreeNode(2);
        TreeNode C = new TreeNode(3);
        TreeNode D = new TreeNode(4);
        TreeNode E = new TreeNode(5);
        TreeNode F = new TreeNode(6);
        TreeNode G = new TreeNode(7);
        TreeNode H = new TreeNode(8);

        A.left = B;
        A.right = C;
        B.left = D;
        B.right = E;
        E.right = H;
        C.right = G;
        C.left = F;

        return A;
    }

    public static int i = 0;

    //返回当前节点,构建二叉树
    public static TreeNode creatTree(String str) {
        if (str.charAt(i) == '#') {//如果字符为#则不创建新的节点
            i++;
            return null;
        } else {
            TreeNode node = new TreeNode(str.charAt(i));//创建新的节点
            i++;
            node.left = creatTree(str);
            node.right = creatTree(str);
            return node;
        }
    }

    //前序遍历
    public void preOder(TreeNode A) {
        if (A == null) {
            return;
        }
        System.out.print(A.val + " ");
        preOder(A.left);
        preOder(A.right);

    }
    //非遍历的前序打印

    public List<Integer> preorderTraversal(TreeNode root) {
        //思路：根节点的val值返回+左节点的val值返回+右节点的val值返回；
        List<Integer> list = new ArrayList<>();
        if (root == null) {
            return list;
        }
        list.add(root.val);
        list.addAll(preorderTraversal(root.left));
        list.addAll(preorderTraversal(root.right));
        return list;

    }

    //中序遍历
    public void inOder(TreeNode A) {
        if (A == null) {
            return;
        }
        inOder(A.left);
        System.out.println(A.val + " ");
        inOder(A.right);

    }

    //后续遍历
    public void posOder(TreeNode A) {
        if (A == null) {
            return;
        }
        posOder(A.left);
        posOder(A.right);
        System.out.println(A.val + " ");

    }
    //获取树中节点的个数

    public int size(TreeNode root) {
        //节点总数 == 自己 + 左节点 + 右节点；
        if (root == null) {
            return 0;
        }
        size = 1 + size(root.left) + size(root.right);
        return size;
    }
    //获取叶子节点个数

    public int getLeafNodeCount(TreeNode root) {
        //sum == 左叶子节点个数+ 右边叶子节点个数
        if (root == null) {
            return 0;
        } else if (root.left == null && root.right == null) {
            return 1;
        }
        nodesize = getLeafNodeCount(root.left) + getLeafNodeCount(root.right);
        return nodesize;
    }

    //获取第k层有多少个节点
    static int size3 = 0;

    public int getKLevelNodeCount(TreeNode root, int k) {
        //第k层个节点 == 第k-1层的左节点+右节点
        if (root == null) {
            return 0;
        }
        if (k == 1) {
            return 1;
        }
        size = getKLevelNodeCount(root.right, k - 1) +
                getKLevelNodeCount(root.left, k - 1);
        return size;
    }

    //二叉树的高度
    static int hight = 0;

    public int getHeight(TreeNode root) {
        //左树的高度||右数的高度的最大值+1
        if (root == null) {
            return 0;
        }
        int left = getHeight(root.left);
        int right = getHeight(root.right);
        hight = left - right > 0 ? left + 1 : right + 1;
        return hight;
    }

    //查找数值
    public TreeNode find(TreeNode root, int val) {
        if (root == null) return null;

        if (root.val == val) return root;

        TreeNode ret = find(root.left, val);
        if (ret.val == val) return ret;

        TreeNode ret1 = find(root.right, val);
        if (ret1.val == val) return ret1;

        return null;
    }

    //层序遍历。队列实现
    public void levelOrder(TreeNode root) {
        if (root == null) return;
        Queue<TreeNode> que = new LinkedList<>();
        que.offer(root);
        while (!que.isEmpty()) {
            TreeNode buf = que.poll();
            System.out.print(buf.val + " ");
            if (buf.left != null) {
                que.offer(buf.left);
            }
            if (buf.right != null) {
                que.offer(buf.right);
            }
        }
    }

    //判断完全二叉树
    public boolean isCompleteTree(TreeNode root) {
        if (root == null) return false;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean flag = false;
        while (!queue.isEmpty()) {
            TreeNode buf = queue.poll();
            if (buf == null) {
                flag = true;
            } else {
                if (flag) {
                    return false;
                }
                queue.offer(buf.left);
                queue.offer(buf.right);
            }
        }
        return true;
    }

    //判断两棵树是否完全相同
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p != null && q == null || p == null && q != null) {
            return false;
        }
        if (p == null && q == null) {
            return true;
        }
        if (p.val != q.val) {
            return false;
        }
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    //判断是否含有相同子树
    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        //子树不是空,主树为空一定不相等
        if (root == null) {
            return false;
        }
        //当主树不为空。判断以此为节点判断两棵树是否想相同
        if (isSameTree(root.left, subRoot)) return true;
        if (isSameTree(root.right, subRoot)) return true;
        return false;
    }

    //翻转二叉树
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode buf = root.left;
        root.left = root.right;
        root.right = buf;
        invertTree(root.left);
        invertTree(root.right);
        return root;
    }

    //判断是否为对称二叉树
    public boolean isSymmetric(TreeNode root) {
        //左子树== 右子树
        //左子树的左子树 == 右子树的右子树
        if (root == null) {
            return false;
        } else {
            return isSameTree1(root.left, root.left);
        }
    }

    public boolean isSameTree1(TreeNode left, TreeNode right) {
        if (left != null && right == null || left == null && right != null) {
            return false;
        }
        if (right == null && left == null) {
            return true;
        }
        if (left.val != right.val) {
            return false;
        }
        return isSameTree1(left.left, right.right) &&
                isSameTree1(left.right, right.left);
    }
    //判断是否为平衡二叉树
   /* public boolean isBalanced(TreeNode root) {
        if(root == null)return true;
        if(!isBalanced(root.left))return false;
        if(!isBalanced(root.right))return false;

        int buf1 = getHeight(root.left);
        int buf2 = getHeight(root.right);

        return Math.abs(buf1-buf2) <= 1 ;
    }*/
    //====================================================================================
    /*public boolean isBalanced(TreeNode root){
        if(root == null) return true;
        int left = getHeight(root.left);
        int right = getHeight(root.right);

        return Math.abs(left - right)<=1 &&
                isBalanced(root.left) && isBalanced(root.right);

    }*/

    public boolean isBalanced(TreeNode root) {
        //如果返回值为-1就是不平衡
        return gethight2(root) != -1;
    }

    private int gethight2(TreeNode root) {
        if (root == null) return 0;

        int h_left = gethight2(root.left);
        if (h_left == -1) return -1;

        int h_right = gethight2(root.right);
        if (h_right == -1) return -1;

        if (Math.abs(h_right - h_left) > 1) return -1;

        return Math.max(h_left, h_right) + 1;
    }

    //二叉树转为链表
    TreeNode prev = null;
    TreeNode head = null;

    public TreeNode treeToDoublyList(TreeNode root) {
        if (root == null) return null;
        treeToDoublyListchild(root);
        head.left = prev;
        prev.right = head;

        return head;
    }

    private void treeToDoublyListchild(TreeNode root) {
        if (root == null) {
            return;
        }
        treeToDoublyListchild(root.left);
        if (prev == null) {
            head = root;
        } else {
            //prev代表上一个节点
            prev.right = root;
            root.left = prev;
        }
        //节点移动
        prev = root;
        treeToDoublyListchild(root.right);
    }

    public static void oderprinf(TreeNode root) {
        if (root == null) return;
        oderprinf(root.left);
        System.out.print(root.val + " ");
        oderprinf(root.right);
    }


    //从中序，后序遍历构建二叉树
    int index = 0;//全局指针，指引当前节点根的数值
    public TreeNode buildTree(int[] inorder, int[] postorder) {

        index = postorder.length-1;
        return creatTree2(inorder,postorder,0 ,postorder.length-1);
    int left ,int right){
        if(left > right){
            return null;
        }

        int val = postorder[index];
        TreeNode node = new TreeNode(val);
        int rootindex = findIndex(inorder,val);
        index--;


        node.right = creatTree2(inorder,postorder,rootindex+1,right);
        node.left = creatTree2(inorder,postorder,left,rootindex-1);
        return node;
    }
    private static int findIndex(int[] inorder, int target) {
        for (int i = 0; i < inorder.length; i++) {
            if (inorder[i] == target) {
                return i;
            }
        }
        return -1; // 未找到
    }
    public String tree2str(TreeNode root) {
     if(root == null){
         return "";
     }
     //叶子
     if(root.right == null &&root.left == null) {
         return root.val + "";
     } else if(root.right != null){
         return root.val + "("+ tree2str(root.left)+")"+"("+ tree2str(root.right)+ ")";
     }else{
         return root.val + "("+")"+"("+ tree2str(root.right)+ ")";
     }
    }
}
