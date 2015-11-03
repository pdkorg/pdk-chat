package com.pdk.chat.wx.message.receive;

import com.pdk.chat.wx.message.base.WeixinMessageReceive;

/**
 * Created by kangss on 2015/8/23
 */
public class TextMessageReceive extends WeixinMessageReceive {
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @Override
    public String toString() {
        return "TextMessageReceive{" +
                "Content='" + Content + '\'' +
                "} " + super.toString();
    }
}
