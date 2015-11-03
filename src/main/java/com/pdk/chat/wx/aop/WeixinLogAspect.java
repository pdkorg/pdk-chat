package com.pdk.chat.wx.aop;

import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.receive.TextMessageReceive;
import com.pdk.chat.wx.message.send.TextMessageSend;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Created by hubo on 2015/9/23
 */
@Aspect
@Component
public class WeixinLogAspect {

    @Around("execution(* com.pdk.chat.wx.session.WeixinSessionManager.sendMessageToCS(..))")
    public Object aroundReceiveMessage(ProceedingJoinPoint joinPoint) throws Throwable{

        if(joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            WeixinMessageReceive messageReceive = (WeixinMessageReceive) joinPoint.getArgs()[0];
            ChatLoggerUtil.getWxLogger().info("收到微信消息：{}", messageReceive);
        }

        Object result = joinPoint.proceed();

        ChatLoggerUtil.getWxLogger().info("返回给微信的response：{}", result);

        return result;
    }


}
