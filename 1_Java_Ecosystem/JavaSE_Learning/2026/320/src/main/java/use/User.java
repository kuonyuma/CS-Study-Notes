package use;

import book.BookList;
import operation.Operation;

public abstract class User {
    public String name;

    public User(String name){
        this.name = name;
    }

   public  abstract int menu();

    //管理者，普通使用者。
    //由于这是父类，我尝试把接口数组定义到这里
    Operation[] works= new Operation[10];

    //操作具体的接口也在这里
    public void doOperation(int choice, BookList booklist){
       works[choice].work(booklist);

    }


}
