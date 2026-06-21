package org.example;


import java.sql.*;
import java.util.Scanner;

public class  Main {
    public static void main(String[] args) throws ClassNotFoundException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            //2.获取数据库链接
            connection = DBUtil.getConnection();

            //3.接受用户输入
            System.out.println("请输入书籍的全称：");
            Scanner scanner = new Scanner(System.in);
            String buf = scanner.next();
            //4.使用占位符
            String sql = "select id, name, unitprice from goods "+
                    "where name = ? ";
            preparedStatement = connection.prepareStatement(sql);
            // //5.设置参数防止注入
            preparedStatement.setString(1,buf);

            //执行查询
            result = preparedStatement.executeQuery();

            // 7. 处理结果集
            while(result.next()){
            long id = result.getLong("id");
            String name = result.getString("name");
            double unitprice = result.getDouble("unitprice");

            System.out.println("id=" + id + ", name=" + name + ", unitprice=" +
                    unitprice);
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            // 6.释放资源
           DBUtil.close(connection,preparedStatement,result);
        }
    }
}