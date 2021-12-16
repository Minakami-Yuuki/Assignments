package com.filter;

import com.Info.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            chain.doFilter(req, resp);
        } else {
            request.getRequestDispatcher("/index.jsp").forward(req,response);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
