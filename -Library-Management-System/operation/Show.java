package operation;
import book.Book;
import book.BookList;
import java.util.Map;
import java.util.Scanner;

public class Show implements Operation{
    @Override
    public void work(BookList bookList) {
        System.out.println("你要查询多少本书？");
        Scanner sc =new Scanner(System.in);
        int k = sc.nextInt();
        if(k == 0){
            System.out.println("数量不可以为0");
            return;
        }
        for(Book book : bookList.getTopKMostExpensiveBooks(k)){
            System.out.println(book);
        }

    }
}
