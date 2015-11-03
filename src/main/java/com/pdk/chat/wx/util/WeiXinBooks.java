package com.pdk.chat.wx.util;


import com.pdk.chat.util.AppConfig;

public class WeiXinBooks {
	private static WxConfigInfo wxConfigInfo = AppConfig.getBean(WxConfigInfo.class);
	public static String getFocus() {
		StringBuilder builder = new StringBuilder();
		builder.append("主人，【小跑君】候您多时了！").append("\n");
		builder.append("我的大名叫【跑的快】，是一直在您身边的私人生活助理。").append("\n");
		builder.append("有啥需要，说句话，【小跑君】都帮您搞定！").append("\n");
		builder.append("而且，还有一大堆优惠券哦~").append("\n");
		builder.append("服务时间:9:00-22:00").append("\n");
		builder.append("服务类型:").append("\n");
		builder.append("[商超代购] [药店代购]").append("\n");
		builder.append("[外卖订餐] [上门按摩]").append("\n");
		builder.append("[上门美容] [搬家搬运]").append("\n");
		builder.append("[家政保洁] [家电维修]").append("\n");
		builder.append("[五金租赁] [洗衣代送]").append("\n");
		builder.append("[代叫快递] [开锁换锁]").append("\n");
		builder.append("(更多服务持续更新中…)").append("\n");
		builder.append("有事儿您说话，懒得跑，就找【跑的快】！");
		return builder.toString();
	}
    
    public static String getTSJY() {
		return "您可以通过拨打客服热线400-777-5669进行投诉和建议，小跑君会尽快帮您处理哦~";
    }
    
    public static String getFWFW() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("主人~~~").append("\n\n");
        buffer.append("小跑君可以帮您——").append("\n");
        buffer.append("买吃的喝的用的玩儿的…").append("\n");
        buffer.append("还能上门给您——").append("\n");
        buffer.append("打扫卫生维修家电按摩美容…").append("\n");
        buffer.append("不光这些，我还能——").append("\n");
        buffer.append("洗衣服搬家开锁订票取东西…").append("\n");
        buffer.append("有了小跑君这么【万能】的私人生活助理，您有任何需要只要在微信里直接提就好啦！").append("\n\n");
        
        buffer.append("小跑君的工作时间是：").append("\n");
        buffer.append("【9:00—22:00】").append("\n\n");
        
        buffer.append("小跑君目前可以服务：").append("\n");
        buffer.append("北京朝阳区、回龙观和西三旗区域~").append("\n\n");
        
        buffer.append("有任何需要快快点击左下角的键盘按钮，直接文字或语音告诉我们就好啦~");
        return buffer.toString();
      }
    
    public static String getUOrder() {
    	StringBuffer buffer = new StringBuffer();
		buffer.append("主人，点击左下角的“键盘”按钮，然后直接打字或语音说出您的任意需求，小跑君就会直接在公众号内回复并帮助您下单啦~就像您跟朋友在微信上聊天一样哦~/:B-)").append("\n");
		buffer.append("下单完成后，您可以再点击左下角的菜单按钮，在“个人中心——订单中心”中查看您的订单哟~/:8-)").append("\n");
		buffer.append("对了，别忘记支付哦~/:,@-D");
		return buffer.toString();
    }
    
    public static String getRest() {
    	StringBuffer buffer = new StringBuffer();
		buffer.append("主人，让【小跑君】休息，休息一下~").append("\n");
		buffer.append("【小跑君】的服务时间是每日【早9:00—晚10:00】").append("\n");
		buffer.append("如果需要夜间提供服务，请在白天提前预约哦~").append("\n");
		buffer.append("给主人造成不便，请您谅解").append("\n");
		buffer.append("请持续关注我们，懒得跑，就找【跑的快】！").append("\n");
		return buffer.toString();
    }

    public static String getFXYL() {
    	StringBuffer buffer = new StringBuffer();
		buffer.append("每日首次将【跑的快】成功分享至微信朋友圈或微信好友，即可马上获得一张优惠券哦~").append("\n");
		buffer.append("快戳下面的链接分享吧~").append("\n");
		buffer.append("<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=");
		buffer.append(wxConfigInfo.getWxappid());
		buffer.append("&redirect_uri=http://");
		buffer.append(wxConfigInfo.getPdkchaturl());
		buffer.append("/wxshare/fx?tag=1&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\">【参与活动】</a>");
		return buffer.toString();
    }
}
