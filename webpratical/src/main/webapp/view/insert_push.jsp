<%@page contentType="text/html" pageEncoding="utf-8"%>
<html>
<head>  <title>添加图书信息提交页面</title>
    <%
        String basePath = "";
        basePath = basePath +request.getScheme() + "://" + request.getServerName() +  ":"
                + request.getServerPort() + request.getContextPath() + "/";
        request.setAttribute("basePath",basePath);
    %>
</head>
<body>
<center>
    <b><font size="3" color="red">请选择图书添加条件：</font></b><br> <br>
<form action= "${basePath}book?action=insert"  method="post" >
    <table border="0" width="238" height="252">
        编号：<input type="text" name="bookid"><br><br>
        书名：<input type="text" name="bookname"><br><br>
        作者：<input type="text" name="author"><br><br>
        价格：<input type="text" name="price"><br><br>
        备注：<input type="text" name="instruction">
        <br> <br>
        <input type="submit" value="提  交" class="btn btn-primary" style="color: red; background-color: black;
width: 80px;height: 40px;">
                <br> <br>
        <input type="reset" value="取  消" class="btn btn-primary" style="color: red; background-color: black;
width: 80px;height: 40px;">
    </table>
</form>
</center>
</body>
</html>

