package com.pdk.chat.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.wx.util.HttpSend;
import com.pdk.chat.wx.util.PdkManagerUrlInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by kangss on 2015/10/14.
 */
@Service
public class WXShareService {
    public JSONObject shareGetGift(String sourceId) throws WeixinBusinessException {
        String url = PdkManagerUrlInfo.share.replace("{sourceId}",sourceId);
        String response = HttpSend.send(url,null,RequestMethod.GET);
        if(StringUtils.isEmpty(response))
            throw new WeixinBusinessException("您今天参与过活动啦，明天再试试吧");
        JSONObject jobj = JSON.parseObject(response);
        if(jobj.getJSONObject("coupon") == null)
            throw new WeixinBusinessException("您今天参与过活动啦，明天再试试吧");
        return jobj;
    }
}
