package com.pdk.chat.wx.message.receive;

/**
 * Created by kangss on 2015/8/25
 */
public class SubscribeEventMessageReceive extends EventMessageReceive {
	private String EventKey ;
	private String Ticket;

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	@Override
	public String toString() {
		return "SubscribeEventMessageReceive{" +
				"EventKey='" + EventKey + '\'' +
				", Ticket='" + Ticket + '\'' +
				"} " + super.toString();
	}
}

