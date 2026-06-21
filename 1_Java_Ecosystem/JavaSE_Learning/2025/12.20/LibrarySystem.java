import java.util.ArrayList;
import java.util.Scanner;

// ==========================================
// 1. 实体类：Book (封装图书信息)
// ==========================================
class Book {
    private String name;
    private String author;
    private double price;
    private String type;
    private boolean isBorrowed; // true表示已借出，false表示未借出

    public Book(String name, String author, double price, String type) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.type = type;
        this.isBorrowed = false; // 默认在馆
    }

    public String getName() {
        return name;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    @Override
    public String toString() {
        return "书名:《" + name + "》" +
                "\t作者:" + author +
                "\t价格:" + price +
                "\t类型:" + type +
                "\t状态:" + (isBorrowed ? "【已借出】" : "【在馆】");
    }
}

// ==========================================
// 2. 数据库模拟：BookList (书架)
// ==========================================
class BookList {
    private ArrayList<Book> books = new ArrayList<>();

    public BookList() {
        // 初始化一些测试数据
        books.add(new Book("三国演义", "罗贯中", 17.0, "小说"));
        books.add(new Book("西游记", "吴承恩", 47.0, "小说"));
        books.add(new Book("Java编程思想", "Bruce Eckel", 108.0, "技术"));
    }

    // 获取某一本书
    public Book getBook(int index) {
        return books.get(index);
    }

    // 设置某一本书（替换）
    public void setBook(int index, Book book) {
        books.set(index, book);
    }

    // 获取当前书的总数
    public int getSize() {
        return books.size();
    }

    // 添加书籍
    public void addBook(Book book) {
        books.add(book);
    }

    // 删除书籍
    public void removeBook(int index) {
        books.remove(index);
    }

    // 根据书名查找书籍的索引，没找到返回-1
    public int findBookIndexByName(String name) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}

// ==========================================
// 3. 业务接口：IOperation (操作的标准)
// ==========================================
interface IOperation {
    void work(BookList bookList);
}

// ------------------------------------------
// 具体业务实现类
// ------------------------------------------

// 【管理员操作】添加图书
class AddOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 添加图书");
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入图书名字: ");
        String name = scanner.next();
        System.out.print("请输入图书作者: ");
        String author = scanner.next();
        System.out.print("请输入图书类型: ");
        String type = scanner.next();
        System.out.print("请输入图书价格: ");
        double price = scanner.nextDouble();

        Book book = new Book(name, author, price, type);
        bookList.addBook(book);
        System.out.println("添加成功！");
    }
}

// 【管理员操作】删除图书
class DelOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 删除图书");
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要删除的图书名字: ");
        String name = scanner.next();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("没有找到这本书，无法删除。");
            return;
        }

        bookList.removeBook(index);
        System.out.println("删除成功！");
    }
}

// 【管理员/用户操作】显示所有图书
class ShowOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 显示图书列表");
        for (int i = 0; i < bookList.getSize(); i++) {
            System.out.println(bookList.getBook(i));
        }
    }
}

// 【管理员/用户操作】查找图书
class FindOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 查找图书");
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要查找的图书名字: ");
        String name = scanner.next();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("未找到此书。");
        } else {
            System.out.println("找到了信息如下: ");
            System.out.println(bookList.getBook(index));
        }
    }
}

// 【用户操作】借阅图书
class BorrowOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 借阅图书");
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要借阅的图书名字: ");
        String name = scanner.next();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("没有找到这本书。");
            return;
        }

        Book book = bookList.getBook(index);
        if (book.isBorrowed()) {
            System.out.println("这本书已经被借走了！");
        } else {
            book.setBorrowed(true);
            System.out.println("借阅成功！");
        }
    }
}

// 【用户操作】归还图书
class ReturnOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println(">>> 归还图书");
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要归还的图书名字: ");
        String name = scanner.next();

        int index = bookList.findBookIndexByName(name);
        if (index == -1) {
            System.out.println("没有找到这本书，无需归还。");
            return;
        }

        Book book = bookList.getBook(index);
        if (book.isBorrowed()) {
            book.setBorrowed(false);
            System.out.println("归还成功！");
        } else {
            System.out.println("这本书在馆内，无需归还。");
        }
    }
}

// 【通用操作】退出系统
class ExitOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("退出系统，欢迎下次光临！");
        System.exit(0);
    }
}

// ==========================================
// 4. 用户体系：User (抽象类及其子类)
// ==========================================
abstract class User {
    protected String name;
    protected IOperation[] operations; // 重点：每个用户持有一个操作数组

    public User(String name) {
        this.name = name;
    }

    public abstract int menu(); // 抽象菜单，由子类实现具体显示

    // 执行操作的方法
    public void doOperation(int choice, BookList bookList) {
        // choice是菜单的选项，对应operations数组的下标
        if (choice >= 0 && choice < operations.length) {
            operations[choice].work(bookList);
        } else {
            System.out.println("输入非法！");
        }
    }
}

// 普通用户
class NormalUser extends User {
    public NormalUser(String name) {
        super(name);
        // 初始化普通用户的操作权限
        this.operations = new IOperation[] {
                new ExitOperation(),
                new FindOperation(),
                new BorrowOperation(),
                new ReturnOperation()
        };
    }

    @Override
    public int menu() {
        System.out.println("============================");
        System.out.println("Hello " + this.name + ", 欢迎来到普通用户菜单");
        System.out.println("1. 查找图书");
        System.out.println("2. 借阅图书");
        System.out.println("3. 归还图书");
        System.out.println("0. 退出系统");
        System.out.println("============================");
        System.out.print("请输入您的操作: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}

// 管理员
class AdminUser extends User {
    public AdminUser(String name) {
        super(name);
        // 初始化管理员的操作权限
        this.operations = new IOperation[] {
                new ExitOperation(),
                new FindOperation(),
                new AddOperation(),
                new DelOperation(),
                new ShowOperation()
        };
    }

    @Override
    public int menu() {
        System.out.println("============================");
        System.out.println("Hello " + this.name + ", 欢迎来到管理员菜单");
        System.out.println("1. 查找图书");
        System.out.println("2. 新增图书");
        System.out.println("3. 删除图书");
        System.out.println("4. 显示所有图书");
        System.out.println("0. 退出系统");
        System.out.println("============================");
        System.out.print("请输入您的操作: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}

// ==========================================
// 5. 程序入口：Main
// ==========================================
public class LibrarySystem {

    // 登录功能
    public static User login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入姓名: ");
        String name = scanner.next();
        System.out.println("请选择你的身份: 1. 管理员  2. 普通用户");
        int choice = scanner.nextInt();

        if (choice == 1) {
            return new AdminUser(name);
        } else {
            return new NormalUser(name);
        }
    }

    public static void main(String[] args) {
        // 1. 准备书籍数据
        BookList bookList = new BookList();

        // 2. 用户登录
        User user = login();

        // 3. 进入主循环
        while (true) {
            // 获取用户选择
            int choice = user.menu();
            // 根据选择执行对应的操作（多态的体现）
            user.doOperation(choice, bookList);

            // 为了视觉效果，暂停一下
            System.out.println("\n按回车键继续...");
            try {
                System.in.read();
            } catch (Exception e) {
            }
        }
    }
}