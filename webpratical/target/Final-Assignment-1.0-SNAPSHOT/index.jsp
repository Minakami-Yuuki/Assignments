<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        String basePath = "";
        basePath = basePath +request.getScheme() + "://" + request.getServerName() +  ":"
                + request.getServerPort() + request.getContextPath() + "/";
        request.setAttribute("basePath",basePath);
    %>

    <link rel="stylesheet" type="text/css" href="/css/style.css" />
</head>
<body>
<div id="bigBox">
    <div class="inputBox">
    <form class="form-signin" action="${basePath}login">
        <input type="hidden" name="action" value="login"/>
        <table align="center">
             <h1 >Login</h1>
            <tr><td><p style="color: red">${requestScope.msg==null?"":requestScope.msg}</p></td></tr>

            <tr><td>
                <div class="inputText">
                    <i class="fa fa-user-circle" style="color: whitesmoke;"></i>
                    <label class="sr-only"></label>
                <input type="text" class="form-control" name="username" placeholder="Username" >
                </div>
            </td></tr>

            <tr><td>
                <div class="inputText">
                    <i class="fa fa-user-circle" style="color: whitesmoke;"></i>
                    <label class="sr-only"></label>
                    <input type="password" class="form-control" name="password" placeholder="Password">
                </div>
            </td></tr>

            <tr><td><button class="inputButton" type="submit">登录</button>

            </td></tr>

        </table>

    </form>

    </div>
</div>
</body>
</html>
