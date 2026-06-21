public class Test {
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        bst.insert(8);
        bst.insert(3);
        bst.insert(1);
        bst.insert(6);
        bst.insert(4);
        bst.insert(7);
        bst.insert(14);
        bst.insert(12);
        bst.insert(20);
        bst.insert(11);
        bst.remove(10);
        bst.insert(13);

        BinarySearchTree.TreeNode node =  bst.search(15);
        if(node == null){
            System.out.println("未找到");
        }else{
            System.out.println(node.val);
        }
        bst.remove(14);
    }
}
