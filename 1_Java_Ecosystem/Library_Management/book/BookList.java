package book;

import db.BookDao;
import java.util.*;

//有书一定有书架
public class BookList {

    //关于为什么要加final，因为在后续操作中books这个引用没有被修改
    private final Map<String, Book> books = new HashMap<>();
    private final BookDao bookDao = new BookDao();

    //构造函数：先尝试从数据库加载；若为空再初始化默认数据

    public BookList() {
        bookDao.createTableIfNotExists();
        List<Book> bookList = bookDao.findAllBooks();
        for (Book book : bookList) {
            books.put(book.getName(), book);
        }
        if (books.isEmpty()) {
            initDefaultBooks();
        }
    }

    private void initDefaultBooks() {
        addBook(new Book("三国演义", "罗贯中", "历史", 19));
        addBook(new Book("西游记", "吴承恩", "小说", 29));
        addBook(new Book("红楼梦", "曹雪芹", "小说", 35));
        addBook(new Book("水浒传", "施耐庵", "小说", 32));
        addBook(new Book("Java编程思想", "Bruce Eckel", "技术", 89));
        addBook(new Book("算法导论", "Thomas H.Cormen", "技术", 128));
        addBook(new Book("活着", "余华", "小说", 25));
        addBook(new Book("白夜行", "东野圭吾", "推理", 42));
    }

    //得到书本数量
    public int getBoosSize() {
        return books.size();
    }

    //增加图书
    public void addBook(Book book) {
        Book oldBook = books.get(book.getName());
        if (oldBook != null) {
            oldBook.setCount(book.getCount());
            bookDao.updateBookCount(oldBook.getName(), oldBook.getCount());
            return;
        }
        books.put(book.getName(), book);
        bookDao.insertBook(book);
    }

    //删除书籍
    public void dealBook(String name) {
        books.remove(name);
        bookDao.deleteBookByName(name);
    }

    //借书并持久化库存
    public boolean borrowBook(String name) {
        Book book = books.get(name);
        if (book == null || book.getCount() == 0) {
            return false;
        }
        book.setCount(-1);
        bookDao.updateBookCount(name, book.getCount());
        return true;
    }

    //还书并持久化库存
    public boolean returnBook(String name) {
        Book book = books.get(name);
        if (book == null) {
            return false;
        }
        book.setCount(1);
        bookDao.updateBookCount(name, book.getCount());
        return true;
    }


    //查找(精确查找与模糊查找)
    public Book findBook() {
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();

        findBookByTitle(str);
        //模糊搜索
        for (Book book1 : books.values()) {
            String name = book1.getName();
            if (name != null && name.contains(str)) {
                return book1;
            }
        }
        return null;
    }

    //全名查找
    public Book findBookByTitle(String str) {

        //精确搜索
        Book book = books.get(str);
        if (books.get(str) != null) {
            return book;
        }
        return null;

    }

    public void searchBooks() {
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();

        findBookByTitle(str);
        boolean flg = false;
        //模糊搜索
        for (Book book1 : books.values()) {
            String name = book1.getName();
            if (name != null && name.contains(str)) {
                System.out.println(book1);
                flg = true;
            }
        }
        if (!flg) {
            System.out.println("未找到有关" + str + "的书籍");
        }
    }


    //遍历书籍
    public Set<Map.Entry<String, Book>> getAllBooks() {
        return books.entrySet();
    }

    //返回前k个价格最大的书籍
    public List<Book> getTopKMostExpensiveBooks(int k){
        PriorityQueue<Book> heap = new PriorityQueue<>(k,
                (x,y)->{
                    int a1 = x.getPrice();
                    int a2 = y.getPrice();
                    return a1 - a2;
                }
        );

        for(Book book : books.values()){
            heap.offer(book);
            int size = heap.size();
            if(size > k){
                heap.poll();
            }
        }
        List<Book> list = new ArrayList<>();
        while(!heap.isEmpty()){
            list.add(heap.poll());
        }
        Collections.reverse(list);
        return list;
    }

}
