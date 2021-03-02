<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ page language="java" import="java.io.InputStream" %>
<%@ page language="java" import="org.dom4j.Document" %>
<%@ page language="java" import="org.dom4j.io.SAXReader" %>
<%@ page language="java" import="com.weking.wxutils.HttpXmlUtils" %>
<%@ page language="java" import="net.sf.json.JSONObject" %>
<%
    String path = request.getContextPath();
    String id = "wx7e2a999d482c5c75";
    String pd = "fadb312b078757eaab184df12f181232";
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    System.out.println(path);
    String code = request.getParameter("code");
    System.out.println("codessss" + code);

    String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + id + "&secret=" + pd + "&code=" + code + "&grant_type=authorization_code";

    String method = "POST";

    String Post = HttpXmlUtils.httpsRequest(url, method, "").toString();
    System.out.println(Post);
    JSONObject jsonData = JSONObject.fromObject(Post);
    String openid = jsonData.getString("openid");
    String access_token = jsonData.getString("access_token");
    String refresh_token = jsonData.getString("refresh_token");

    String rt = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + id + "&grant_type=refresh_token&refresh_token=" + refresh_token + "";
    String tworesult = HttpXmlUtils.httpsRequest(rt, method, "").toString();
    System.out.println(tworesult);
    JSONObject twojsonData = JSONObject.fromObject(tworesult);
    access_token = jsonData.getString("access_token");

    String geturl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";

    String userinfo = HttpXmlUtils.httpsRequest(geturl, "GET", "").toString();
    System.out.println(userinfo);
    JSONObject userinfodata = JSONObject.fromObject(userinfo);
    String nickname = userinfodata.getString("nickname");
    String unionid = userinfodata.getString("unionid");

    request.getRequestDispatcher("/PutCash/setOpenid?openid=" + openid + "&nickname=" + nickname + "&unionid=" + unionid).forward(request, response);

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<head>

    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>绑定咔嚓号</title>

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


    <link rel="stylesheet" type="text/css" href="css/style.css"/>

    <script type="text/javascript" src="js/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
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
            <form class="cd-form" onsubmit="tj();" action="/wkServer/PutCash/setOpenid">


                <!--<p class="fieldset">
                    <label class="image-replace cd-password" for="signin-password">密码</label>
                    <input class="full-width has-padding has-border" id="signin-password" type="text"  placeholder="输入密码">
                </p> -->


                <p class="fieldset">
                    <input class="full-width2" type="submit" value="提 交">
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
        var account = document.getElementById("account").value;
        if (account == '') {
            alert('内容不得为空');
            return false;
        }
        var openids = document.getElementById("openids").value;
        if (openids == '')
            return false;
    }
</script>
</html> 