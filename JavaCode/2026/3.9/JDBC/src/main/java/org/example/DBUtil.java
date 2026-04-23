package org.example;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class DBUtil {

    private  DBUtil(){};


//    Connection connection = null;
//    PreparedStatement preparedStatement = null;
//    ResultSet resultset = null;

    private static  HikariDataSource dataSource = null;
    //加载驱动
    static{
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test_310?" +
                "characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false");
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource = new HikariDataSource(config);
    }
    public static DataSource getDataSource() {
        return dataSource;
    }

    //数据库连接
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    //释放资源
    public static void close(Connection connection,
                             PreparedStatement preparedStatement,ResultSet resultset)  {

        if(resultset != null){
            try{
                resultset.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if(preparedStatement != null){
            try{
                preparedStatement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if(connection != null){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }






}
