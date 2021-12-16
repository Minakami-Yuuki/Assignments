package com.control;

import com.Info.BookInfo;
import com.Info.User;
import com.dao.BookDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BookServlet extends BaseServlet {

    BookDAO bookDAO = new BookDAO();

    public void insert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Integer bookid = Integer.parseInt(request.getParameter("bookid"));
        String bookname = request.getParameter("bookname");
        String author = request.getParameter("author");
        String instruction = request.getParameter("instruction");
        Float price = Float.parseFloat(request.getParameter("price"));
        BookInfo info = new BookInfo(bookid, bookname, author, price, instruction);
        System.out.println(info);
        bookDAO.insert(info);
        User user = (User) request.getSession().getAttribute("user");
        Integer id = user.getId();
        bookDAO.insertUserAndBook(id,info.getBookid());
        request.getRequestDispatcher("/view/success.jsp").forward(request,response);
    }

    public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        String s = request.getParameter("bookid");

        if(s != null && s.length() != 0) {
            Integer bookid = Integer.parseInt(s);
            bookDAO.delete(bookid);
            response.sendRedirect(request.getContextPath() + "/view/success.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/view/error.jsp");
        }
    }

    public void findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        User user = (User) request.getSession().getAttribute("user");
        Integer id = user.getId();
        List<BookInfo> books = bookDAO.getAll(id);
        request.setAttribute("books",books);
        request.getRequestDispatcher("/view/find_all.jsp").forward(request,response);
    }

    public void findBookByLike(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer bookid = 0;
        String s = request.getParameter("bookid");
        if (s != null && s.length() != 0) {
            bookid = Integer.parseInt(s);
        }
        String bookname = request.getParameter("bookname");
        String author = request.getParameter("author");
        String p1 = request.getParameter("p1");
        Integer min = 0;
        if (p1 != null && p1.length() != 0) {
            min = Integer.parseInt(p1);
        }
        Integer max = 1000;
        String p2 = request.getParameter("p2");
        if (p2 != null && p2.length() != 0) {
            max = Integer.parseInt(p2);
        }
        BookInfo info = new BookInfo(bookid, bookname, author, null, null);
        List<BookInfo> books = bookDAO.selectLike(info, min, max);
        System.out.println(books);
        request.setAttribute("books",books);
        request.getRequestDispatcher("/view/find_show.jsp").forward(request,response);
    }

}
