package com.pdk.chat.wx.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class WeixinUrlInfo {
	@Autowired
	private WxConfigInfo wxConfigInfo;
    private  String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";  // 发送信息URL
    private  String sendTempletUrl = "https://api.weixin.qq.com/cgi-bin/mesage/templatea/send?access_token=";  // 发送模板信息URL

	public String getSendUrl() {
		return sendUrl;
	}

	public String getSendTempletUrl() {
		return sendTempletUrl;
	}

	public  String getMessUrl(String access_token,String media_id) {
		return  "https://api.weixin.qq.com/cgi-bin/media/get?access_token="+access_token+"&media_id="+media_id; //获得voiceURL
    }

	public String getUploadMediaUrl(String access_token,String type){
		return "https://api.weixin.qq.com/cgi-bin/media/upload?access_token="+access_token+"&type="+type;
	}
    
    public  String getTicket(String access_token) {
		return  "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";
    }
    
    public  String getInfo(String access_token,String openid) {
		return "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
    }
    
    public  String getAuthorize(String redirect_uri) {
		return "https://open.weixin.qq.com/connect/oauth2/authorize?" +
				"appid=" + wxConfigInfo.getWxappid() +
				"&redirect_uri=http://" + redirect_uri +
				"&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
    }
    
    public  String getOauth2(String appid,String appsecret,String code) {
    	String info = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    	info = info.replace("APPID", appid);
    	info = info.replace("SECRET", appsecret);
    	info = info.replace("CODE", code);
    	return info;
    }


	public  String getOauth2(String code) {
		return getOauth2(wxConfigInfo.getWxappid(), wxConfigInfo.getWxappsecret(), code);
	}
    
   
    
}
