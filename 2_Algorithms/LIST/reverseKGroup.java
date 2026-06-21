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
class Solution {
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