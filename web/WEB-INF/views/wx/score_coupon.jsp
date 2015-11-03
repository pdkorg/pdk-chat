<%--
  Created by IntelliJ IDEA.
  User: hubo
  Date: 2015/10/12
  Time: 18:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String resourcePath = request.getServletContext().getInitParameter("resource_path") + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no" name="viewport" id="viewport" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/index.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/order_quan.css">
    <script src="<%=resourcePath%>static/wx/js/jquery-2.1.4.js"></script>
    <title>评价完成</title>
</head>
<body>
<div class="top">
    <font>评价成功</font>
    <div class="back" onclick="goToOrderList()">
        <img src="<%=resourcePath%>static/wx/images/top_back.png"/>
    </div>
</div>
<div class="content">
    <div class="con_top">
        <img src="<%=resourcePath%>static/wx/images/top.png"/>
        <p>￥<span>${requestScope.couponMny}</span></p>
    </div>
    <div class="nr">
        <p>总价值<span>${requestScope.couponMny}</span>元的优惠券已经放入您的账户~</p>
        <div class="quan">
            <img src="<%=resourcePath%>static/wx/images/quan.png" id="coupon_Img" onload="resetType('${requestScope.couponType}')"/>
            <font class="jg">满<font>${requestScope.couponMinPayMny}</font>元使用</font>
            <font class="pr">${requestScope.couponMny}</font>
            <font class="fw" id="coupon_type">${requestScope.couponType}</font>
            <font class="sj">截止日期:<font>${requestScope.endDate}</font></font>
        </div>
        <a href="javascript:void(0)" onclick="goToCoupon()">查看全部优惠券</a>
        <p>下次有任何需要记得呼唤小跑君哦~</p>
        <div class="hd">
            <h2>---------　<span>最新<b>优惠</b>活动</span>　---------</h2>
            <div class="banner">
                <a href="http://mp.weixin.qq.com/s?__biz=MzA4NTc0Njc3Ng==&mid=211361519&idx=1&sn=10fbede93a8e7d55f727be965b41f557&scene=0#rd">
                    <img src="<%=resourcePath%>static/wx/images/zx_1.png"/>
                </a>
                <a href="http://mp.weixin.qq.com/s?__biz=MzA4NTc0Njc3Ng==&mid=211361519&idx=2&sn=a4c84e67e39374bdda0734eda589e55e&scene=0#rd">
                    <img src="<%=resourcePath%>static/wx/images/zx_2.png"/>
                </a>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">

    var openId = "${requestScope.openId}";



    function goToCoupon() {
        window.location = "<%=basePath%>coupons/user/" + openId;
    }

    function goToOrderList() {
        window.location = "<%=basePath%>order/user/"+openId;
    }

    function resetType(flowTypeName){
        var fontsize = parseFloat($(".fw").css("line-height"));
        var top = ($("#coupon_Img").outerHeight()-flowTypeName.length*fontsize)/2;
        $("#coupon_type").css("top",top+"px");
    }


</script>
</body>
</html>