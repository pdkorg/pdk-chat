package com.pdk.chat.websocket.msg;

import com.pdk.chat.util.IdGenerator;
import com.pdk.chat.util.MessageTypeConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hubo on 2015/8/20
 */
public class ChatMessage {

    public static final int SOURCE_APP = 1;

    public static final int SOURCE_WX = 2;

    public ChatMessage() {
        this.id = IdGenerator.generateId();
    }

    private String id;

    private String fromId;

    private String fromName;

    private String fromHeadImg;

    private String sendToId;

    private String sendToName;

    private String sendHeadImg;

    private Date createTime;

    private int msgType = MessageTypeConstant.TYPE_TEXT;

    private String content;

    private int source;

    public boolean isOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    private boolean isOffline = false;

    private Map<String,String> attribute = new HashMap<>();

    public void addAttribute(String name,String value){
        attribute.put(name,value);
    }

    public String getAttribute(String name){
        return attribute.get(name);
    }

    public String getId() {
        return id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return this.fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSendToId() {
        return sendToId;
    }

    public void setSendToId(String sendToId) {
        this.sendToId = sendToId;
    }

    public String getSendToName() {
        return sendToName;
    }

    public void setSendToName(String sendToName) {
        this.sendToName = sendToName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getSendHeadImg() {
        return sendHeadImg;
    }

    public void setSendHeadImg(String sendHeadImg) {
        this.sendHeadImg = sendHeadImg;
    }

    public String getFromHeadImg() {
        return fromHeadImg;
    }

    public void setFromHeadImg(String fromHeadImg) {
        this.fromHeadImg = fromHeadImg;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", fromId='" + fromId + '\'' +
                ", fromName='" + fromName + '\'' +
                ", fromHeadImg='" + fromHeadImg + '\'' +
                ", sendToId='" + sendToId + '\'' +
                ", sendToName='" + sendToName + '\'' +
                ", sendHeadImg='" + sendHeadImg + '\'' +
                ", createTime=" + createTime +
                ", msgType=" + msgType +
                ", content='" + content + '\'' +
                ", source=" + source +
                ", isOffline=" + isOffline +
                ", attribute=" + attribute +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage message = (ChatMessage) o;

        return !(id != null ? !id.equals(message.id) : message.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void reset() {
        fromId = null;
        fromName = null;
        fromHeadImg = null;
        sendToId = null;
        sendToName = null;
        sendHeadImg = null;
        createTime = null;
        msgType  = MessageTypeConstant.TYPE_TEXT;
        content = null;
        source = 0;
        isOffline = false;
        attribute.clear();
    }

    public void cloneTo(ChatMessage message) {
        message.setFromId(this.getFromId());
        message.setFromName(this.getFromName());
        message.setFromHeadImg(this.getFromHeadImg());
        message.setSendToId(this.getSendToId());
        message.setSendToName(this.getSendToName());
        message.setSendHeadImg(this.getSendHeadImg());
        message.setCreateTime(this.getCreateTime());
        message.setMsgType(this.getMsgType());
        message.setSource(this.getSource());
        message.setContent(this.getContent());
        message.setIsOffline(this.isOffline());
        for (Map.Entry<String, String> entry : attribute.entrySet()) {
            message.addAttribute(entry.getKey(), entry.getValue());
        }
    }
}
