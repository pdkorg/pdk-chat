package com.pdk.chat.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.dto.User;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.wx.dto.WeixinFans;
import com.pdk.chat.wx.util.HttpSend;
import com.pdk.chat.wx.util.PdkManagerUrlInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by kangss on 2015/8/29
 */
@Service
public class FansService {

    public User queryByOpenid(String openid) throws WeixinBusinessException {
        String url = PdkManagerUrlInfo.queryUser;
        url = url.replace("{sourceId}",openid);
        String json = HttpSend.send(url,null, RequestMethod.POST);
        JSONObject jobj = JSONObject.parseObject(json);
        if(!"success".equals(jobj.get("result"))) {
            throw new WeixinBusinessException("查询用户信息时异常："+jobj.get("msg"));
        }
        User user = new User();
        JSONObject jUser = (JSONObject) jobj.get("user");
        if(jUser != null) {
            user.setHeaderImgPath(jUser.getString("headerImg"));
            user.setId(jUser.getString("id"));
            user.setName(jUser.getString("name"));
        }
        user.setSourceId(openid);
        user.setUserType("微信");
        return user;
    }

    public int countByOpenid(String openid) throws WeixinBusinessException {
        String url = PdkManagerUrlInfo.countUrl;
        url = url.replace("{sourceId}",openid);
        String json = HttpSend.send(url,null, RequestMethod.POST);
        JSONObject jobj = JSONObject.parseObject(json);
        if(!"success".equals(jobj.get("result"))) {
            throw new WeixinBusinessException("注册服务用户时异常："+jobj.get("msg"));
        }
        return jobj.getInteger("count");
    }

    public void subscribe(WeixinFans fans, String eventKey) throws WeixinBusinessException {
        String url = PdkManagerUrlInfo.userUrl;
        String param = "name="+StringUtils.defaultString(fans.getNickname())
                + "&type=0&sex="+StringUtils.defaultString(fans.getSex())
                +"&headerImg="+StringUtils.defaultString(fans.getHeadimgurl())
                +"&sourceId="+fans.getOpenid()
                +"&eventKey="+StringUtils.defaultString(eventKey);
        String json = HttpSend.send(url,param, RequestMethod.POST);
        JSONObject jobj = JSONObject.parseObject(json);
        if(!"success".equals(jobj.get("result"))) {
            throw new WeixinBusinessException("注册服务用户时异常："+jobj.get("msg"));
        }
    }

    public void unsubscribe(String openid) throws WeixinBusinessException {
        String url = PdkManagerUrlInfo.unsubscribeUrl+"/"+openid;
        String json = HttpSend.send(url,"", RequestMethod.PUT);
        JSONObject jobj = JSONObject.parseObject(json);
        if(!"success".equals(jobj.get("result"))) {
            throw new WeixinBusinessException("用户取消关注时异常："+jobj.get("msg"));
        }
    }
}
