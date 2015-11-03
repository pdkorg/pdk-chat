package com.pdk.chat.wx.message.send;

import com.pdk.chat.wx.message.base.WeixinMessageSend;

/**
 * Created by kangss on 2015/8/23
 */
public class VoiceMessageSend extends WeixinMessageSend {
    private Voice voice = new Voice();
    public void setMedia_id(String media_id){
        voice.setMedia_id(media_id);
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public class Voice{
        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        @Override
        public String toString() {
            return "Voice{" +
                    "media_id='" + media_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VoiceMessageSend{" +
                "voice=" + voice +
                "} " + super.toString();
    }
}

