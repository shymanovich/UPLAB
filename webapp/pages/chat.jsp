<html>
<head>
    <meta charset="utf-8">
    <title>Chat by Shymanovich Vlad</title>
    <style>
        <%@include file="../css/style.css"%>
    </style>
    <script>
        <%@include file="../scripts/chat.js"%>
        <%@include file="../scripts/ajax.js"%>
    </script>
</head>
<body>
<h1 class="header">
    by Shymanovich Vlad
</h1>

<div class="container">
    <div class="menu">
        <img src="../images/menu.png" class="menu-icon">
        <div class="sub-menu">
            <form id="user-name" method="get" action="/login">
                <b>Your nickname: </b>
                <input type="text" placeholder="Nickname" id="nickname" name="nickname" value="<%= request.getSession().getAttribute("nickname") %>" readonly>
                <input type="submit" name="leave" value="Leave">
            </form>
            <form enctype="multipart/form-data" id="change-avatar" method="post" action="/user">
                <p><input type="file" name="avatar" accept="image/*">
                    <input type="submit" value="Select avatar"></p>
            </form>
        </div>
    </div>

    <div class="server">
        <img src="../images/online.png" class="condition">
    </div>

    <ul id="msg_cnt">
    </ul>
</div>

<div id="user-avatar" class="user-avatar"/>
<div>
    <textarea placeholder="Enter+ctrl to send" onkeypress="handleKeyPressMsgArea(event.keyCode)" id="msg_area"></textarea>
    <input id="send-btn" type="button" onclick="send_msg()" value="Send" class="send-btn">
</div>
</body>
</html>