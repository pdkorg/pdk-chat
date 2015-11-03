package com.pdk.chat.websocket.manage;

import com.pdk.chat.dto.User;

/**
 * Created by hubo on 2015/9/11
 */
public interface ChatSchedulerListener {

    void afterAddUserToChatRoom(User user, ChatRoom room);

    void afterOfflineUser(User user, ChatRoom room);

    void afterOnlineUser(User user, ChatRoom room);

}
