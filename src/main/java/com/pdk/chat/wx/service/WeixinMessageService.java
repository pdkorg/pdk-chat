package com.pdk.chat.wx.service;

import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.AppConfig;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.ChatMessageObjectPool;
import com.pdk.chat.util.MessageTypeConstant;
import com.pdk.chat.websocket.manage.ChatManager;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.session.WeixinMessageSession;
import com.pdk.chat.wx.session.WeixinSessionManager;
import com.pdk.chat.wx.strategy.fatory.WeixinEventStrategyFactory;
import com.pdk.chat.wx.strategy.fatory.WeixinMessageStrategyFactory;
import com.pdk.chat.wx.strategy.itf.WeixinEventStrategy;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.WeixinSendUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kangss on 2015/8/23
 */
@Service
public class WeixinMessageService {

    private WeixinMessageStrategyFactory factory;

    private WeixinEventStrategyFactory eventFactory;

    private ChatManager chatManager;

    private ExecutorService exec = Executors.newCachedThreadPool();

    private ChatManager getChatManager() {
        if (chatManager == null)
            chatManager = AppConfig.getBean(ChatManager.class);
        return chatManager;
    }

    private WeixinMessageStrategyFactory getFactory() {
        if (factory == null)
            factory = AppConfig.getBean(WeixinMessageStrategyFactory.class);
        return factory;
    }

    private WeixinEventStrategyFactory getEvnetFactory() {
        if (eventFactory == null)
            eventFactory = AppConfig.getBean(WeixinEventStrategyFactory.class);
        return eventFactory;
    }

    /**
     * Message
     * 接收微信发送来的消息，根据消息消息类型进行不同的业务处理后，发送给客服
     *
     * @param message 微信发送信息
     * @return response
     */
    public String receiveMessage(final WeixinMessageReceive message, final WeixinMessageSession session) throws BusinessException {
        final WeixinMessageStrategy strategy = getFactory().create(message);
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    strategy.beforeSendToCS(message);
                    ChatMessage chatMessage = strategy.transformToCS(message);
                    chatMessage.setFromId(session.getUser().getId());
                    session.receiveMessage(chatMessage);
                    strategy.afterSendToCS(message);
                } catch (BusinessException e) {
                    ChatLoggerUtil.getWxLogger().error(e.getMessage(), e);
                }
            }
        });
        return strategy.getResponse(message);
    }

    /**
     * 发送给客户端（微信、APP）消息
     * @param message 聊天消息
     * @param openId openId
     * @return 发送消息返回结果
     * @throws BusinessException
     */
    public String sendMessage(final ChatMessage message, final String openId) throws BusinessException {

        final ChatMessage sendMessage = ChatMessageObjectPool.borrow();

        message.cloneTo(sendMessage);

        final WeixinMessageStrategy strategy = getFactory().create(sendMessage);

        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    strategy.beforeSendToWX(sendMessage);
                    WeixinMessageSend wxmsg = strategy.transformToWX(sendMessage);
                    if(wxmsg != null) {
                        wxmsg.setTouser(openId);
                        ChatLoggerUtil.getSendToWxLogger().info("待发送到微信的信息：{}", wxmsg);
                        String result = WeixinSendUtil.send(wxmsg);
                        ChatLoggerUtil.getSendToWxLogger().info("发送到微信的信息结果：{}", result);
                        strategy.afterSendToWX(sendMessage);
                    }
                } catch (BusinessException e) {
                    ChatLoggerUtil.getSendToWxLogger().info("发送到微信的信息结果：{}", e.getMessage());
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                    ChatMessage error = ChatMessageObjectPool.borrow();
                    error.setFromId(sendMessage.getSendToId());
                    error.setCreateTime(new Date());
                    error.setMsgType(MessageTypeConstant.TYPE_SEND_CLIENT_ERROR);
                    error.setContent("消息发送失败：" + e.getMessage());
                    error.setSource(ChatMessage.SOURCE_WX);
                    getChatManager().sendMsgToCS(error);
                } finally {
                    ChatMessageObjectPool.returnObj(sendMessage);
                }
            }
        });
        return "";
    }

    public String handleEvent(final WeixinMessageReceive message, final WeixinSessionManager manager) throws WeixinBusinessException {

        final WeixinEventStrategy strategy = getEvnetFactory().createEventStrategy(message);

        String result = strategy.handleEvent(message);

        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    strategy.asyncNotify(message, manager);
                } catch (BusinessException e) {
                    ChatLoggerUtil.getWxLogger().error(e.getMessage(), e);
                }
            }
        });

        return result;
    }

}

