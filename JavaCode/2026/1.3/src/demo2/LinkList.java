package demo2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LinkList  {
    ListNode head;
    ListNode last;
    int size ;
    class ListNode{
        int data;
        ListNode pre;
        ListNode next;
    }

    public int size() {
        size = 0;
        ListNode curr = head;
        while(curr != null){
            size++;
            curr = curr.next;
        }
        return size;
    }


    public boolean isEmpty() {
        return false;
    }


    public boolean contains(Object o) {
        return false;
    }


    public Iterator iterator() {
        return null;
    }


    public Object[] toArray() {
        return new Object[0];
    }


    public Object[] toArray(Object[] a) {
        return new Object[0];
    }


    public boolean add(Object o) {
        return false;
    }


    public boolean remove(Object o) {
        return false;
    }


    public boolean containsAll(Collection c) {
        return false;
    }


    public boolean addAll(Collection c) {
        return false;
    }


    public boolean addAll(int index, Collection c) {
        return false;
    }


    public boolean removeAll(Collection c) {
        return false;
    }


    public boolean retainAll(Collection c) {
        return false;
    }


    public void clear() {
        ListNode curN = head.next;
        while(head != null){
            head = null;
            head = curN;
            curN = curN.next;
        }
    }


    public Object get(int index) {
        return null;
    }


    public Object set(int index, Object element) {
        return null;
    }




    public Object remove(int index) {
        return null;
    }


    public int indexOf(Object o) {

        return 0;
    }

    public int lastIndexOf(Object o) {
        return 0;
    }


    public ListIterator listIterator() {
        return null;
    }


    public ListIterator listIterator(int index) {
        return null;
    }


    public List subList(int fromIndex, int toIndex) {
        return List.of();
    }

    public void display(){
        ListNode cur = head;
        while(cur != null){
            System.out.println(cur.data + " ");
            cur = cur.next;
        }
    }



}
