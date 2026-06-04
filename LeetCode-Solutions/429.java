/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {
    public List<List<Integer>> levelOrder(Node root) {
        
        //创建一个二维数组
        List<List<Integer>> result = new ArrayList<>();
        //创建一个队列
        Queue<Node> queue = new LinkedList<>();
        if(root == null)
            return result;

        queue.add(root);
        //队列不为空就继续
        while(!queue.isEmpty()){
            //创建一个一维数组
            List<Integer> tmp = new ArrayList<>();
            
            int size = queue.size();

            for(int i = 0; i< size;i++){
                
                Node buf = queue.poll();
                tmp.add(buf.val);

                //压入子元素
                for(Node e: buf.children)
                    queue.add(e);
            }
            
            result.add(tmp);
        }
        return result;
    }
}