package com.Info;

/**
 * @title: BookInfo
 * @Author
 * @Date: 2021/9/12 11:05
 * @Version 1.0
 */
public class BookInfo {
    private Integer bookid;
    private String bookname;
    private String author;
    private Float price;
    private String instruction;

    public BookInfo() {
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "bookid=" + bookid +
                ", bookname='" + bookname + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", instruction='" + instruction + '\'' +
                '}';
    }

    public BookInfo(Integer bookid, String bookname, String author, Float price, String instruction) {
        this.bookid = bookid;
        this.bookname = bookname;
        this.author = author;
        this.price = price;
        this.instruction = instruction;
    }

    public Integer getBookid() {
        return bookid;
    }

    public void setBookid(Integer bookid) {
        this.bookid = bookid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}