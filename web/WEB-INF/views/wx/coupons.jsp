<%--
  Created by IntelliJ IDEA.
  User: hubo
  Date: 2015/9/29
  Time: 13:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String resourcePath = request.getServletContext().getInitParameter("resource_path") + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>我的优惠券</title>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/index.css">
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/coupons.css">
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
</head>
<body>
<div class="top">
    <font>我的优惠券</font>

    <div class="back" onclick="WeixinJSBridge.call('closeWindow');">
        <img src="<%=resourcePath%>static/wx/images/top_back.png"/>
    </div>
</div>
<div class="content">
    <div class="showpage">
        <ul class="you_tu">
        </ul>
    </div>
    <div class="list_load">
        <img src="<%=resourcePath%>static/wx/images/load.gif"/>
    </div>
</div>
<script src="<%=resourcePath%>static/wx/js/jquery-2.1.4.js" type="text/javascript"></script>
<script type="text/javascript">

    var openId = "${requestScope.openId}";

    var effectCouponUrl = "${requestScope.effectCouponUrl}";

    var overdueCouponUrl = "${requestScope.overdueCouponUrl}";

    var backUrl = "${requestScope.backUrl}";

    var pageNo = 1;

    var pageSize = 5;

    var total = 0;

    $(function () {
        if (openId != null && openId != "") {
            setTimeout("queryEffectCoupon()", 500);
        } else {
            window.location = backUrl;
        }
    });

    function queryOverDueCoupon() {
        $(".btn_more").remove();
        if (pageNo > 1) {
            $(".showpage").append("<div class='btn_more'><div class='gif'><img src='<%=resourcePath%>static/wx/images/load.gif' /></div></div>");
        }
        $.ajax({
            url: overdueCouponUrl + "?pageNo=" + pageNo + "&pageSize=" + pageSize,
            data: {},
            dataType: "json",
            async: true,
            type: "GET",
            success: function (data) {

                var datas = eval(data);

                if (datas.result == "success") {
                    var overDuehtml = "";
                    if (datas.overDueLst && datas.overDueLst.length > 0) {
                        if (pageNo == 1) {
                            overDuehtml += "<li class='bt'><h2>----------------　<span>失效优惠券</span>　----------------</h2></li>";
                        }
                        $.each(datas.overDueLst, function () {
                            overDuehtml += "<li>";
                            if (this.status == 0) {
                                overDuehtml += "<img  id='"+this.DT_RowId+"_img' src='<%=resourcePath%>static/wx/images/quan_pre2.png' onload=\"resetType('"+this.DT_RowId+"','"+this.flowTypeName+"')\"/>";
                            } else {
                                overDuehtml += "<img  id='"+this.DT_RowId+"_img' src='<%=resourcePath%>static/wx/images/quan_pre1.png' onload=\"resetType('"+this.DT_RowId+"','"+this.flowTypeName+"')\"/>";
                            }
                            overDuehtml += "<span class='jg you_zi'>满<span>" + this.minPayMny + "</span>元使用</span>";
                            overDuehtml += "<span class='pr you_zi'>" + this.mny + "</span>";
                            overDuehtml += "<span class='fw you_zi' id='"+this.DT_RowId+"_type'>" + this.flowTypeName + "</span>";
                            overDuehtml += "<span class='sj you_zi'>有效期：" + this.beginDate + " 至 " + this.endDate + "</span>";
                            overDuehtml += "</li>";
                        });

                        if (pageNo > 1) {
                            $(".btn_more").remove();
                        }

                        $(".showpage .you_tu").append(overDuehtml);

                        total = datas.total;

                        if (pageNo * pageSize < total) {
                            $(".showpage").append("<div class='btn_more'><a href='javascript:;' onclick='queryOverDueCoupon();'>点击查看往期优惠券</a> </div>");
                        }

                    }

                    if (pageNo == 1 && $(".you_tu").children().length == 0) {
                        $(".content").empty().html('<div class="list_load"><img src="<%=resourcePath%>static/wx/images/quan_no.png" /></div>');
                    }

                } else {
                    alert("还请您拨打:400-777-5669进行通知!");
                }

                pageNo = pageNo + 1;

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("还请您拨打:400-777-5669进行通知!");
            }
        });
    }

    function queryEffectCoupon() {
        $.ajax({
            url: effectCouponUrl + "?r=" + Math.random(),
            data: {},
            dataType: "json",
            async: true,
            type: "GET",
            success: function (data) {

                $(".list_load").remove();

                var datas = eval(data);

                if (datas.result == "success") {
                    var effecthtml = "";
                    if (datas.effectLst && datas.effectLst.length > 0) {
                        effecthtml += "<li class='bt'><h2>----------------　<span>当前可用优惠券</span>　----------------</h2></li>";
                        $.each(datas.effectLst, function () {
                            effecthtml += "<li>";
                            effecthtml += "<img id='"+this.DT_RowId+"_img' src='<%=resourcePath%>static/wx/images/quan.png' onload=\"resetType('"+this.DT_RowId+"','"+this.flowTypeName+"')\"/>";
                            effecthtml += "<span class='jg you_zi'>满<span>" + this.minPayMny + "</span>元使用</span>";
                            effecthtml += "<span class='pr you_zi'>" + this.mny + "</span>";
                            effecthtml += "<span class='fw you_zi' id='"+this.DT_RowId+"_type'>" + this.flowTypeName + "</span>";
                            effecthtml += "<span class='sj you_zi'>有效期：" + this.beginDate + " 至 " + this.endDate + "</span>";
                            effecthtml += "</li>";
                        });

                    }
                    if (datas.unEffectLst && datas.unEffectLst.length > 0) {
                        effecthtml += "<li class='bt ky'><h2>----------------　<span>即将生效优惠券</span>　----------------</h2></li>";
                        $.each(datas.unEffectLst, function () {
                            effecthtml += "<li>";
                            effecthtml += "<img id='"+this.DT_RowId+"_img' src='<%=resourcePath%>static/wx/images/quan.png' onload=\"resetType('"+this.DT_RowId+"','"+this.flowTypeName+"')\"/>";
                            effecthtml += "<span class='jg you_zi'>满<span>" + this.minPayMny + "</span>元使用</span>";
                            effecthtml += "<span class='pr you_zi'>" + this.mny + "</span>";
                            effecthtml += "<span class='fw you_zi' id='"+this.DT_RowId+"_type'>" + this.flowTypeName + "</span>";
                            effecthtml += "<span class='sj you_zi'>有效期：" + this.beginDate + " 至 " + this.endDate + "</span>";
                            effecthtml += "</li>";
                        });
                    }
                    var oldHtml = $(".showpage .you_tu").html();
                    $(".showpage .you_tu").empty().html(effecthtml).append(oldHtml);
                    queryOverDueCoupon();
                } else {
                    alert("还请您拨打:400-777-5669进行通知!");
                }

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $(".list_load").remove();
                alert("还请您拨打:400-777-5669进行通知!");
            }
        });
    }

    function resetType(DT_RowId,flowTypeName){
        var fontsize = parseFloat($(".fw").css("line-height"));
        var top = ($("#"+DT_RowId+"_img").outerHeight()-flowTypeName.length*fontsize)/2;
        $("#"+DT_RowId+"_type").css("top",top+"px");
    }
</script>
</body>
</html>
