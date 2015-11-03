package com.pdk.chat.wx.message.receive;

import com.pdk.chat.wx.message.base.WeixinMessageReceive;

/**
 * Created by kangss on 2015/9/1
 */
public class EventMessageReceive extends WeixinMessageReceive {
    private String Event ;

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    @Override
    public String toString() {
        return "EventMessageReceive{" +
                "Event='" + Event + '\'' +
                "} " + super.toString();
    }
}
