<%--
  Created by IntelliJ IDEA.
  User: duduchao
  Date: 15/10/5
  Time: 22:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String resourcePath = request.getServletContext().getInitParameter("resource_path") + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no"
          name="viewport" id="viewport"/>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>订单中心列表</title>
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/index.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/order_list.css">
    <script src="<%=resourcePath%>static/wx/js/jquery-2.1.4.js"></script>
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
</head>
<body>
<div class="top">
    <font>订单中心</font>

    <div class="back" onclick="WeixinJSBridge.call('closeWindow')">
        <img src="<%=resourcePath%>static/wx/images/top_back.png" />
    </div>
</div>
<div class="content">
    <div class="list_load">
        <img src="<%=resourcePath%>static/wx/images/load.gif"/>
    </div>
</div>
<script type="text/javascript">

    var openId = "${requestScope.openId}";

    var queryUserOrderUrl = "${requestScope.queryUserOrderUrl}";

    var backUrl = "${requestScope.backUrl}";

    var pageNum = 1;

    var pageSize = 10;

    var total = 0;

    if (openId != null && openId != "") {
        setTimeout("queryUserOrder()", 500);
    } else {
        window.location = backUrl;
    }

    function queryUserOrder() {
        $(".btn_more").remove();
        if (pageNum > 1) {
            $(".content").append("<div class='btn_more'><div class='gif'><img src='<%=resourcePath%>static/wx/images/load.gif' /></div></div>");
        }
        $.ajax({
            url: queryUserOrderUrl + "?pageNum=" + pageNum + "&pageSize=" + pageSize + "&r=" + Math.random(),
            data: {},
            dataType: "json",
            async: true,
            type: "GET",
            success: function (data) {
                var datas = eval(data);
                $(".list_load").remove();
                if (datas.result == "success") {
                    var orderList = datas.data;
                    var total = datas.total;
                    var contenthtml = "";
                    if (orderList && orderList.length > 0) {
                        $.each(orderList, function () {
                            var flowTypeName = this.head.flowTypeName;
                            var time = this.head.startTime;
                            var mny = this.head.mny;
                            var payStatus = this.head.payStatus;

                            if(payStatus == 1 && this.head.reviewId) {
                                contenthtml += '<div class="list list_finish"><img src="<%=resourcePath%>static/wx/images/icon_order.png" class="icon_finish"/>';
                            }else {
                                contenthtml += '<div class="list">';
                            }
                            contenthtml += '<div class="list_top"><div class="list_img"><img src="<%=resourcePath%>static/wx/images/'+flowTypeName+'.png"/></div>';
                            contenthtml += '<div class="left"><div class="list_bt"><span class="list_name">' + flowTypeName + '</span>';
                            contenthtml += '</div>';
                            contenthtml += '<span class="list_time">' + time + '</span>';
                            contenthtml += '</div>';
                            if (!this.head.priced) {
                                contenthtml += '<div class="right"><img src="<%=resourcePath%>static/wx/images/time.png"/><span  class="bj">等待报价</span></div>';
                            } else {
                                contenthtml += '<div class="right"><span>￥' + mny + '</span></div>';
                            }
                            contenthtml += '</div>';
                            contenthtml += '<a href="javascript:void(0)" onclick="queryOrderDetail(\''+this.head.id+'\')" class="list_nr">';
                            contenthtml += '<ul class="list_listing">';

                            if (this.body && this.body.length > 0) {
                                $.each(this.body, function () {
                                    var orderDetailName = this.name;
                                    var num = this.num;
                                    var unitName = this.unitId;
                                    contenthtml += '<li><font class="name">' + orderDetailName + '</font> <font class="number">' + num + unitName + '</font><font class="price">￥'+this.goodsMny+'</font></li>';
                                });
                            }

                            contenthtml += '</ul>';
                            if (!this.head.priced) {
                                contenthtml += '<div class="list_btn btn_see"><span>查看详情</span></div>';
                                contenthtml += '</a></div>';
                            } else {
                                if (payStatus == 0) {
                                    contenthtml += '<div class="list_btn btn_pay"><span>支付</span></div>';
                                    contenthtml += '</a></div>';
                                } else {
                                    if(this.head.reviewId == null || this.head.reviewId.length == 0) {
                                        contenthtml += '</a>';
                                        contenthtml += '<a onclick="goToEmployeeScore(\''+this.head.id+'\')" class="list_nr"><div class="list_btn btn_pay"><span>评价</span></div></a>';
                                        contenthtml += '</div>';
                                    } else {
                                        contenthtml += '<div class="list_btn btn_see"><span>查看详情</span></div>';
                                        contenthtml += '</a></div>';
                                    }
                                }
                            }
                        });

                        if (pageNum > 1) {
                            $(".btn_more").remove();
                        }

                        $(".content").append(contenthtml);

                        total = datas.total;

                        if (pageNum * pageSize < total) {
                            $(".content").append("<div class='btn_more'><a href='javascript:;' onclick='queryUserOrder();'>点击加载更多订单</a> </div>");
                        } else {
                            $(".content").append("<div class='btn_more'><a>没有更多订单了</a></div>");
                        }

                    } else {
                        contenthtml += '<div class="nolist_img"><img src="<%=resourcePath%>static/wx/images/list_no.png"/></div>';
                        $(".content").empty().html(contenthtml);
                    }
                } else {
                    alert("还请您拨打:400-777-5669进行通知!");
                }

                pageNum = pageNum + 1;
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("还请您拨打:400-777-5669进行通知!");
                $(".list_load").remove();
            }

        });
    }

    function queryOrderDetail(orderId) {
        window.location = "<%=basePath%>order/"+ orderId  + "?openId="+ openId;
    }

    function goToEmployeeScore(orderId) {
        window.location = "<%=basePath%>order/score/employee/"+orderId +"?openId="+openId;
    }

</script>
</body>
</html>
