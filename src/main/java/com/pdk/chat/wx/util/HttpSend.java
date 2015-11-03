package com.pdk.chat.wx.util;

import com.pdk.chat.exception.WeixinBusinessException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component("weixinSend")
public class HttpSend {

    public static String send(String action,String param) throws WeixinBusinessException{
       return send( action, param, RequestMethod.POST);
    }

    public static String send(String action,String param,RequestMethod method) throws WeixinBusinessException{
        InputStream is = null;
        try {
            is = request(action,param,method);
            return IOUtils.toString(is,"UTF-8");
        }catch (Exception e) {
            throw new WeixinBusinessException("向微信发送消息时异常",e);
        }finally{
            if(is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    
    public static String send(String action) throws WeixinBusinessException {
        return send(action, null);
    }

    public static InputStream request(String action,String param,RequestMethod method) throws WeixinBusinessException{
        URL url;
        try {
            url = new URL(action);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method.name());
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            OutputStream os ;
            if (param != null) {
                os = http.getOutputStream();
                os.write(param.getBytes("UTF-8"));// 传入参数
                os.flush();
                os.close();
            }
            return http.getInputStream();
        }catch (Exception e) {
            throw new WeixinBusinessException("向微信发送消息时异常",e);
        }
    }
}
