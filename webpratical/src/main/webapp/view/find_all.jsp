<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" import="java.sql.*"%>
<html>
<head> <title>显示所有图书的页面</title>
    <%
        String basePath = "";
        basePath = basePath +request.getScheme() + "://" + request.getServerName() +  ":"
                + request.getServerPort() + request.getContextPath() + "/";
        request.setAttribute("basePath",basePath);
    %>
    <script type="text/javascript" src="script/jquery-1.7.2.js"></script>

    <script type="text/javascript">
        $(function (){

            $(".delete").click(function (){
                let val = $(this).val();
                alert("图书编号为：" + val);
                let flag = confirm("确定要删除该书吗？");
                if (flag) {
                    location.href = "${basePath}book?action=delete&bookid=" + val;
                } else {
                    return false;
                }
            })
        })
    </script>

</head>
<body>
<center>

    <h2><font size="5" color="blue">图书目录</font></h2>
    <table border="2" bgcolor= "orange" width="650">
        <tr bgcolor="#fa8072" align="center">
             <td>图书编号</td> <td>书名</td><td>作者</td> <td>价格</td><td>备注</td><td>操作</td>
        </tr>
        <c:forEach items="${requestScope.books}" var="book">
            <tr align="center">
<%--                <td>${book.bookid}</td>--%>
            <td>${book.bookid}</td>
            <td>${book.bookname}</td>
            <td>${book.author}</td>
            <td>${book.price}</td>
            <td>${book.instruction}</td>
            <td><button class="delete" style="color: red" value="${book.bookid}">删除</button></td>
            </tr>
        </c:forEach>

    </table>
</center>
</body>
</html>

