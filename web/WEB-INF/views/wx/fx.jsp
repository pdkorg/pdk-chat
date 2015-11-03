<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String resourcePath = request.getServletContext().getInitParameter("resource_path") + "/";
%>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>跑的快-您的私人生活助理</title>
	<meta content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no" name="viewport" id="viewport" />   
    <link rel="stylesheet" type="text/css" href="<%=resourcePath%>static/wx/css/bootstrap.min.css">
    <script src="<%=resourcePath%>static/wx/js/jquery-2.1.4.js"></script>
	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<style type="text/css">
	* {margin: 0;padding: 0;}
	a {text-decoration: none;}
	a:hover {text-decoration: none;}
	img {border: none;}
	ol, ul, li {list-style: none;margin-bottom: 0em;}
	h1, h2, h3, h4, h5, h6 {font-weight: normal;font-size: 100%;margin: 0;}
	input, button {border: none;}
	body {margin: 0 auto;overflow: hidden;font-size:100%;font-size:12px;font-size:1em;	min-width:24rem;max-width: 62.4rem;overflow:auto; background-color: #fff; font-family:"黑体";background:url(<%=resourcePath%>static/wx/images/bd_bg.png) repeat #f0592f;}
	.banner img{width: 100%;}
</style>
	<script>
		var appId, timestamp, nonceStr, signature,code;
		var shareUrl = "http://mp.weixin.qq.com/s?__biz=MzA4NTc0Njc3Ng==&mid=211017284&idx=1&sn=48d32e00959e78451af2d96ee9863cf0#rd&ADUIN=1120199156&ADSESSION=1438270042&ADTAG=CLIENT.QQ.5359_.0&ADPUBNO=26396";
		var sharetitle = "跑的快—您的私人生活助理";
		var sharedesc = "现在关注跑的快微信公众号，马上就送125元现金礼包";
		var imgUrl = "<%=resourcePath%>static/wx/images/logo.jpg";
		var openId = "${requestScope.openId}";
		$(function() {
			$.ajax({
				type : "POST",
				url : "<%=basePath%>wxshare/init",
				data : {
					url : window.location.href
				},
				dataType : "json",
				success : function(data, textStatus, jqXHR) {
					wxinit(data);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert("网络故障！");
				},
				dataType : "json"
			});
		});
	
		function wxinit(data) {
			appId = data.appId;
			wx.config({
				debug : false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
				appId : data.appId, // 必填，公众号的唯一标识
				timestamp : data.timestamp, // 必填，生成签名的时间戳
				nonceStr : data.nonceStr, // 必填，生成签名的随机串
				signature : data.signature,// 必填，签名，见附录1
				jsApiList : [ 'onMenuShareTimeline', 'onMenuShareAppMessage' ]
			// 必填，需要使用的JS接口列表，所有JS接口列表见附录2
			});
			wx.ready(function() {
				wx.checkJsApi({
				    jsApiList: ['onMenuShareTimeline','onMenuShareAppMessage'], // 需要检测的JS接口列表，所有JS接口列表见附录2,
				    success: function(res) {
				    }
				});
				wx.error(function(res) {
				});
				wx
					.onMenuShareTimeline({
						title : sharetitle, // 分享标题
						link : shareUrl, // 分享链接
						imgUrl : imgUrl, // 分享图标
						success : function() { // 用户确认分享后执行的回调函数
							wxshare();
						},
						cancel : function() {
							
						}
					});
				wx
					.onMenuShareAppMessage({
						title : sharetitle, // 分享标题
						desc : sharedesc, // 分享描述
						link : shareUrl, // 分享链接
						imgUrl : imgUrl, // 分享图标
						type : 'link', // 分享类型,music、video或link，不填默认为link
						dataUrl : '', // 如果type是music或video，则要提供数据链接，默认为空
						success : function() {
							wxshare();
						},
						cancel : function() {
							
						}
					});
				
			});
	
		}
		
		function wxshare(){
			$.ajax({
				type : "POST",
				url : "<%=basePath%>wxshare/share",
				data : {
					openId : openId
				},
				dataType : "json",
				success : function(data, textStatus, jqXHR) {
					if(data.result == "success"){
						goToShowCoupon(data.coupon);
					}else{
						if(data.msg)
							alert(data.msg);
						WeixinJSBridge.call('closeWindow');
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert("网络故障！");
				}
						
			});
		}

		function goToShowCoupon(data) {
			var toUrl = "<%=basePath%>wxshare/showCoupon?openId={openId}&couponMny={couponMny}&couponMinPayMny={couponMinPayMny}&flowTypeName={flowTypeName}&endDate={endDate}"
			toUrl = toUrl.replace("{couponMny}",data.mny)
					.replace("{couponMinPayMny}",data.minPayMny)
					.replace("{flowTypeName}",data.flowTypeName)
					.replace("{endDate}",data.endDate)
					.replace("{openId}",openId);
			window.location = toUrl;
		}


	</script>
</head>
<body>
    <div class="banner">
		<img src="<%=resourcePath%>static/wx/images/fx.png"/>
	</div>
</body>
</html>