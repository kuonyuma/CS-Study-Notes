package demo2;

public class MyQueue {

    //单链表实现队列
    //从尾插入

    class ListNode{
        int val;
        ListNode next;
    }
    ListNode head;
    ListNode last;

    public void add(int val){
        ListNode newnode = new ListNode();
        newnode.val = val;
        newnode.next = null;
        ListNode curlast = last;
        if(curlast == null){
            last = newnode;
        }
        curlast.next = newnode;
        last = newnode;
    }
    public int peek(){
        return last.val;
    }
    public int poll(){
        ListNode cur = head;
        head = cur.next;
        return cur.val;
    }

}
