<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>登陆页面</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css" />
</head>
<body>
<div id="bigBox">
    <h1>图书管理系统</h1>
    <div class="inputBox">
        <form action="/view/register.jsp" method="post">
            <div class="inputText">
                <i class="fa fa-user-circle" style="color: whitesmoke;"></i>
                <input type="text" placeholder="学号" name="username"/>
            </div>
            <div class="inputText">
                <i class="fa fa-key" style="color: whitesmoke;"></i>
                <input type="password" placeholder="密码" name="password"/>
            </div>
            <input type="submit" class="inputButton" value="注册" />
        </form>
        <form action="index.jsp" method="post">
            <input type="submit" class="inputButton" value="登录" />
        </form>
    </div>
</div>
</body>
</html>
