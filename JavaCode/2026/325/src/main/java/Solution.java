import java.util.Comparator;
import java.util.PriorityQueue;

class Solution{
    

    class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        //链表相加
        ListNode sumListhead = new ListNode();
        ListNode head = sumListhead;//头节点
        int tmp = 0;
        ListNode cur1 = l1;
        ListNode cur2 = l2;

        while(cur1 != null && cur2 != null){
            int add = (cur1.val +  cur2.val + tmp) % 10;
            ListNode newnode = new ListNode(add);
            tmp = (cur1.val + cur2.val + tmp) / 10;
            newnode.next = head.next;
            head.next = newnode;
            cur1 = cur1.next;
            cur2 = cur2.next;
            head = head.next;
        }
        while(cur1 != null){
            int add = (cur1.val + tmp) % 10;
            ListNode newnode = new ListNode(add);
            tmp = (cur1.val + tmp) / 10;
            newnode.next = head.next;
            head.next = newnode;
            cur1 = cur1.next;
            head = head.next;
        }
        while(cur2 != null){
            int add = (cur2.val + tmp) % 10;
            ListNode newnode = new ListNode(add);
            tmp = (cur2.val + tmp) / 10;
            newnode.next = head.next;
            head.next = newnode;
            cur2 = cur2.next;
            head = head.next;
        }
        if(tmp != 0){
            ListNode newnode = new ListNode(tmp);
            newnode.next = head.next;
            head.next = newnode;
            head = head.next;
        }
        return sumListhead.next;

    }
    //两两反转链表
    public ListNode swapPairs(ListNode node) {
         //头节点
        ListNode Phead  = new ListNode();
        Phead.next = node;
        ListNode head = Phead;

        //如果是偶数个节点 cur != null,如果是奇数个节点则是Next！= null
        while(head.next != null && head.next.next != null){
            ListNode cur = head.next;
            ListNode Next = cur.next;
            ListNode NNext = Next.next;

            //交换
            head.next = Next;
            Next.next = cur;
            cur.next = NNext;

            head = Next;
        }
        return Phead.next;

    }
    public void reorderList(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) {
            return;
        }
        
        // 找到中间节点 - 快慢双指针
        ListNode fast = head;
        ListNode slow = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        
        ListNode mid = slow.next;
        slow.next = null; // 切断前后两部分
        
        // 链表倒置 - 头插法反转右半部分
        ListNode dummy = new ListNode(0); // 创建一个虚拟头节点
        ListNode cur = mid;
        while (cur != null) {
            ListNode next = cur.next; // 保存下一个节点
            cur.next = dummy.next;    // 将当前节点指向虚拟头节点的下一个节点（即旧的插入节点）
            dummy.next = cur;         // 将虚拟头节点指向当前节点（完成头插）
            cur = next;               // 移动到下一个需要处理的节点
        }
        
        // 连接两个链表
        ListNode cur1 = head; // 左链表的头
        ListNode cur2 = dummy.next;  // 右半部分反转后的头
        while (cur1 != null && cur2 != null) {
            ListNode next1 = cur1.next;
            ListNode next2 = cur2.next;
            
            cur1.next = cur2;
            cur2.next = next1;
            
            cur1 = next1;
            cur2 = next2;
        }
    }
    
    //================================================================================
    // 比较器
    private class Comprartor implements Comparator<ListNode> {
        @Override
        public int compare(ListNode o1, ListNode o2) {
            return Integer.compare(o1.val,o2.val);
        }
    } 
    
    //以升序的方式合并多个链表
    public ListNode mergeKLists(ListNode[] lists) {
        int n = lists.length;
        if(lists == null || n == 0) return null;
        PriorityQueue<ListNode> heap = new PriorityQueue<>(new Comprartor());
        for(ListNode e : lists){
            if(e != null){
                heap.offer(e);
            }
        }
        ListNode Phead = new ListNode();
        ListNode point = Phead;
        while(!heap.isEmpty()){
            ListNode min = heap.poll();
            point.next = min;
            point = point.next;
            if(min.next != null){

                heap.offer(min.next);
            }
        }
        return Phead.next;
    }
    /**
     * Definition for singly-linked list.
     * public class ListNode {
     *     int val;
     *     ListNode next;
     *     ListNode() {}
     *     ListNode(int val) { this.val = val; }
     *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
     * }
     */

        //分治的方法
        public ListNode mergeKLists2(ListNode[] lists){
            if(lists == null || lists.length == 0) return null;

            ListNode head = mergerSort(lists,0, lists.length - 1);
            return head;
        }
        public ListNode mergerSort(ListNode[] lists,int l ,int r){
            if(l > r) return null;
            if(l == r) return lists[l];

            int mid = (r - l) / 2 + l;
            ListNode left = mergerSort(lists,l,mid);
            ListNode right = mergerSort(lists,mid + 1,r);

            //合并两个链表
            ListNode tmp = new ListNode();
            ListNode tail = tmp;
            ListNode cur1 = left;
            ListNode cur2 = right;
            while(cur1 != null && cur2 != null){
                //升序
                if(cur1.val <= cur2.val){
                    tail.next = cur1;
                    tail = tail.next;
                    cur1 = cur1.next;
                }else{
                    tail.next = cur2;
                    tail = tail.next;
                    cur2 = cur2.next;
                }
            }
            //这里是平均分，左右节点相差数不会超过一个
            if(cur1 != null)
                tail.next = cur1;
            if(cur2 != null)
                tail.next = cur2;

            return tmp.next;
        }
     //对调k个链表
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1) return head;
        int len = 0;
        ListNode cur = head;
        while(cur != null){
            len++;
            cur = cur.next;
        }
        //有多少组
        int gap = len / k;
        ListNode tmps = new ListNode();//哨兵节点
        cur = tmps;//虚拟链表的指针
        ListNode cur2 = head;//实际链表的指针
        ListNode next;
        ListNode prev = null;
        for (int i = 0; i < gap; i++) {
            //记录对调之前的头节点
            prev = cur2;
            for (int j = 0; j < k; j++) {
                next = cur2.next;
                cur2.next = cur.next;
                cur.next = cur2;
                cur2 = next;
            }
            cur = prev;
        }
        prev.next = cur2;
        return tmps.next;
    }
}
