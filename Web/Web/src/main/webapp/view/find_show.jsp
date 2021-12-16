<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import="com.Info.BookInfo" %>
<%@ page import="java.util.List" %>
<html>
<head> <title>图书查询结果</title>
    <%
        String basePath = "";
        basePath = basePath +request.getScheme() + "://" + request.getServerName() +  ":"
                + request.getServerPort() + request.getContextPath() + "/";
        request.setAttribute("basePath",basePath);
    %>
</head>
<body>
<center>

    <h2><font size="5" color="blue">图书目录</font></h2>
    <table border="2" bgcolor= "orange" width="650">
        <tr bgcolor="#fa8072" align="center">
            <td>图书编号</td> <td>书名</td><td>作者</td> <td>价格</td><td>备注</td>
        </tr>
        <c:forEach items="${requestScope.books}" var="book">
            <tr align="center">
                    <%--                <td>${book.bookid}</td>--%>
                <td>${book.bookid}</td>
                <td>${book.bookname}</td>
                <td>${book.author}</td>
                <td>${book.price}</td>
                <td>${book.instruction}</td>
            </tr>
        </c:forEach>

    </table>
</center>
</body>
</html>

