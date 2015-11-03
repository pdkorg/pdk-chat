package com.pdk.chat.util;

import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hubo on 2015/8/25
 */
public class CommonUtil {


    public static String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static Date toDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String formatDate() {
        return formatDate(new Date());
    }

    public static Date startOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    /**
     * 当天的结束时间
     * @return
     */
    public static Date endOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static String getRealPath(String resource) {
        return ContextLoaderListener.getCurrentWebApplicationContext().getServletContext().getRealPath(resource);
    }

    /**
     * 获取微信媒体真实路径
     * @return 微信媒体路径
     */
    public static String getWxMediaRealPath() {
        return getRealPath("wxmedia");
    }


    public static String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public static String getContextPath() {
        return ContextLoaderListener.getCurrentWebApplicationContext().getServletContext().getContextPath();
    }

    public static String getResourceToken() {
        return ContextLoaderListener.getCurrentWebApplicationContext().getServletContext().getInitParameter("resource_token");
    }

    public static String getResourcePath() {
        return ContextLoaderListener.getCurrentWebApplicationContext().getServletContext().getInitParameter("resource_path");
    }

    public static String getResourceFileUploadPath() {
        return getResourcePath() + "/file/upload";
    }
}
