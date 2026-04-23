
public Solution.ListNode addTwoNumbers(Solution.ListNode l1, Solution.ListNode l2) {
    //链表相加
    Solution.ListNode sumListhead = new Solution.ListNode();
    Solution.ListNode head = sumListhead;//头节点
    int tmp = 0;
    Solution.ListNode cur1 = l1;
    Solution.ListNode cur2 = l2;

    while(cur1 != null && cur2 != null){
        int add = (cur1.val +  cur2.val + tmp) % 10;
        Solution.ListNode newnode = new Solution.ListNode(add);
        tmp = (cur1.val + cur2.val + tmp) / 10;
        newnode.next = head.next;
        head.next = newnode;
        cur1 = cur1.next;
        cur2 = cur2.next;
        head = head.next;
    }
    while(cur1 != null){
        int add = (cur1.val + tmp) % 10;
        Solution.ListNode newnode = new Solution.ListNode(add);
        tmp = (cur1.val + tmp) / 10;
        newnode.next = head.next;
        head.next = newnode;
        cur1 = cur1.next;
        head = head.next;
    }
    while(cur2 != null){
        int add = (cur2.val + tmp) % 10;
        Solution.ListNode newnode = new Solution.ListNode(add);
        tmp = (cur2.val + tmp) / 10;
        newnode.next = head.next;
        head.next = newnode;
        cur2 = cur2.next;
        head = head.next;
    }
    if(tmp != 0){
        Solution.ListNode newnode = new Solution.ListNode(tmp);
        newnode.next = head.next;
        head.next = newnode;
        head = head.next;
    }
    return sumListhead.next;

}

