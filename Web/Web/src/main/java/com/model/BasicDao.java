package com.model;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.util.List;

public class BasicDao<T> {
    QueryRunner qr = new QueryRunner();


    public int update(String sql,Object...params){
        Connection connection = null;
        try {
            connection = JDBCUtilsByDruid.getConnection();
            int update = qr.update(connection, sql, params);
            return update;
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally {
            JDBCUtilsByDruid.close(null,null,connection);
        }
    }

    public T querySingle(String sql, Class<T> c, Object... params){
        Connection connection = null;
        try{
            connection = JDBCUtilsByDruid.getConnection();
            T query = qr.query(connection, sql, new BeanHandler<>(c), params);
            return query;
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            JDBCUtilsByDruid.close(null,null,connection);
        }
    }

    public List<T> queryMulti(String sql, Class<T> c, Object...params){
        Connection connection = null;
        try{
            connection = JDBCUtilsByDruid.getConnection();
            List<T> list = qr.query(connection, sql, new BeanListHandler<>(c), params);
            return list;
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally {
            JDBCUtilsByDruid.close(null,null,connection);
        }
    }

    public Object queryScale(String sql, Object...params){
        Connection connection = null;
        try {
            connection = JDBCUtilsByDruid.getConnection();
            Object query = qr.query(connection, sql, new ScalarHandler(), params);
            return query;
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            JDBCUtilsByDruid.close(null,null,connection);
        }
    }
}
