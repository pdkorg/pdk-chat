package com.pdk.chat.wx.message.send;

import com.pdk.chat.wx.message.base.WeixinMessageSend;

/**
 * Created by kangss on 2015/8/23.
 */
public class TextMessageSend extends WeixinMessageSend {
    private Text text = new Text();

    public Text getText() {
        return text;
    }

    public void setContent(String content){
        text.setContent(content);
    }
    public class Text{
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Text{" +
                    "content='" + content + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TextMessageSend{" +
                "text=" + text +
                "} " + super.toString();
    }
}
