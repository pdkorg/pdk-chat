package com.pdk.chat.wx.util;

import com.pdk.chat.util.AppConfig;

/**
 * Created by kangss on 2015/9/1
 */
public class PdkManagerUrlInfo {
    public static String userUrl = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/sm_user";
    public static String countUrl = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/sm_user/select_user_count/{sourceId}";
    public static String unsubscribeUrl = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/sm_user/un_register";
    public static String queryUser = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/sm_user/select_user/{sourceId}";
    public static String payOrder = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/order/pay/{orderId}/{couponId}/{actMny}/{couponMny}/{tip}";
    public static String queryUserOrderUrl = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/order/user/{sourceId}";
    public static String queryOrderDetailUrl = "http://"+AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl()+"/wechat/order/{orderId}";
    public static String effectCouponUrl = "http://" + AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl() + "/wechat/coupon/user/{sourceId}";
    public static String overdueCouponUrl = "http://" + AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl() + "/wechat/coupon/user/{sourceId}/overdue";
    public static String employeeScoreUrl = "http://" + AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl() + "/wechat/review/employee/{sourceId}";
    public static String checkEmployeeScoreUrl = "http://" + AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl() + "/wechat/review/employee";
    public static String reviewOrder = "http://" + AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl() + "/wechat/order/review/{orderId}";
    public static String share = "http://" + AppConfig.getBean(WxConfigInfo.class).getPdkmanagerurl() + "/wechat/coupon/user/{sourceId}/share";
}
