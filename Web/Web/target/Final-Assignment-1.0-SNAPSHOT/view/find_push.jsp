<%@ page language="java"  pageEncoding="utf-8"%>
<html>
<head>  <title>查询条件提交页面</title>
    <%
        String basePath = "";
        basePath = basePath +request.getScheme() + "://" + request.getServerName() +  ":"
                + request.getServerPort() + request.getContextPath() + "/";
        request.setAttribute("basePath",basePath);
    %>
</head>
<body>
<center>
    <b><font size="3" color="red">请选择图书查询条件：</font></b> <br> <br>
<form action= "${basePath}book?action=findBookByLike"  method="post" style="align-content: center">
    编号：<input type="text" name="bookid"><br><br>
    书名：<input type="text" name="bookname"><br><br>
    作者：<input type="text" name="author"><br><br>
    <b><font size="3" color="red">价格范围:</font></b><p>&nbsp;&nbsp;&nbsp;&nbsp;
    最小：<input type="text" name="p1"><br><br>
    &nbsp;&nbsp;&nbsp;&nbsp;
    最大：<input type="text" name="p2"> <p>&nbsp;&nbsp;
    <input type="submit" value="提  交" class="btn btn-primary" style="color: red; background-color: black;
width: 80px;height: 40px;">
    <br> <br>
    &nbsp;&nbsp;&nbsp;<input type="reset" value="取  消" class="btn btn-primary" style="color: red; background-color: black;
width: 80px;height: 40px;">
</form>
</center>
</body>
</html>

