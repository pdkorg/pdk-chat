package com.pdk.chat.wx.util;

import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.util.ResourceUploadUtil;
import com.pdk.chat.util.UploadResult;
import com.pdk.chat.wx.dto.MediaInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class MediaMessageUtil {
	@Autowired
	private GetAccessToken getAccessToken;
	@Autowired
	WeixinUrlInfo urlInfo;
	public MediaInfo getMediaPath(String mediaId,MessageTypeEnum typeEnum) throws WeixinBusinessException {
		String url = urlInfo.getMessUrl(getAccessToken.getAccessToken().getAccess_token(), mediaId);
		return sendRequest(url, typeEnum);
	}

	private MediaInfo sendRequest(String url,MessageTypeEnum typeEnum) throws WeixinBusinessException{
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient client =  httpClientBuilder.build();
		HttpGet get = new HttpGet(url);
		String filename = null;
		MediaInfo info = new MediaInfo();
		try {
			HttpResponse res = client.execute(get);
			Header[] h = res.getHeaders("Content-disposition");
			for (Header header : h) {
				if(header.getValue().contains("filename=")){
					filename = header.getValue().substring(header.getValue().indexOf("filename=")+10, header.getValue().length()-1);
				}
			}
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				UploadResult result = ResourceUploadUtil.uploadFile(typeEnum, "/wx", entity.getContent(), filename);
				if(result.isSuccess()) {
					ChatLoggerUtil.getInfoLogger().info("上传文件URI：{}", result.getAttr("fileUri"));
					info.setUrlPath(result.getAttr("fileUri"));
				}else {
					throw new BusinessException("上传失败：" + result.getErrorMsg());
				}
			}
			return info;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}


	public String uploadMedia(String filePath, MessageTypeEnum typeEnum) throws WeixinBusinessException {

		try {
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

			URL mediaUrl = new URL(CommonUtil.getResourcePath() + "/" + filePath);

			String wxUploadUrl = urlInfo.getUploadMediaUrl(getAccessToken.getAccessToken().getAccess_token(),typeEnum.getWxmsgType());

			UploadResult r = ResourceUploadUtil.uploadFileToWx(wxUploadUrl, mediaUrl.openStream(), fileName);

			if(r.isSuccess()) {
				return r.getAttr("media_id");
			} else {
				throw new WeixinBusinessException(r.getErrorMsg());
			}

		} catch (WeixinBusinessException e){
			throw e;
		} catch (Exception e) {
			throw new WeixinBusinessException(e.getMessage(), e);
		}
	}
}
