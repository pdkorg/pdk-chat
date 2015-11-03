<%--
  Created by IntelliJ IDEA.
  User: hubo
  Date: 2015/10/9
  Time: 18:12
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
    <title>订单详情</title>
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/index.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/order_detail.css">
    <link href="<%=resourcePath%>static/wx/css/ion.rangeSlider.css" rel="stylesheet" type="text/css"/>
    <link href="<%=resourcePath%>static/wx/css/ion.rangeSlider.Metronic.css" rel="stylesheet" type="text/css"/>
    <link href="<%=resourcePath%>static/wx/css/ion.rangeSlider.skinFlat.css" rel="stylesheet" type="text/css"/>
    <script src="<%=resourcePath%>static/wx/js/jquery-2.1.4.js"></script>
    <script src="<%=resourcePath%>static/wx/js/ion.rangeSlider.js"></script>
    <script src="<%=resourcePath%>static/wx/js/ion.rangeSlider.min.js"></script>
</head>
<body>
<div class="top">
    <font>订单详情</font>

    <div class="back" onclick="goToOrderList()">
        <img src="<%=resourcePath%>static/wx/images/top_back.png"/>
    </div>
</div>
<div class="content">
    <div class="list_load">
        <img src="<%=resourcePath%>static/wx/images/load.gif"/>
    </div>
</div>

