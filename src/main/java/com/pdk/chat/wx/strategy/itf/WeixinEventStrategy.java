package com.pdk.chat.wx.strategy.itf;

import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.session.WeixinSessionManager;

/**
 * Created by hubo on 2015/9/23
 */
public interface WeixinEventStrategy {

    String handleEvent(WeixinMessageReceive message) throws WeixinBusinessException;

    void asyncNotify(WeixinMessageReceive message,  WeixinSessionManager manager) throws BusinessException;

}
