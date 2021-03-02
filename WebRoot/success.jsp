<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%
        String msg = String.valueOf(request.getAttribute("msg"));
        %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title><%=msg %></title>

    <style type="text/css">
        * {
            margin: 0;
            padding: 0;
            list-style-type: none;
        }

        a, img {
            border: 0;
        }

        body {
            font: 12px/180% Arial, Helvetica, sans-serif, "新宋体";
        }
    </style>


    <link rel="stylesheet" type="text/css" href="/css/style.css"/>

    <script type="text/javascript" src="/js/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="/js/main.js"></script>
</head>
<body>
<div class="demo">
    <nav class="main_nav">
        <ul>


        </ul>
    </nav>
</div>

<div class="cd-user-modal">
    <div class="cd-user-modal-container">
        <ul class="cd-switcher">
            <li><a href="#0"></a></li>
            <li><a href="#0"></a></li>
        </ul>

        <div id="cd-login"> <!-- 表单 -->
            <form class="cd-form" onsubmit="return tj();">


                <!--<p class="fieldset">
                    <label class="image-replace cd-password" for="signin-password">密码</label>
                    <input class="full-width has-padding has-border" id="signin-password" type="text"  placeholder="输入密码">
                </p> -->


                <p class="fieldset">
                    <input class="full-width2" type="submit" value="<%=msg %>">
                </p>
            </form>
        </div>


    </div>
</div>
<div style="text-align:center;">

</div>
</body>
<script type="text/javascript">
    function tj() {
        return false;

    }
</script>
</html> 