<script type="text/javascript">

    var queryOrderDetailUrl = "${requestScope.queryOrderDetailUrl}";

    var openId = "${requestScope.openId}";

    var tipMny = 0;

    var userId = "";

    $(function() {
        loadOrderDetail(0);
    });

    function loadOrderDetail(startTipMny) {
        $.ajax({
            url: queryOrderDetailUrl + "?r=" + Math.random(),
            data: {},
            dataType: "json",
            async: true,
            type: "GET",
            success: function (datas) {
                $(".list_load").remove();

                var result = eval(datas);

                if (result.order) {

                    var order = result.order;


                    var contentHtml = '<div class="detail_list serve"><div class="detail_top">';
                    contentHtml += '<font>小跑君信息</font></div>';

                    if (result.employee) {
                        var headImage;

                        if (result.employee.headerImg == null || result.employee.headerImg == "") {
                            headImage = "<%=resourcePath%>static/wx/images/fw.png";
                        } else {
                            headImage = "<%=resourcePath%>" + result.employee.headerImg;
                        }

                        contentHtml += '<div class="detail_nr"><div class="tx_img"><div class="tx"><img src="' + headImage + '"/>';
                        contentHtml += '<font>' + result.score + '</font></div></div>';

                        contentHtml += '<div class="xq"><div class="xq_name">';
                        contentHtml += '<font>' + result.employee.name + '</font>';

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

                        contentHtml += '</ul></div>';

                        contentHtml += '<div class="xq_pj"><font>已完成<span>' + (result.serviceCount + 512) + '</span>次服务</font><font>已收到<span>' + (result.tipCount + 372) + '</span>次小费</font></div></div></div></div>'
                    } else {
                        contentHtml += '<div class="detail_nr"><p class="detail_p">未分配小跑君</p> </div>'
                    }

                    $(".content").empty().append(contentHtml);

                    if (result.user) {
                        userId = result.user.id;
                        var contentHtml = '<div class="detail_list user">';
                        contentHtml += '<div class="detail_top"><font>用户信息</font><div class="user_right">';
                        contentHtml += '<b class="name">' + result.user.realName + '</b><b class="open"></b></div></div>';
                        contentHtml += '<div class="user_nr detail_nr">';
                        contentHtml += '<p class="line1"><font class="name">姓名：<b>' + result.user.realName + '</b></font>';
                        contentHtml += '<font class="tel">' + result.user.phone + '</font>';
                        contentHtml += '</p><p class="line2"><font>地址：<font>' + order.head.adress + '</font></font></p></div></div>';
                        $(".content").append(contentHtml);
                        setUserSlide();
                    }

                    var orderHtml = '<div class="detail_list note">';

                    var orderDetailByAddress = {};

                    var addressSet = [];

                    orderHtml += '<div class="detail_top"><font>订单详情</font><b>编号：' + order.head.code + '</b></div>';


                    orderHtml += '<div class="note_nr detail_nr"><ul>';

                    if (order.body && order.body.length > 0) {
                        $.each(order.body, function () {
                            if (orderDetailByAddress[this.buyAdress] == null) {
                                orderDetailByAddress[this.buyAdress] = [];
                                addressSet.push(this.buyAdress);
                            }
                            orderDetailByAddress[this.buyAdress].push(this);
                        });

                        $.each(addressSet, function () {
                            orderHtml += '<li class="gm">购买地点：' + this + '</li>';

                            $.each(orderDetailByAddress[this], function () {

                                orderHtml += '<li class="note_xq clearfix">';
                                orderHtml += '<p>' + this.name + '</p>';

                                orderHtml += '<font class="number">×' + this.num + this.unitId + '</font>';

                                orderHtml += '<font class="price">￥' + this.goodsMny + '</font>';
                                orderHtml += '</li>';
                            });

                        });
                    }

                    if (order.head.payStatus == 0) {
                        if (!order.head.priced) {
                            orderHtml += '<li class="hj"><font>等待报价</font></li>';
                        } else {
                            orderHtml += '<li class="hj"><font>合计:<b>￥' + order.head.mny + '</b></font></li>';
                        }
                    } else {
                        orderHtml += '<li class="list_yh note_xq clearfix"><p>优惠券</p><font class="pri">-￥' + order.head.couponMny + '</font></li>';
                        orderHtml += '<li class="list_yh note_xq clearfix"><p>小费</p><font class="pri">￥' + order.head.feeMny + '</font></li>';
                        orderHtml += '<li class="hj"><font>实付款:<b>￥' + order.head.actualMny + '</b></font>	</li>';
                    }

                    orderHtml += '</ul></div>';

                    orderHtml += '</div>';

                    $(".content").append(orderHtml);

                    if (order.head.payStatus == 0 && order.head.priced) {

                        var couponAndTipHtml = '<div class="quan detail_list"><div class="detail_top"><font>优惠券</font></div>';

                        if (result.coupon) {
                            couponAndTipHtml += '<div class="detail_nr quan_nr clearfix"><p>' + result.coupon.flowTypeName + '</p>';
                            couponAndTipHtml += '<font class="number">满' + result.coupon.minPayMny + '<b>-￥' + result.coupon.mny + '</b></font></div>';
                        } else {
                            couponAndTipHtml += '<div class="detail_nr quan_nr clearfix quan_no"><p>暂时没有可用优惠券哦</p></div>';

                        }
                        couponAndTipHtml += '</div>';
                        couponAndTipHtml += '<div class="fei detail_list">';
                        couponAndTipHtml += '<div class="detail_top">';
                        couponAndTipHtml += '<font>小费</font> <b>小费越多返券金额越多哦~</b></div>';
                        couponAndTipHtml += '<div class="fei_nr detail_nr"><font>如果您满意小跑君的服务，请尽情打赏小跑君吧~</font>';
                        couponAndTipHtml += '<font>小费将归小跑君所有哦~</font>';
                        couponAndTipHtml += '<p class=""></p>';
                        couponAndTipHtml += '<ul><li class=""></li><li class=""></li><li class=""></li><li class=""></li></ul>';
                        couponAndTipHtml += '<input id="range_09"/></div></div>';

                        var couponMny = 0;
                        if (result.coupon) {
                            couponMny = result.coupon.mny;
                        }

                        var actMny = order.head.mny - couponMny;
                        if (actMny < 0) {
                            actMny = 0;
                        }

                        actMny = Number(actMny.toFixed(2));

                        couponAndTipHtml += '<div class="accounts"><div class="jin"><b>实付款:</b><font>￥' + actMny + '</font></div>';
                        couponAndTipHtml += '<a class="ac_btn" id="pay_btn"onclick="javascript:payOrder(\'' + order.head.id + '\', \'' + result.user.id + '\')"><font>立即支付</font></a></div>';

                        $(".content").append(couponAndTipHtml);

                        var from = 0;

                        if(startTipMny == 3) {
                            from = 1;
                        } else if(startTipMny == 5) {
                            from = 2;
                        } else if(startTipMny == 10) {
                            from = 3;
                        }

                        var slider = $("#range_09").ionRangeSlider({
                            grid: true,
                            from: from,
                            values: [
                                "0元", "3元", "5元", "10元"
                            ],
                            onChange: function (result) {
                                $(".fei_nr p").removeClass().addClass('lei' + result.from);
                                var num = result.from;
                                tipMny = Number(result.from_value.substring(0, result.from_value.length - 1));
                                $(".fei_nr li").slice(0, num).addClass('li_active');
                                $(".fei_nr li").slice(num, 3).removeClass('li_active');
                                actMny = Number(((order.head.mny - couponMny) < 0 ? 0 : (order.head.mny - couponMny)).toFixed(2)) + tipMny;
                                $(".accounts .jin font").empty().html('￥' + actMny);
                            }
                        });

                        if(from > 0) {
                            $(".fei_nr p").removeClass().addClass('lei' + from);
                            tipMny = startTipMny;
                            $(".fei_nr li").slice(0, from).addClass('li_active');
                            $(".fei_nr li").slice(from, 3).removeClass('li_active');
                            actMny = Number(((order.head.mny - couponMny) < 0 ? 0 : (order.head.mny - couponMny)).toFixed(2)) + tipMny;
                            $(".accounts .jin font").empty().html('￥' + actMny);
                        }

                    } else if (order.head.payStatus == 1 && order.head.priced && order.head.reviewId == null) {
                        $(".content").append('<div class="accounts"><div class="jin"><font>支付完成</font></div><a onclick="goToEmployeeScore(\'' + order.head.id + '\')" class="ac_btn">立即评价</a></div>');
                    }
                }
            },
            error: function () {
                $(".list_load").remove();
                alert("还请您拨打:400-777-5669进行通知!");
            }
        });
    }

    function setUserSlide() {
        var i = 0;
        $(".user_right").click(function (event) {
            if (i == 0) {
                $(this).parents(".detail_top").addClass('line');
                $(this).parents(".detail_top").siblings(".user_nr").slideDown(200);
                $(this).children('.name').hide();
                $(this).children('.open').addClass('user_icon');
                i = 1;
            }
            else {
                $(this).parents(".detail_top").removeClass('line');
                /*$(this).next(".user_nr").slideUp(200);*/
                $(this).parents(".detail_top").siblings(".user_nr").slideUp(200);
                $(this).children('.name').show();
                $(this).children('.open').removeClass('user_icon');
                i = 0;
            }
        });
    }

    function goToOrderList() {
        window.location = "<%=basePath%>order/user/" + openId;
    }

    function goToEmployeeScore(orderId) {
        window.location = "<%=basePath%>order/score/employee/" + orderId + "?openId=" + openId;
    }


    function payOrder(orderId, userId) {
        $("#pay_btn").attr("onclick", "");    // 1、防止多次点击支付  2、防止回退点击支付
        var payUrl = "<%=basePath%>order/pay/"+orderId+"?openId=" + openId + "&userId=" + userId + "&tip=" + tipMny;

        $.ajax({
            url: payUrl,
            type: 'get',
            dataType: 'json',
            success: function (data) {
                if (!data.success) {
                    alert(data.msg);
                    goToOrderList();
                } else {
                    var payInfo = data.data;
                    WeixinJSBridge.invoke('getBrandWCPayRequest', {
                        "appId": payInfo.appid,
                        "timeStamp": payInfo.timeStamp,
                        "nonceStr": payInfo.nonceStr,
                        "package": payInfo.packages,
                        "signType": "MD5",
                        "paySign": payInfo.finalsign
                    }, function (res) {
                        WeixinJSBridge.log(res.err_msg);
                        if (res.err_msg == "get_brand_wcpay_request:ok") {
                            goToEmployeeScore(orderId)
                        } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
//                            WeixinJSBridge.call('closeWindow');
//                            goToOrderList();
                            loadOrderDetail(tipMny);
                        } else {
                            alert("支付失败!!!!");
                            goToOrderList();
                        }
                    });
                }
            },
            error: function () {
                alert("还请您拨打:4007775669进行通知!");
            }
        });
    }
</script>
</body>
</html>
