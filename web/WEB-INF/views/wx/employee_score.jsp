<%--
  Created by IntelliJ IDEA.
  User: hubo
  Date: 2015/10/10
  Time: 17:38
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
    <meta content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no"
          name="viewport" id="viewport"/>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>订单评价</title>
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/index.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/order_review.css">
    <script src="<%=resourcePath%>static/wx/js/jquery-2.1.4.js"></script>
</head>
<body>
<div class="top">
    <font>订单评价</font>

    <div class="back" onclick="back()">
        <img src="<%=resourcePath%>static/wx/images/top_back.png"/>
    </div>
</div>
<div class="content">
    <div class="list_load">
        <img src="<%=resourcePath%>static/wx/images/load.gif"/>
    </div>
</div>

<script type="text/javascript">

    var openId = "${requestScope.openId}";

    var orderId = "${requestScope.orderId}";

    var employeeScoreUrl = "${requestScope.employeeScoreUrl}";

    var checkEmployeeScoreUrl = "${requestScope.checkEmployeeScoreUrl}";

    $(function() {
        $.ajax({
            url: checkEmployeeScoreUrl + "?orderId=" + orderId + "&sourceId=" + openId + "&r=" + Math.random(),
            dataType: "json",
            type: "GET",
            success: function (datas) {
                var response = eval(datas);
                if(!response.result) {
                    loadEmployeeReview();
                } else {
                    goToOrderList();
                }
            },
            error: function() {
                goToOrderList();
            }
        });
    });

    function loadEmployeeReview() {
        $.ajax({
            url: employeeScoreUrl + "?orderId="+orderId+"&r=" + Math.random(),
            dataType: "json",
            type: "GET",
            success: function (datas) {
                $(".list_load").remove();
                var result = eval(datas);

                var contentHtml = '<div class="message">';

                var headImage;

                if (result.employee.headerImg == null || result.employee.headerImg == "") {
                    headImage = "<%=resourcePath%>static/wx/images/fw.png";
                } else {
                    headImage = "<%=resourcePath%>" + result.employee.headerImg;
                }

                contentHtml += '<div class="tx"><img src="'+headImage+'"/><font>'+result.score+'</font></div>';

                contentHtml += '<ul class="star">';

                var scroeStr = Number(result.score).toString();

                var baseScroe = 0;

                var half = false;

                if (scroeStr.indexOf(".") > 0) {
                    baseScroe = Number(scroeStr.substring(0, scroeStr.indexOf(".")));
                    half = true;
                } else {
                    baseScroe = result.score;
                }

                for (var i = 0; i < baseScroe; i++) {
                    contentHtml += '<li class="star_r"></li>';
                }

                if (half) {
                    contentHtml += '<li class=""><i class="star_red"></i></li>';
                }

                contentHtml += '</ul>';

                contentHtml += '<p class="name">'+result.employee.name+'</p>';

                contentHtml += '<ul class="service"><li>已完成<font>'+(result.serviceCount + 1000)+'</font>次服务</li><li>已收到<font>'+(result.tipCount + 1000)+'</font>次小费</li></ul>';

                contentHtml += '<div class="review"><p>感谢您的小费，非常荣幸为您服务！</p><p>请您给我一个评价吧！</p>';
                contentHtml += '<ul class="r_star"><li class=""></li><li class=""></li><li class=""></li><li class=""></li><li class=""></li></ul></div>';

                contentHtml += '<div class="word"><textarea maxlength="50" id="description" type="text" class="word_nr" placeholder="如果您对我们有任何意见和建议，欢迎在这里写下来"></textarea><a id="submitBtn" href="javascript:void(0);" onclick="score(\''+result.employee.id+'\')">评价订单</a></div>';

                contentHtml += '</div>';

                $(".content").empty().append(contentHtml);

                $(".r_star li").click(function (event) {
                    $(this).addClass('star_r');
                    $(this).prevAll().addClass('star_r');
                    $(this).nextAll('li').removeClass('star_r');
                    /*var index = $(".r_star li").index(this);
                     alert(index+1);
                     获得点击星星的索引值即为评分*/
                });
            },
            error:function() {
                $(".list_load").remove();
                alert("还请您拨打:400-777-5669进行通知!");
            }
        });
    }

    function back() {
        window.history.back();
    }

    function goToOrderList() {
        window.location = "<%=basePath%>order/user/"+openId;
    }

    function score(employeeId) {

        var scoreObj = {};
        scoreObj.openId = openId;
        scoreObj.employeeId = employeeId;
        scoreObj.orderId = orderId;
        scoreObj.score = $(".r_star .star_r").length;
        scoreObj.description = $("#description").val();

        if(scoreObj.score == 0) {
            alert("请给小跑君打个分吧~");
            return;
        }

        $("#submitBtn").attr("onclick", "");

        $.ajax({
            url: employeeScoreUrl,
            data: {jsonStr:JSON.stringify(scoreObj)},
            dataType: "json",
            type: "POST",

            success: function (datas) {
                var result = eval(datas);

                if(result.result = "success") {
                    var coupon = result.coupon;

                    if(coupon) {
                        window.location = "<%=basePath%>coupons/score/" + openId + "?couponMny=" + coupon.mny
                                + "&couponMinPayMny=" + coupon.minPayMny + "&couponType=" + coupon.flowTypeName
                                + "&endDate=" + coupon.endDate;
                    } else {
                        goToOrderList();
                    }

                } else {
                    alert(result.errMessage);
                    goToOrderList();
                }

            },
            error:function() {
                alert("还请您拨打:400-777-5669进行通知!");
            }
        });
    }

</script>
</body>
</html>
