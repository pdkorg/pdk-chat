package com.pdk.chat.wx.strategy.itf;

import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;

/**
 * Created by kangss on 2015/8/24.
 */
public interface WeixinMessageStrategy {

    void afterSendToCS(WeixinMessageReceive message) throws WeixinBusinessException;

    void beforeSendToCS(WeixinMessageReceive message) throws WeixinBusinessException;

    String getResponse(WeixinMessageReceive message) throws WeixinBusinessException;

    ChatMessage transformToCS(WeixinMessageReceive message) throws WeixinBusinessException;

    WeixinMessageSend transformToWX(ChatMessage message) throws WeixinBusinessException;

    void afterSendToWX(ChatMessage message) throws WeixinBusinessException;

    void beforeSendToWX(ChatMessage message) throws WeixinBusinessException;

}
