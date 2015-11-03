package com.pdk.chat.wx.message.receive;

import com.pdk.chat.wx.message.base.WeixinMessageReceive;
/**
 * Created by kangss on 2015/8/23
 */
public class VoiceMessageReceive extends WeixinMessageReceive {
	private String MediaId;
	private String Format;
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

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	@Override
	public String toString() {
		return "VoiceMessageReceive{" +
				"MediaId='" + MediaId + '\'' +
				", Format='" + Format + '\'' +
				", mediaUrl='" + mediaUrl + '\'' +
				"} " + super.toString();
	}
}

