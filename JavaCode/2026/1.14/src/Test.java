
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Test {
    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

    }
    //状态标记法
    class ColorNode{
        boolean mark;
        TreeNode node;
        public ColorNode(TreeNode node,boolean mark){
            this.node = node;
            this.mark = mark;
        }

    }

    //非递归前序打印二叉树。
    public void preorderTraversal(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        if(root == null){
            return;
        }
        stack.push(root);
        TreeNode buf;
        while(!stack.isEmpty()){
            buf = stack.pop();
            System.out.println(buf.val);

            if(buf.right != null){
                stack.push(buf.right);
            }
            if(buf.left != null){
                stack.push(buf.left);
            }
        }

    }
    //中序遍历
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if(root == null){
            return list;
        }
        Stack<ColorNode> stack = new Stack<>();
        ColorNode cnode = new ColorNode(root,false);
        stack.push(cnode);
        while(!stack.isEmpty()){
            ColorNode topnode = stack.pop();
            if(topnode.mark){
             list.add(topnode.node.val);
            }else{
                if(topnode.node.right != null){
                    ColorNode tmp = new ColorNode(topnode.node.right, false);
                    stack.push(tmp);
                }
                ColorNode tmp1 = new ColorNode(topnode.node, true);
                stack.push(tmp1);
                if(topnode.node.left != null){
                    ColorNode tmp = new ColorNode(topnode.node.left, false);
                    stack.push(tmp);
                }
            }
        }
    return list;
    }
    //后序遍历
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if(root == null){
            return list;
        }
        Stack<ColorNode> stack = new Stack<>();
        ColorNode cnode = new ColorNode(root,false);
        stack.push(cnode);
        while(!stack.isEmpty()){
            ColorNode topnode = stack.pop();
            if(topnode.mark){
                list.add(topnode.node.val);
            }else{
                ColorNode tmp1 = new ColorNode(topnode.node, true);
                stack.push(tmp1);
                if(topnode.node.right != null){
                    /*
                    ColorNode tmp = new ColorNode(topnode.node.right, false);
                    stack.push(tmp);*/
                    stack.push(new ColorNode(topnode.node.right,false));
                }
                if(topnode.node.left != null){
                    ColorNode tmp = new ColorNode(topnode.node.left, false);
                    stack.push(tmp);
                }
            }
        }
        return list;
    }
}
