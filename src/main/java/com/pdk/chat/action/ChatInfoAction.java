package com.pdk.chat.action;

import com.pdk.chat.dto.ChatRoomJson;
import com.pdk.chat.dto.User;
import com.pdk.chat.websocket.manage.ChatManager;
import com.pdk.chat.websocket.manage.ChatRoom;
import com.pdk.chat.wx.session.WeixinSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hubo on 2015/9/13
 */
@Controller
@RequestMapping("/info")
public class ChatInfoAction {

    @Autowired
    private ChatManager chatManager;

    @RequestMapping("/list")
    public String index(Map<String, Object> map) {

        List<User> csList = chatManager.getOnlineCSList();
        List<User> userList = chatManager.getOnlineUserList();
        List<ChatRoomJson> chatRooms = new ArrayList<>();
        for (User cs : csList) {
            ChatRoom r = chatManager.findChatRoomByCS(cs);
            chatRooms.add(new ChatRoomJson(r.getCs(), r.getUserList()));
        }

        List<User> wxUserList = WeixinSessionManager.getInstance().listUser();

        map.put("csList", csList);
        map.put("userList", userList);
        map.put("wxUserList", wxUserList);
        map.put("chatRoomList", chatRooms);

        return "list";
    }

}
