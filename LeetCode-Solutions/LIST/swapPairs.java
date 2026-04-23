public Solution.ListNode swapPairs(Solution.ListNode node) {
    //头节点
    Solution.ListNode Phead  = new Solution.ListNode();
    Phead.next = node;
    Solution.ListNode head = Phead;

    //如果是偶数个节点 cur != null,如果是奇数个节点则是Next！= null
    while(head.next != null && head.next.next != null){
        Solution.ListNode cur = head.next;
        Solution.ListNode Next = cur.next;
        Solution.ListNode NNext = Next.next;

        //交换
        head.next = Next;
        Next.next = cur;
        cur.next = NNext;

        head = Next;
    }
    return Phead.next;

}