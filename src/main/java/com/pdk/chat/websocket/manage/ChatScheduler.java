package com.pdk.chat.websocket.manage;

import com.pdk.chat.dto.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天调度器
 * Created by hubo on 2015/9/11
 */
public class ChatScheduler {

    private List<ChatRoom> rooms = new LinkedList<>();

    private final Map<User, ChatRoom> userChatRegistry = new ConcurrentHashMap<>();

    private final Map<User, ChatRoom> csChatRegistry = new ConcurrentHashMap<>();

    private List<ChatSchedulerListener> schedulerListeners = new ArrayList<>();

    private final Set<User> offlineUserSet = new HashSet<>();

    public synchronized boolean addUserToChatRoom(User user) {

        boolean result = false;

        if(isChating(user)) {
            return false;
        }

        if(rooms.size() > 0) {
            Collections.sort(rooms);
            ChatRoom room = rooms.get(0);
            if(room.hasPosition()) {
                room.addChatUser(user);
                userChatRegistry.put(user, room);
                for (ChatSchedulerListener listener : schedulerListeners) {
                    listener.afterAddUserToChatRoom(user, room);
                }
                result = true;
            }
        }
        return result;
    }

    public synchronized ChatRoom getMinUserChatRoom() {
        if(rooms.size() > 0) {
            Collections.sort(rooms);
            return rooms.get(0);
        }else {
            return null;
        }
    }

    public synchronized boolean addUserToDesignatedChatRoom(User cs, User user) {

        boolean result = false;
        ChatRoom room = findChatRoomByCS(cs);
        if (room.hasPosition()) {
            room.addChatUser(user);
            userChatRegistry.put(user, room);
            for (ChatSchedulerListener listener : schedulerListeners) {
                listener.afterAddUserToChatRoom(user, room);
            }
            result = true;
        }
        return result;
    }

    public void addListener(ChatSchedulerListener l) {
        schedulerListeners.add(l);
    }

    public void removeListener(ChatSchedulerListener l) {
        schedulerListeners.remove(l);
    }

    public synchronized void removeUserFromChatRoom(User user) {
        ChatRoom room = userChatRegistry.remove(user);
        room.removeUser(user);
        if(offlineUserSet.contains(user)) {
            offlineUserSet.remove(user);
        }
    }

    /**
     * 注册一个聊天室到调度器
     * @param cs
     */
    public synchronized void registerCS(User cs) {
        if(cs == null) {
            throw new NullPointerException();
        }
        ChatRoom r = new ChatRoom(cs);
        rooms.add(r);
        csChatRegistry.put(cs, r);
    }

    /**
     * 注销一个聊天室
     * @param cs
     */
    public synchronized void unRegisterCS(User cs) {
        if(cs == null) {
            throw new NullPointerException();
        }

        ChatRoom r = findChatRoomByCS(cs);
        for (User user : r.getUserList()) {
            userChatRegistry.remove(user);
            if(offlineUserSet.contains(user)) {
                offlineUserSet.remove(user);
            }
        }
        r.destroy();
        rooms.remove(r);
        csChatRegistry.remove(cs);
    }

    public synchronized void destroy() {
        userChatRegistry.clear();
        csChatRegistry.clear();
        offlineUserSet.clear();
        for (ChatRoom room : rooms) {
            room.destroy();
        }
        rooms.clear();
    }

    public ChatRoom findChatRoomByUser(User u) {
        ChatRoom r = userChatRegistry.get(u);
        if(r == null){
            synchronized (this) {
                if(r == null) {
                    if(!addUserToChatRoom(u)) {
                        return null;
                    } else {
                        r = userChatRegistry.get(u);
                    }
                }
            }
        }
        return r;
    }

    public boolean isChating(User u) {
        return userChatRegistry.containsKey(u);
    }

    public ChatRoom findChatRoomByCS(User cs) {
        return csChatRegistry.get(cs);
    }

    public synchronized void offlineUser(User user) {
        if(isChating(user)) {
            offlineUserSet.add(user);
            for (ChatSchedulerListener listener : schedulerListeners) {
                listener.afterOfflineUser(user, userChatRegistry.get(user));
            }
        }
    }

    public synchronized void onlineUser(User user) {
        offlineUserSet.remove(user);
        for (ChatSchedulerListener listener : schedulerListeners) {
            listener.afterOnlineUser(user, userChatRegistry.get(user));
        }
    }

    public synchronized boolean isOffline(User user) {
        return offlineUserSet.contains(user);
    }

    public synchronized int getIdleChatCount() {
        int sum = 0;
        for (ChatRoom room : rooms) {
            sum += room.getIdleChatCount();
        }
        return sum;
    }

}
