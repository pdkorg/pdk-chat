package com.pdk.chat.exception;

/**
 * 业务异常
 * Created by hubo on 2015/8/13
 */
public class WeixinBusinessException  extends BusinessException{


    public WeixinBusinessException() {
        super();
    }


    public WeixinBusinessException(String message) {
        super(message);
    }


    public WeixinBusinessException(String message, Throwable cause) {
        super(message, cause);
    }


    public WeixinBusinessException(Throwable cause) {
        super(cause);
    }


    protected WeixinBusinessException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
