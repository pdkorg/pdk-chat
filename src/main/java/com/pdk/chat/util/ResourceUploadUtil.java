package com.pdk.chat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.wx.util.MessageTypeEnum;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hubo on 2015/9/20
 */
public class ResourceUploadUtil {

    public static UploadResult uploadFile(MessageTypeEnum type, String path, InputStream in, String fileName) throws IOException {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addTextBody("token", CommonUtil.getResourceToken());
        builder.addTextBody("uploadType", type.getWxmsgType());
        builder.addTextBody("module", "CT");
        builder.addTextBody("path", path);
        builder.addBinaryBody("uploadFile", in, ContentType.MULTIPART_FORM_DATA, fileName);

        return uploadFile(CommonUtil.getResourceFileUploadPath(), builder.build(), new UploadResultHandler(){

            @Override
            public void handleResult(UploadResult result, HttpEntity content) throws IOException {
                JSONObject uploadJson = JSON.parseObject(EntityUtils.toString(content, "UTF-8"));
                result.setSuccess(uploadJson.getBoolean("result"));
                if(result.isSuccess()) {
                    result.addAttr("fileUri", uploadJson.getString("fileUri"));
                }
            }
        });
    }

    public static UploadResult uploadFileToWx(String url, InputStream in, String fileName) throws IOException {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addBinaryBody("media", in, ContentType.MULTIPART_FORM_DATA, fileName);

        return uploadFile(url, builder.build(), new UploadResultHandler() {

            @Override
            public void handleResult(UploadResult result, HttpEntity content) throws IOException {
                JSONObject uploadJson = JSON.parseObject(EntityUtils.toString(content, "UTF-8"));

                String errorCode = uploadJson.getString("errcode");

                result.setSuccess(errorCode == null);

                if (result.isSuccess()) {
                    result.addAttr("media_id", uploadJson.getString("media_id"));
                } else {
                    result.setErrorMsg(errorCode);
                }
            }
        });
    }


    public static UploadResult uploadFile (String url, HttpEntity params, UploadResultHandler handler) throws IOException {

        HttpPost post = new HttpPost(url);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        post.setEntity(params);

        HttpResponse response = httpClient.execute(post);

        UploadResult result = new UploadResult();

        if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
            HttpEntity entity = response.getEntity();
            if(entity != null){
                handler.handleResult(result, entity);
            }
        } else {
            result.setSuccess(false);
            result.setErrorMsg(String.valueOf(response.getStatusLine().getStatusCode()));
        }

        return result;

    }


}
