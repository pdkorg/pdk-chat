package com.pdk.chat.util;

import org.apache.http.HttpEntity;

import java.io.IOException;

/**
 * Created by hubo on 2015/9/20.
 */
public interface UploadResultHandler {
    void handleResult(UploadResult result, HttpEntity content) throws IOException;
}
