package com.pdk.chat.wx.message.receive;

import com.pdk.chat.wx.message.base.WeixinMessageReceive;

/**
 * Created by kangss on 2015/8/23.
 */
public class LinkMessageReceive extends WeixinMessageReceive {
	private String Title;
	private String Description;
	private String Url;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}
}

