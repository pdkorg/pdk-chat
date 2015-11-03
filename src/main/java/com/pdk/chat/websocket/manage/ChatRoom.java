package com.pdk.chat.websocket.manage;

import com.pdk.chat.dto.User;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.CommonUtil;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室
 * <p>基于一个客服，多个用户模式</p>
 * Created by hubo on 2015/8/21
 */
public class ChatRoom implements Comparable<ChatRoom>{

    private static final Logger log = ChatLoggerUtil.getChatLogger();

    private User cs;

    private List<User> userList;

    private Map<User, Date> userAddChatRoomTime;

    private int roomSize = 7;

    public ChatRoom(User cs) {
        this.cs = cs;
        userList = new LinkedList<>();
        userAddChatRoomTime = new HashMap<>();
        log.info("{} ---- 创建聊天室成功！聊天室客服[id:{}, name:{}]", CommonUtil.formatDate(new Date()), cs.getId(), cs.getName());
    }

    @Override
    public int compareTo(ChatRoom o) {
        return userList.size() - o.getUserList().size();
    }

    public boolean containsUser(User user) {
        return userList.contains(user);
    }

    public List<User> getUserList() {
        return userList;
    }

    public User getCs() {
        return cs;
    }

    public boolean addChatUser(User user) {
        if(roomSize == userList.size() || containsUser(user)) {
            return false;
        }

        userList.add(user);

        Date addDate = new Date();

        userAddChatRoomTime.put(user, addDate);

        log.info("{} ---- 用户[id:{}, name:{}]加入到聊天室客服[id:{}, name:{}]中", CommonUtil.formatDate(addDate),
                user.getId(), user.getName(), cs.getId(), cs.getName());

        return true;
    }

    public boolean hasPosition() {
        return roomSize > userList.size();
    }

    public void destroy() {
        log.info("{} ---- 销毁聊天室成功！聊天室客服[id:{}, name:{}]", CommonUtil.formatDate(new Date()), cs.getId(), cs.getName());
        for (User user : userList) {
            removeUser(user);
        }
        cs = null;
    }

    public void removeUser(User user) {
        userList.remove(user);
        userAddChatRoomTime.remove(user);
        log.info("{} ---- 从聊天室客服[id:{}, name:{}]移除用户[id:{}, name:{}]成功！", CommonUtil.formatDate(new Date()), cs.getId(), cs.getName(), user.getId(), user.getName());
    }

    public int getIdleChatCount() {
        return roomSize - userList.size();
    }

    public Date getUserAddChatRoomTime(User user) {
        return userAddChatRoomTime.get(user);
    }

}
