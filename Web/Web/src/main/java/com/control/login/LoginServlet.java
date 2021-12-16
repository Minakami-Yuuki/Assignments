package com.control.login;

import com.Info.User;
import com.control.BaseServlet;
import com.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends BaseServlet {

    UserDao userDao = new UserDao();

    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDao.login(username, password);
        if(user == null) {
            request.setAttribute("msg","用户或密码错误");
            request.getRequestDispatcher("/index.jsp").forward(request,response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("user",user);
            response.sendRedirect(request.getContextPath() + "/view/index.jsp");
        }
    }


}
