package com.pdk.chat.wx.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by kangss on 2015/8/26
 */
@Component
public class WxConfigInfo {
    @Value("#{wxConfig['wxappid']}")
    private String wxappid;
    @Value("#{wxConfig['wxappsecret']}")
    private String wxappsecret;
    @Value("#{wxConfig['token']}")
    private String token;
    @Value("#{wxConfig['partner']}")
    private String partner;
    @Value("#{wxConfig['partnerkeykey']}")
    private String partnerkeykey;
    @Value("#{wxConfig['ffmpeg']}")
    private String ffmpeg;
    @Value("#{wxConfig['pdkmanagerurl']}")
    private String pdkmanagerurl;
    @Value("#{wxConfig['pdkchaturl']}")
    private String pdkchaturl;
    public String getFfmpeg() {
        return ffmpeg;
    }

    public String getPdkmanagerurl() {
        return pdkmanagerurl;
    }



    public String getWxappid() {
        return wxappid;
    }

    public String getWxappsecret() {
        return wxappsecret;
    }

    public String getToken() {
        return token;
    }

    public String getPartner() {
        return partner;
    }

    public String getPartnerkeykey() {
        return partnerkeykey;
    }

    public String getPdkchaturl() {
        return pdkchaturl;
    }
}
