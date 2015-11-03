package com.pdk.chat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection="pdk_chat_chatmsg")
public class ChatMsg implements IBaseVO{
    @Id
    private String id;

    @Indexed
    private String fromId;

    private String fromName;

    private String fromHeadImg;

    @Indexed
    private String sendToId;

    private String sendToName;

    private String sendToHeadImg;

    private Short msgType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TextIndexed
    private String content;

    private Short source;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ts;

    private Short dr;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return from_id
     */
    public String getFromId() {
        return fromId;
    }

    /**
     * @param fromId
     */
    public void setFromId(String fromId) {
        this.fromId = fromId == null ? null : fromId.trim();
    }

    /**
     * @return from_name
     */
    public String getFromName() {
        return fromName;
    }

    /**
     * @param fromName
     */
    public void setFromName(String fromName) {
        this.fromName = fromName == null ? null : fromName.trim();
    }

    /**
     * @return send_to_id
     */
    public String getSendToId() {
        return sendToId;
    }

    /**
     * @param sendToId
     */
    public void setSendToId(String sendToId) {
        this.sendToId = sendToId == null ? null : sendToId.trim();
    }

    /**
     * @return send_to_name
     */
    public String getSendToName() {
        return sendToName;
    }

    /**
     * @param sendToName
     */
    public void setSendToName(String sendToName) {
        this.sendToName = sendToName == null ? null : sendToName.trim();
    }

    /**
     * @return msg_type
     */
    public Short getMsgType() {
        return msgType;
    }

    /**
     * @param msgType
     */
    public void setMsgType(Short msgType) {
        this.msgType = msgType;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * @return source
     */
    public Short getSource() {
        return source;
    }

    /**
     * @param source
     */
    public void setSource(Short source) {
        this.source = source;
    }

    /**
     * @return ts
     */
    public Date getTs() {
        return ts;
    }

    /**
     * @param ts
     */
    public void setTs(Date ts) {
        this.ts = ts;
    }

    /**
     * @return dr
     */
    public Short getDr() {
        return dr;
    }

    /**
     * @param dr
     */
    public void setDr(Short dr) {
        this.dr = dr;
    }

    public String getSendToHeadImg() {
        return sendToHeadImg;
    }

    public void setSendToHeadImg(String sendToHeadImg) {
        this.sendToHeadImg = sendToHeadImg;
    }

    public String getFromHeadImg() {
        return fromHeadImg;
    }

    public void setFromHeadImg(String fromHeadImg) {
        this.fromHeadImg = fromHeadImg;
    }
}