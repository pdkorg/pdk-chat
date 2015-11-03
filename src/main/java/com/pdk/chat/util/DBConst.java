package com.pdk.chat.util;

/**
 * Created by hubo on 2015/8/13.
 */
public class DBConst {

    public static final String ID = "id";

    public static final String DR = "dr";

    public static final String TS = "ts";

    public static final String STATUS = "status";

    /** 状态-启用 */
    public static final short STATUS_ENABLE = 0;

    /** 状态-停用 */
    public static final short STATUS_DISABLE = 1;

    /** 删除标识-正常 */
    public static final short DR_NORMAL = 0;

    /** 删除标识-删除 */
    public static final short DR_DEL = 1;

    /** 性别-男 */
    public static final Short SEX_MALE = 0;

    /** 性别-女 */
    public static final Short SEX_FEMALE = 1;

    /** 用户类型-微信用户 */
    public static final Short USER_TYPE_WEIXIN = 0;

    /** 用户类型-注册用户 */
    public static final Short USER_TYPE_REGISTERED = 1;
}
