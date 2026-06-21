package demo3;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        MyBInaryTree.TreeNode tree = new MyBInaryTree.TreeNode('a');
        MyBInaryTree bInaryTree = new MyBInaryTree();
        tree = bInaryTree.creatTree();
        bInaryTree.preOder(tree);
        System.out.println();
        List<Integer> list= new ArrayList<>();
        list =  bInaryTree.preorderTraversal(tree);
        for(int x: list){
            System.out.print(x+ " ");
        }
        System.out.println();
        System.out.println("===========================");
        int size = bInaryTree.size(tree);
        System.out.println("节点个数："+ size);
        int size2 = bInaryTree.getLeafNodeCount(tree);
        System.out.println("叶子结点个数="+ size2);
        int size3 = bInaryTree.getKLevelNodeCount(tree,3);
        System.out.println("第k层的节点个数："+ size3);
        System.out.println("===============================");
        bInaryTree.levelOrder(tree);

    }
}
