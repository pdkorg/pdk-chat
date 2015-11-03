package com.pdk.chat.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hubo on 2015/9/20
 */
public class UploadResult {

    private boolean success;

    private Map<String, String> attrs = new HashMap<>();

    private String errorMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void addAttr(String key, String value) {
        attrs.put(key, value);
    }

    public String getAttr(String key) {
        return attrs.get(key);
    }

}
