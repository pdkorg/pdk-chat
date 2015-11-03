package com.pdk.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pdk.chat.model.ChatMsg;
import com.pdk.chat.util.MessageTypeConstant;
import com.pdk.chat.websocket.msg.ChatMessage;

import java.util.Date;

/**
 * Created by hubo on 2015/10/21
 */
public class ChatMsgJson {

    private int index;

    @JsonProperty(value = "DT_RowId", index = 0)
    private String id;

    private String csName;

    private String csId;

    private String userName;

    private String userId;

    private int source;

    private String sourceName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    private int type;

    private String typeName;

    private String content;

    public ChatMsgJson(int index,  String csId, String csName, String userId, String userName, ChatMsg chatMsg) {

        this.index = index;
        this.csName = csName;
        this.csId = csId;
        this.userName = userName;
        this.userId = userId;
        this.id = chatMsg.getId();
        this.content = chatMsg.getContent();
        this.source = chatMsg.getSource();
        this.createTime = chatMsg.getCreateTime();
        this.type = chatMsg.getMsgType();

        if(source == ChatMessage.SOURCE_WX) {
            sourceName = "微信";
        } else if(source == ChatMessage.SOURCE_APP) {
            sourceName = "APP";
        } else {
            sourceName = "客服";
        }

        switch (type) {
            case MessageTypeConstant.TYPE_TEXT:
                typeName = "文本";
                break;
            case MessageTypeConstant.TYPE_IMAGE:
                typeName = "图片";
                break;
            case MessageTypeConstant.TYPE_VOICE:
                typeName = "音频";
                break;
            default:
                typeName = "未知";
        }

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCsName() {
        return csName;
    }

    public void setCsName(String csName) {
        this.csName = csName;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
