package com.pdk.chat.wx.message.receive;


import com.pdk.chat.wx.message.base.WeixinMessageReceive;

/**
 * Created by kangss on 2015/8/23
 */
public class ImageMessageReceive extends WeixinMessageReceive {
	// 图片链接
	private String PicUrl;

	private String MediaId;

	private String mediaUrl;

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public String getPicUrl() {
		return PicUrl;

	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	@Override
	public String toString() {
		return "ImageMessageReceive{" +
				"PicUrl='" + PicUrl + '\'' +
				", MediaId='" + MediaId + '\'' +
				", mediaUrl='" + mediaUrl + '\'' +
				"} " + super.toString();
	}
}


