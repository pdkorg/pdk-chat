package com.pdk.chat.dto;

/**
 * Created by hubo on 2015/10/28
 */
public class UserJson {

    private String id;

    private String sourceId;

    private String name;

    private String headImgHtml;

    public UserJson(String id, String sourceId, String name, String img) {
        this.id = id;
        this.sourceId = sourceId;
        this.name = name;
        this.headImgHtml = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImgHtml() {
        return headImgHtml;
    }

    public void setHeadImgHtml(String headImgHtml) {
        this.headImgHtml = headImgHtml;
    }

}
