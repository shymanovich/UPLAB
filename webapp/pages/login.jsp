<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Chat</title>
    <style>
        <%@include file="../css/login.css"%>
    </style>
    <link rel="stylesheet" href="../css/login.css">
</head>
<body>
<section class="container">
    <div class="login">
        <h1>Join chat</h1>
        <form id="loginform" method="post" action="/login">
            <p><input type="text" name="username" value="" placeholder="Username"></p>
            <p><input type="password" name="password" value="" placeholder="Password"></p>
            <p class="submit"><input type="submit" name="commit" value="Log in"></p>
        </form>
    </div>
</section>
</body>
</html>