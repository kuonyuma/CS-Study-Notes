package db;

import book.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDao {

    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS books ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "name VARCHAR(100) NOT NULL UNIQUE,"
                + "author VARCHAR(100) NOT NULL,"
                + "type VARCHAR(50) NOT NULL,"
                + "price INT NOT NULL,"
                + "count INT NOT NULL DEFAULT 1,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                + ")";

        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("创建books表失败", e);
        }
    }

    public List<Book> findAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT name, author, type, price, count FROM books";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(rs.getString("name"), rs.getString("author"), rs.getString("type"), rs.getInt("price"));
                book.resetCount(rs.getInt("count"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询图书失败", e);
        }
        return books;
    }

    public void insertBook(Book book) {
        String sql = "INSERT INTO books(name, author, type, price, count) VALUES(?, ?, ?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getType());
            ps.setInt(4, book.getPrice());
            ps.setInt(5, book.getCount());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("新增图书失败", e);
        }
    }

    public void updateBookCount(String name, int count) {
        String sql = "UPDATE books SET count = ? WHERE name = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新库存失败", e);
        }
    }

    public void deleteBookByName(String name) {
        String sql = "DELETE FROM books WHERE name = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除图书失败", e);
        }
    }
}


