package com.model;

import java.sql.*;

/**
 * @title: DBConnect
 * @Author
 * @Date: 2021/9/12 11:00
 * @Version 1.0
 */
public class DBConnect {
    private static String driverName = "com.mysql.jdbc.Driver";   //驱动程序名
    private static String userName = "root";                      //数据库用户名
    private static String userPwd = "20011104";                     //密码
    private static String dbName = "web";                    //数据库名
    public static Connection getDBconnection() {
        String  url1="jdbc:mysql://localhost/"+dbName;
        String  url2 ="?user="+userName+"&password="+userPwd;
        String  url3="&useUnicode=true&characterEncoding=GB2312";
        String  url =url1+url2+url3;
        try {
            Class.forName(driverName);
            Connection con= DriverManager.getConnection(url);
            return con;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void closeDB(Connection con, PreparedStatement pstm, ResultSet rs){
        try {
            if(rs!=null) rs.close();
            if(pstm!=null) pstm.close();
            if(con!=null) con.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}