package com.pdk.chat.wx.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.WeixinBusinessException;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by hubo on 15/10/2
 */
public class BaiduLocationUtil {

    private static final String locationAPI = "http://api.map.baidu.com/geocoder/v2/?ak=Uo8fS4lmKGCabTErIUEIyfkt&coordtype=wgs84ll&output=json";

    /**
     * 解析GPS坐标并转换成位置信息
     * @param Latitude 纬度
     * @param Longitude 经度
     * @return 位置信息
     */
    public static String analyseGPSToLoction(String Latitude, String Longitude) throws WeixinBusinessException {

        if(Latitude == null || Longitude == null) {
            return "无法解析GPS坐标！";
        }

        String locationParam = "location=" + Latitude + "," + Longitude;

        String jsonResult = HttpSend.send(locationAPI + "&" + locationParam, null, RequestMethod.GET);

        JSONObject obj = JSON.parseObject(jsonResult);

        int status = obj.getInteger("status");

        if(status == 0) {

           return obj.getJSONObject("result").getString("formatted_address");

        }

        return "无法解析GPS坐标！";
    }

}
