package com.pdk.chat.dto;

import java.util.List;

/**
 * Created by hubo on 2015/10/20
 */
public class ChatRoomJson {

    public ChatRoomJson(User cs, List<User> userList) {
        this.cs = cs;
        this.userList = userList;
    }

    private final User cs;

    private final List<User> userList;

    public User getCs() {
        return cs;
    }

    public List<User> getUserList() {
        return userList;
    }

}
