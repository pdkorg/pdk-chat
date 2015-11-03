package com.pdk.chat.wx.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.AppConfig;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.wx.message.base.WeixinMessageSend;

/**
 * Created by kangss on 2015/9/13
 */
public class WeixinSendUtil {


    public static String send(WeixinMessageSend wxmsg) throws WeixinBusinessException {
        return send( wxmsg,0);
    }

    private static String send(WeixinMessageSend wxmsg,int i) throws WeixinBusinessException {
        GetAccessToken getAccessToken = AppConfig.getBean(GetAccessToken.class);
        String accessToken = getAccessToken.getAccessToken().getAccess_token();
        String weixinSendUrl = AppConfig.getBean(WeixinUrlInfo.class).getSendUrl() + accessToken;
        ChatLoggerUtil.getSendToWxLogger().info((i!=0?"--第["+i+"]次重发--":"")+"发送给微信的消息URL：{}", weixinSendUrl);
        String response = HttpSend.send(weixinSendUrl, JSONObject.toJSONString(wxmsg));
        JSONObject jobj = JSON.parseObject(response);
        if(jobj.getInteger("errcode") == 0){
            return "success";
        }else{
            if(jobj.getInteger("errcode") == 40001){
                getAccessToken.resetAccessToken(accessToken);
                if(i < 4){
                    return send(wxmsg,i+1);
                }else{
                    throw new WeixinBusinessException(jobj.get("errcode") + ":" + jobj.get("errmsg"));
                }
            }else {
                throw new WeixinBusinessException(jobj.get("errcode") + ":" + jobj.get("errmsg"));
            }
        }
    }
}
