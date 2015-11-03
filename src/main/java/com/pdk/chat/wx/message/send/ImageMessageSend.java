package com.pdk.chat.wx.message.send;


import com.pdk.chat.wx.message.base.WeixinMessageSend;

/**
 * Created by kangss on 2015/8/23
 */
public class ImageMessageSend extends WeixinMessageSend {
    private Image image = new Image();
    public void setMedia_id(String media_id){
        image.setMedia_id(media_id);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public class Image{
        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "media_id='" + media_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ImageMessageSend{" +
                "image=" + image +
                "} " + super.toString();
    }
}


