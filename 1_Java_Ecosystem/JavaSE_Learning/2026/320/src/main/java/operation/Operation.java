package operation;

import book.BookList;

public interface Operation {
    /*这里我有一个模糊的思路
    * 一个接口统一所有功能
    * 然后存一个数组接口
    * 每个下表对应不同的func
    * */
     void work(BookList booList);
     //问题：如何用？
    //我是在user里面展示的图书用通过user.出来

}
