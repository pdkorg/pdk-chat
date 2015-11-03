package com.pdk.chat.wx.message.receive;

/**
 * Created by kangss on 2015/8/25
 */
public class ViewMenuEventMessageReceive extends EventMessageReceive {
	private String EventKey;

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	@Override
	public String toString() {
		return "ViewMenuEventMessageReceive{" +
				"EventKey='" + EventKey + '\'' +
				"} " + super.toString();
	}
}

