package com.model;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtilsByDruid {
    static DataSource dataSource = null;
    static{
        try {
            Properties properties = new Properties();
            InputStream resource = JDBCUtilsByDruid.class.getClassLoader().getResourceAsStream("druid.properties");
            properties.load(resource);
            //获取连接池
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){

        try {
            return dataSource.getConnection();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void close(Statement statement, ResultSet set, Connection connection){
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(set != null){
            try {
                set.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
