import book.BookList;
import use.User;
import use.UserManager;

public class Master {

    public static void main(String[] args) {
        BookList booklist = new BookList();
        UserManager userManager = new UserManager();

        User user = userManager.login();
        while (user == null) {
            user = userManager.login();
        }

        while(true){
            int choice = user.menu();
            user.doOperation(choice, booklist);
        }
    }
}