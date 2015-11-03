package com.pdk.chat.wx.message.base;


public class WeixinMessageReceive {

	private String ToUserName;
	private String FromUserName;
	private String CreateTime;
	private String MsgType;
	private String MsgId;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}

	public String getMsgId() {
		return MsgId;
	}

	public void setMsgId(String msgId) {
		MsgId = msgId;
	}

	@Override
	public String toString() {
		return "WeixinMessageReceive{" +
				"ToUserName='" + ToUserName + '\'' +
				", FromUserName='" + FromUserName + '\'' +
				", CreateTime='" + CreateTime + '\'' +
				", MsgType='" + MsgType + '\'' +
				", MsgId='" + MsgId + '\'' +
				'}';
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeixinMessageReceive that = (WeixinMessageReceive) o;

        return !(MsgId != null ? !MsgId.equals(that.MsgId) : that.MsgId != null);

    }

    @Override
    public int hashCode() {
        return MsgId != null ? MsgId.hashCode() : 0;
    }
}

