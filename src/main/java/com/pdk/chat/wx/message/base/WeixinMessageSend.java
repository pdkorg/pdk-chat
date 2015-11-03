package com.pdk.chat.wx.message.base;


public class WeixinMessageSend {
	private String touser;
	private String msgtype;

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	@Override
	public String toString() {
		return "WeixinMessageSend{" +
				"touser='" + touser + '\'' +
				", msgtype='" + msgtype + '\'' +
				'}';
	}
}

