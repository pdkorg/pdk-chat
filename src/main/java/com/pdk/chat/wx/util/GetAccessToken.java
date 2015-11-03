package com.pdk.chat.wx.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.AppConfig;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.wx.dto.AccessToken;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class GetAccessToken {

    private final Object lock = new Object();

    public static AccessToken accessToken = new AccessToken();

    private WxConfigInfo wxConfigInfo = AppConfig.getBean(WxConfigInfo.class);

    private ExecutorService exec = Executors.newSingleThreadExecutor();

    public GetAccessToken() {
        Runnable fetchNewAccessTokenTask = new Runnable() {
            @Override
            public void run() {
                ChatLoggerUtil.getWxLogger().info(" Runnable fetchNewAccessTokenTask Run ! class : {}", this.getClass());
                while (!Thread.interrupted()) {
                    try {
                        synchronized (lock) {
                            fetchNewAccessToken();
                        }
                        ChatLoggerUtil.getWxLogger().info("fetchNewAccessToken : {}", accessToken);
                        TimeUnit.MINUTES.sleep(90);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                    } catch (Exception e) {
                        ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                    }
                }
            }
        };
        exec.execute(fetchNewAccessTokenTask);
    }

    public void resetAccessToken(final String resetToken) throws WeixinBusinessException {
        if (resetToken != null && !resetToken.equals(getAccessToken().getAccess_token())) {
            return;
        }
        synchronized (lock) {
            if (resetToken != null && !resetToken.equals(getAccessToken().getAccess_token())) {
                return;
            }

            ChatLoggerUtil.getSendToWxLogger().info("----------resetAccessToken method run!");

            fetchNewAccessToken();
        }

    }

    public void resetAccessToken() throws WeixinBusinessException {
        resetAccessToken(getAccessToken().getAccess_token());
    }

    private void fetchNewAccessToken() throws WeixinBusinessException {
        String str = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + wxConfigInfo.getWxappid() + "&secret=" + wxConfigInfo.getWxappsecret();
        ChatLoggerUtil.getSendToWxLogger().info("获取微信端token URL ：{}", str);
        String tokenJson = HttpSend.send(str);
        ChatLoggerUtil.getSendToWxLogger().info("获取从微信端token结果：{}", tokenJson);
        JSONObject token = JSON.parseObject(tokenJson);

        AccessToken newToken = new AccessToken();
        newToken.setAccess_token(token.getString("access_token"));
        newToken.setExpires_in(token.getString("expires_in"));
        newToken.setCreateTime(new Date());

        accessToken = newToken;
    }

    public AccessToken getAccessToken() throws WeixinBusinessException {
        return accessToken;
    }

    @PreDestroy
    public void destroy() {
        exec.shutdownNow();
    }

}
