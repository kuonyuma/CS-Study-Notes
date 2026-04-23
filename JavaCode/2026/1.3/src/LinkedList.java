import java.util.List;

public class LinkedList {

    private ListNode head;

    private static class ListNode{
        private int data;
        private ListNode next;
    }
    public void addLast(int data){
        ListNode newnode = new ListNode();
        ListNode curr = head;
        if(head == null){
            head = newnode;
            newnode.data =data;
            newnode.next = null;
            return;
        }
        while(curr.next !=null){
            curr = curr.next;
        }
        curr.next =newnode;
        newnode.data =data;
        newnode.next = null;

    }
    public void addFirst(int data){
        ListNode newnode = new ListNode();
        newnode.data = data;
        newnode.next = head;
        head = newnode;
    }

    //插入坐标之前
    public void addIndex(int index,int data){
        if(index < 0)return;
        if(index == 0){
            addFirst(data);
            return;
        }
        ListNode newnode = new ListNode();
        newnode.data = data;
        ListNode cur = head;
        while(index > 1 && cur!= null){
            cur = cur.next;
            index--;
        }
        if(cur == null){
            return;
        }
        newnode.next = cur.next;
        cur.next = newnode;

    }
    public void display(){
        ListNode curr = head;
        while(curr != null){
            System.out.print(curr.data+ " ");
            curr = curr.next;
        }
    }
    public void clear(){
        head = null;
    }

    public boolean findData(int data){
        ListNode cur = head;
        if(cur == null){
            return false;
        }
        while(cur != null){
            if(cur.data == data){
                return true;
            }
                cur = cur.next;
        }
        return false;
    }

    public void remove(int data) {
        //防止链表为空
        if(head == null){
            return;
        }
        //如果头节点为目标节点
        if(head.data == data){
            head = head.next;
        }
        //找目标节点的前驱节点
        ListNode cur = head;
        while(cur.next != null){
            if(cur.next.data == data){
                cur.next = cur.next.next;
                return;
            }
            cur = cur.next;
        }
    }

    public boolean chkPalindrome(ListNode head) {

         ListNode past = head;
         ListNode slow = head;
        ListNode phead = head;

         int x;
         //先找到中间节点
        while(past != null&&past.next != null){
           past = past.next.next;
           slow = slow.next;
        }
        ListNode cur = slow.next;
        //此时slow已经指向中间节点了
        //将后半部分的节点反转顺序
        while(cur != null){
            ListNode curN = cur.next;
            cur.next = slow;
            slow =cur;
            cur = curN;
        }
        //此时链表已经反转完毕
        //用past开始向后往中间走
        while(slow != phead){
            if(slow.data != phead.data ){
                return false;
            }
            if(head.next == slow){
               return true;
            }
            slow = slow.next;
            phead = phead.next;
        }
        return  true;
    }
    public boolean hasCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;
        while(fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow){
            return true;
            }
        }
        return false;
    }
    //判断链表是否循环并且找到循环入口
    public ListNode detectCycle(ListNode head) {
        if(head ==null){
            return head;
        }
        ListNode past = head;
        ListNode slow = head;
        while(past != null && past.next != null){
            past = past.next.next;
            slow = slow.next;
            if(slow == past){
                //判断入口
                //L == (N-1)C + Y;
                //head从起点开始走，slow从相遇的点位开始走，两者相遇就是L
                ListNode curr = head;
                while(curr != slow){
                    slow = slow.next;
                    curr = curr.next;
                }
                return curr;
            }
        }
        return  null;

    }


}
