package com.dao;

import com.Info.BookInfo;
import com.model.BasicDao;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @title: Book
 * @Author
 * @Date: 2021/9/12 10:53
 * @Version 1.0
 */

public class BookDAO extends BasicDao<BookInfo> {

    @Test
    public void test(){
//        BookInfo bookInfo = new BookInfo(4, "英语", "老师", 10.0f, "鼻血");
//        update(bookInfo);
        delete(2);
    }

    /**
     * 根据当前用户查询书籍
     * @param userId
     * @return java.util.List<com.Info.BookInfo>
     * */
    public List<BookInfo> getAll(Integer userId){
        String sql = "select * from book where bookid in(select bookId from user_book where userId=?)";
        List<BookInfo> bookInfos = queryMulti(sql, BookInfo.class, userId);
        return bookInfos;
    }

    public void insert(BookInfo bookInfo) {
        String sql = "insert into book values(?,?,?,?,?)";
        update(sql,bookInfo.getBookid(),bookInfo.getBookname(),bookInfo.getAuthor(),bookInfo.getPrice(),bookInfo.getInstruction());
    }

    public void update(BookInfo book) {
        String sql = "update book set bookname=?,author=?,price=?,instruction=? where bookid=?";
        update(sql,book.getBookname(),book.getAuthor(),book.getPrice(),book.getInstruction(),book.getBookid());
    }

    public List<BookInfo> selectLike(BookInfo book,Integer max, Integer min){
        String sql = "select * from book where bookid=? or bookname = ? or author = ? or price between ? and ?";
        List<BookInfo> books = queryMulti(sql, BookInfo.class, book.getBookid(), book.getBookname(), book.getAuthor(), min, max);
        return books;
    }

    public void delete(Integer id) {
        String sql = "delete from book where bookid = ?";
        update(sql,id);
    }

    public void insertUserAndBook(Integer id, Integer bookid) {
        String sql = "insert into user_book values(?,?,?)";
        update(sql,null,id,bookid);
    }
}