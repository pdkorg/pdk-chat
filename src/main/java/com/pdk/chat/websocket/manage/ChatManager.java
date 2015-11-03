package com.pdk.chat.websocket.manage;

import com.pdk.chat.dto.User;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.model.ChatMsg;
import com.pdk.chat.model.UnReadMsg;
import com.pdk.chat.service.ChatMsgService;
import com.pdk.chat.service.UnReadMsgService;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.ChatMessageObjectPool;
import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.util.MessageTypeConstant;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.websocket.msg.ChatMessageSession;
import com.pdk.chat.websocket.msg.MockChatMessageSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by hubo on 2015/8/20
 */
@Service
public class ChatManager {

    private final Map<User, ChatMessageSession> csMap;

    private final Map<User, ChatMessageSession> userMap;

    private Map<String, User> idNameMap;

    private final Set<User> noChatUserList;

    private final ChatScheduler scheduler;

    private final BlockingQueue<UnReadMsg> noReadMsgPipeline;

    private final BlockingQueue<ChatMsg> chatMsgPipeline;

    private final BlockingQueue<DealWithNoChatUserAndUnReadMsgThread> dealWithNoChatUserAndUnReadMsgEventBlockingQueue;

    private ExecutorService exec = Executors.newCachedThreadPool();

    @Autowired
    private UnReadMsgService unReadMsgService;

    @Autowired
    private ChatMsgService chatMsgService;

    public ChatManager() {

        csMap = new ConcurrentHashMap<>();
        userMap = new ConcurrentHashMap<>();
        noChatUserList = Collections.synchronizedSet(new HashSet<User>());

        idNameMap = new ConcurrentHashMap<>();

        scheduler = new ChatScheduler();

        noReadMsgPipeline = new LinkedBlockingQueue<>(2000);
        chatMsgPipeline = new LinkedBlockingQueue<>(2000);
        dealWithNoChatUserAndUnReadMsgEventBlockingQueue = new LinkedBlockingQueue<>(30);

        exec.execute(new PersistentUnReadMsgWorker());
        exec.execute(new PersistentUnReadMsgWorker());

        exec.execute(new PersistentChatMsgWorker());
        exec.execute(new PersistentChatMsgWorker());
        exec.execute(new PersistentChatMsgWorker());
        exec.execute(new PersistentChatMsgWorker());

        exec.execute(new DealWithNoChatUserAndUnReadMsgWorker());

        scheduler.addListener(new ChatSchedulerListener() {
            @Override
            public void afterAddUserToChatRoom(User user, ChatRoom room) {
                User cs = room.getCs();
                updateUserList(cs, user);
                if(user.getLocationInfo() != null) {
                    updateUserLocation(cs, user);
                }
            }

            @Override
            public void afterOfflineUser(User user, ChatRoom room) {
                ChatMessage message = ChatMessageObjectPool.borrow();
                message.setFromId(user.getId());
                message.setFromName(user.getName());
                message.setSendToId(room.getCs().getId());
                message.setSendToName(room.getCs().getName());
                message.setCreateTime(new Date());
                message.setMsgType(MessageTypeConstant.TYPE_USER_NOT_ONLINE);
                sendMsgToCS(room.getCs(), message, false);
            }

            @Override
            public void afterOnlineUser(User user, ChatRoom room) {
                User cs = room.getCs();
                updateUserList(cs, user);
                if(user.getLocationInfo() != null) {
                    updateUserLocation(cs, user);
                }
            }
        });
    }

    public List<User> getOnlineUserList() {
        return new ArrayList<>(userMap.keySet());
    }

    public List<User> getOnlineCSList() {
        return new ArrayList<>(csMap.keySet());
    }

    public User getCSOrUser(String id) {
        return idNameMap.get(id);
    }

    /**
     * 添加一个客服，并且创建一个新的聊天室。
     * @param cs 新的客服
     * @param chatMessageSession 客服的会话session
     * @return 是否添加成功
     * @throws BusinessException
     */
    public boolean addCS(User cs, ChatMessageSession chatMessageSession) throws BusinessException {

        if(cs == null) {
            return false;
        }

        if(chatMessageSession == null) {
            throw new IllegalArgumentException("chatMessageSession is null!");
        }

        if(!csMap.containsKey(cs)) {
            synchronized (this) {
                if(!csMap.containsKey(cs)) {
                    csMap.put(cs, chatMessageSession);
                    idNameMap.put(cs.getId(), cs);
                    createChatRoom(cs);
                }
            }
            return true;
        }

        return false;
    }

    public ChatMessageSession getCSMessageSession(User key) {
        return csMap.get(key);
    }

    /**
     * 移除一个客服，并且销毁一个聊天室
     * @param cs 要移除的客服
     */
    public void removeCS(User cs) {
        if(cs == null) {
            return;
        }
        if(csMap.containsKey(cs)) {
            synchronized (this) {
                if(csMap.containsKey(cs)) {
                    csMap.remove(cs).destroy();
                    idNameMap.remove(cs.getId());
                    destroyChatRoom(cs);
                }
            }
        }
    }

    private void destroyChatRoom(User cs) {
        scheduler.unRegisterCS(cs);
    }

    /**
     * 添加一个用户
     * @param user 用户对象
     * @param chatMessageSession 用户会话信息
     * @throws BusinessException
     */
    public boolean addUser(User user, ChatMessageSession chatMessageSession) throws BusinessException {
        if(user == null) {
            return false;
        }

        if(chatMessageSession == null) {
            throw new IllegalArgumentException("chatMessageSession is null!");
        }

        if(!userMap.containsKey(user)) {
            synchronized (this) {
                if(!userMap.containsKey(user)) {

                    userMap.put(user, chatMessageSession);

                    idNameMap.put(user.getId(), user);

                    synchronized (scheduler) {
                        if(scheduler.isChating(user)) {
                            if(scheduler.isOffline(user)) {
                                scheduler.onlineUser(user);
                            }
                        } else {
                            if (!scheduler.addUserToChatRoom(user)) {
                                dealWithNoCS(user, false);
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 移除一个用户
     * @param user 用户
     * @throws BusinessException
     */
    public synchronized void removeUser(User user) throws BusinessException {
        if(user == null) {
            return;
        }
        synchronized (this) {
            noChatUserList.remove(user);
            userMap.remove(user).destroy();
            idNameMap.remove(user.getId());
            scheduler.offlineUser(user);
        }
    }

    public void removeUserFromChatRoom(User user) {
        if(user == null) {
            return;
        }
        synchronized (this) {
            boolean hasPosition = scheduler.findChatRoomByUser(user).hasPosition();
            scheduler.removeUserFromChatRoom(user);
            if(!hasPosition) {
                notifyHasIdleChatRoom();
            }
        }
    }

    public boolean addUserToDesignatedChatRoom(User cs, User user) {
        return cs!= null && user != null && scheduler.addUserToDesignatedChatRoom(cs, user);
    }

    public ChatRoom findChatRoomByCS(User cs) {
        return scheduler.findChatRoomByCS(cs);
    }

    private void updateUserList(User cs, User user){
        ChatMessage msg = ChatMessageObjectPool.borrow();
        msg.setFromId(user.getId());
        msg.setFromName(user.getName());
        msg.setFromHeadImg(user.getHeaderImgPath());
        msg.setSendToId(cs.getId());
        msg.setSendToName(cs.getName());
        msg.setSendHeadImg(cs.getHeaderImgPath());
        msg.setMsgType(MessageTypeConstant.TYPE_UPDATE_USER_LIST);
        msg.setCreateTime(new Date());
        msg.setContent(user.getUserType());
        sendMsgToCS(cs, msg, false);
    }

    private void updateUserLocation(User cs, User user){
        ChatMessage msg = ChatMessageObjectPool.borrow();
        msg.setFromId(user.getId());
        msg.setFromName(user.getName());
        msg.setFromHeadImg(user.getHeaderImgPath());
        msg.setSendToId(cs.getId());
        msg.setSendToName(cs.getName());
        msg.setSendHeadImg(cs.getHeaderImgPath());
        msg.setMsgType(MessageTypeConstant.TYPE_LOCATION);
        msg.setCreateTime(new Date());
        msg.setContent(user.getLocationInfo());
        sendMsgToCS(cs, msg, false);
    }

    public List<User> getUserList(User cs) {
        return new ArrayList<>(scheduler.findChatRoomByCS(cs).getUserList());
    }

    public void sendMsgToUser(User user, final ChatMessage message, boolean isDB) {
        try {
            userMap.get(user).sendMessage(message);
        } catch (BusinessException e) {
            ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
        }
        if(isDB) {
            pushChatMessageToPersistence(message);
        } else {
            ChatMessageObjectPool.returnObj(message);
        }
    }

    public synchronized void sendMsgToUser(ChatMessage chatMessage){
        User destUser = idNameMap.get(chatMessage.getSendToId());
        chatMessage.setSendToName(destUser.getName());
        chatMessage.setSendHeadImg(destUser.getHeaderImgPath());
        sendMsgToUser(destUser, chatMessage, true);
    }

    public void sendMsgToCS(User cs, ChatMessage message, boolean isDB) {
        try {
            csMap.get(cs).sendMessage(message);
        } catch (Exception e) {
            ChatLoggerUtil.getChatLogger().error(e.getMessage(), e);
            if(isDB) {
                recordUnReadMsg(message);
            }
        }
        if(isDB) {
            pushChatMessageToPersistence(message);
        } else {
            ChatMessageObjectPool.returnObj(message);
        }
    }

    private void pushChatMessageToPersistence(final ChatMessage message) {

        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatMsg chatMsg = trans(message);
                    chatMsgPipeline.put(chatMsg);
                } catch (InterruptedException e) {
                    ChatLoggerUtil.getInfoLogger().error("放入ChatMessageToPersistence线程中断退出 ", e);
                } finally {
                    ChatMessageObjectPool.returnObj(message);
                }
            }
        });

    }

    public synchronized Date getUserAddChatRoomTime(User user) {
        ChatRoom r = scheduler.findChatRoomByUser(user);
        return r.getUserAddChatRoomTime(user);
    }

    public synchronized void sendMsgToCS(ChatMessage message){

        User user = idNameMap.get(message.getFromId());
        message.setFromName(user.getName());
        message.setFromHeadImg(user.getHeaderImgPath());

        if(!isPersistentMsgType(message)) {
            sendNotPersitentMsgToCS(message);
        } else {
            ChatRoom room = scheduler.findChatRoomByUser(user);

            if(room != null) {
                message.setSendToId(room.getCs().getId());
                message.setSendToName(room.getCs().getName());
                message.setSendHeadImg(room.getCs().getHeaderImgPath());
                sendMsgToCS(room.getCs(), message, true);
            } else {
                dealWithNoCS(user, message.getMsgType() != MessageTypeConstant.TYPE_SEND_CLIENT_ERROR);
                recordUnReadMsg(message);
                ChatMessageObjectPool.returnObj(message);
            }
        }
    }

    private void sendNotPersitentMsgToCS(ChatMessage message) {

        User user = idNameMap.get(message.getFromId());

        synchronized (scheduler) {
            if(scheduler.isChating(user)) {
                ChatRoom room = scheduler.findChatRoomByUser(user);
                message.setSendToId(room.getCs().getId());
                message.setSendToName(room.getCs().getName());
                message.setSendHeadImg(room.getCs().getHeaderImgPath());
                sendMsgToCS(room.getCs(), message, false);
            } else {
                ChatMessageObjectPool.returnObj(message);
            }
        }
    }

    public boolean isPersistentMsgType(ChatMessage message) {
        return message.getMsgType() == MessageTypeConstant.TYPE_TEXT ||
                message.getMsgType() == MessageTypeConstant.TYPE_IMAGE ||
                message.getMsgType() == MessageTypeConstant.TYPE_VOICE;
    }

    public void recordUnReadMsg(ChatMessage message) {
        UnReadMsg unReadMsg = new UnReadMsg();
        unReadMsg.setUserId(message.getFromId());
        unReadMsg.setUserName(message.getFromName());
        unReadMsg.setUserHeadImg(message.getFromHeadImg());
        unReadMsg.setContent(message.getContent());
        unReadMsg.setCreateTime(message.getCreateTime());
        unReadMsg.setMsgType((short) message.getMsgType());
        unReadMsg.setSource((short) message.getSource());
        noReadMsgPipeline.add(unReadMsg);
        ChatLoggerUtil.getChatLogger().info("{} ---- 已记录用户[id:{}, name:{}]未读聊天信息：{}", CommonUtil.formatDate(unReadMsg.getCreateTime()),
                unReadMsg.getUserId(), unReadMsg.getUserName(), unReadMsg.getContent());
    }

    private void dealWithNoCS(User user, boolean needSend) {
        synchronized (this) {
            noChatUserList.add(user);
        }
        if(needSend) {
            int hours =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            // 22点以后，9点之前，不发消息了！
            if (hours >= 9 && hours < 22) {
                ChatMessage msg = ChatMessageObjectPool.borrow();
                msg.setFromId("SYSTEM000000000000000001");
                msg.setFromName("系统提示");
                msg.setContent("客服正在忙！请您稍等~~~");
                msg.setSendToId(user.getId());
                msg.setSendToName(user.getName());
                msg.setCreateTime(new Date());
                sendMsgToUser(user, msg, false);
            }
        }
    }

    public void createChatRoom(User cs) throws BusinessException {
        scheduler.registerCS(cs);
        notifyHasIdleChatRoom();
    }


    @PreDestroy
    public void destroy() {

        scheduler.destroy();
        
        noChatUserList.clear();
        
        if(csMap.size() > 0) {
            for (ChatMessageSession session : csMap.values()) {
                session.close();
                session.destroy();
            }

            csMap.clear();
        }

        if(userMap.size() > 0) {
            for (ChatMessageSession session : userMap.values()) {
                session.close();
                session.destroy();
            }

            userMap.clear();
        }

        if(idNameMap.size() > 0) {
            idNameMap.clear();
        }
        
        /*
         * 关闭持久化线程
         */
        exec.shutdownNow();

        ChatMessageObjectPool.close();

    }


    class PersistentUnReadMsgWorker implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {

                    List<UnReadMsg> saveList = new ArrayList<>();

                    synchronized (noReadMsgPipeline) {

                        while (noReadMsgPipeline.size() > 0) {
                            saveList.add(noReadMsgPipeline.take());
                        }

                        if(saveList.size() > 0) {
                            unReadMsgService.save(saveList);
                            saveList.clear();
                        }

                    }

                    TimeUnit.SECONDS.sleep(1);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                } catch (Exception e) {
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    class PersistentChatMsgWorker implements Runnable {
        @Override
        public void run() {

            while (!Thread.interrupted()) {

                try {

                    List<ChatMsg> saveList = new ArrayList<>();

                    synchronized (chatMsgPipeline) {
                        while (chatMsgPipeline.size() > 0) {
                            ChatMsg msg = chatMsgPipeline.take();
                            saveList.add(msg);
                        }
                    }

                    if(saveList.size() > 0) {
                        chatMsgService.save(saveList);
                        saveList.clear();
                    }

                    TimeUnit.SECONDS.sleep(1);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                } catch (Exception e) {
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    class DealWithNoChatUserAndUnReadMsgWorker implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    DealWithNoChatUserAndUnReadMsgThread thread = dealWithNoChatUserAndUnReadMsgEventBlockingQueue.take();
                    thread.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    private Map<User, List<UnReadMsg>> getUnReadMsgMap(List<User> userList) {
        Map<User, List<UnReadMsg>> result = new HashMap<>();
        try {
            List<UnReadMsg> unReadMsgList = unReadMsgService.queryUnReadMsg(userList);
            for (UnReadMsg unReadMsg : unReadMsgList) {
                User u = new User(unReadMsg.getUserId(), unReadMsg.getUserName());
                if(unReadMsg.getSource() == ChatMessage.SOURCE_WX) {
                    u.setUserType("微信");
                } else if(unReadMsg.getSource() == ChatMessage.SOURCE_APP) {
                    u.setUserType("APP");
                } else {
                    u.setUserType("未知类型");
                }
                if(!result.containsKey(u)) {
                    result.put(u, new ArrayList<UnReadMsg>());
                }
                result.get(u).add(unReadMsg);
            }
        } catch (Exception e) {
            ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
        }

        return result;
    }

    private Map<User, List<UnReadMsg>> getUnReadMsgMap(Set<User> userList) {
        List<User> queryUserList = new ArrayList<>();
        queryUserList.addAll(userList);
        return getUnReadMsgMap(queryUserList);
    }


    public void notifyHasIdleChatRoom() {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dealWithNoChatUserAndUnReadMsgEventBlockingQueue.put(new DealWithNoChatUserAndUnReadMsgThread());
                } catch (InterruptedException e) {
                    ChatLoggerUtil.getInfoLogger().error("dealWithNoChatUserAndUnReadMsgEventBlockingQueue 放入线程中断退出！ ", e);
                }
            }
        });
    }

    private void sendUnReadMsg(User user, List<UnReadMsg> unReadMsgList){
        ChatRoom room = scheduler.findChatRoomByUser(user);
        for (UnReadMsg unReadMsg : unReadMsgList) {
            ChatMessage msg = ChatMessageObjectPool.borrow();
            msg.setFromId(unReadMsg.getUserId());
            msg.setFromName(unReadMsg.getUserName());
            msg.setFromHeadImg(unReadMsg.getUserHeadImg());
            msg.setSendToId(room.getCs().getId());
            msg.setSendToName(room.getCs().getName());
            msg.setSendHeadImg(room.getCs().getHeaderImgPath());
            msg.setContent(unReadMsg.getContent());
            msg.setCreateTime(new Date());
            msg.setMsgType(unReadMsg.getMsgType());
            msg.setSource(unReadMsg.getSource());
            sendMsgToCS(room.getCs(), msg, true);
        }
    }

    private void addOfflineUserUnReadMsgToCS() throws BusinessException {

        int idleCount = 0;

        synchronized (ChatManager.this) {
            if (scheduler.getIdleChatCount() > 0) {
                idleCount = scheduler.getIdleChatCount();
            }
        }

        if(idleCount > 0) {

            Set<User> addUsers = new HashSet<>();

            List<UnReadMsg> deleteList = new ArrayList<>();

            List<User> unReadUsers = unReadMsgService.queryUnReadUserList();

            int size = Math.min(idleCount, unReadUsers.size());

            for (int i = 0; i < size; i++) {
                addUsers.add(unReadUsers.get(i));
            }

            Map<User, List<UnReadMsg>> unUserListMap = getUnReadMsgMap(addUsers);

            synchronized (ChatManager.this) {
                for (User u : unUserListMap.keySet()) {
                    if (addUser(u, new MockChatMessageSession())) {
                        addUsers.add(u);
                    }
                }

                for (User user : addUsers) {
                    sendUnReadMsg(user, unUserListMap.get(user));
                    removeUser(user);
                    deleteList.addAll(unUserListMap.remove(user));
                }
            }

            if(deleteList.size() > 0) {
                unReadMsgService.delete(deleteList);
            }
        }
    }

    private void addNoChatUserToChatRoom() throws BusinessException {

        Set<User> addUsers = new HashSet<>();

        Set<String> userIdSet = new HashSet<>();

        Map<User, List<UnReadMsg>> unReUserListMap = new HashMap<>();

        synchronized(ChatManager.this) {
            if (noChatUserList.size() > 0) {
                Iterator<User> noChatUserItr = noChatUserList.iterator();
                synchronized (noReadMsgPipeline) {
                    while (noChatUserItr.hasNext()) {
                        User user = noChatUserItr.next();
                        if (scheduler.addUserToChatRoom(user)) {
                            noChatUserItr.remove();
                            addUsers.add(user);
                            userIdSet.add(user.getId());
                            unReUserListMap.put(user, new ArrayList<UnReadMsg>());
                        }
                    }
                    Iterator<UnReadMsg> itr = noReadMsgPipeline.iterator();
                    while (itr.hasNext()) {
                        UnReadMsg unReadMsg = itr.next();
                        if(userIdSet.contains(unReadMsg.getUserId())) {
                            itr.remove();
                            unReUserListMap.get(new User(unReadMsg.getUserId())).add(unReadMsg);
                        }
                    }
                }
            }
        }
        
        Map<User, List<UnReadMsg>> addUserUnReadMsgMap = getUnReadMsgMap(addUsers);

        List<UnReadMsg> deleteList = new ArrayList<>();

        if (addUserUnReadMsgMap.size() > 0) {

            for (Map.Entry<User, List<UnReadMsg>> entry : addUserUnReadMsgMap.entrySet()) {

                User user = entry.getKey();

                deleteList.addAll(entry.getValue());

                synchronized (ChatManager.this) {

                    List<UnReadMsg> unReadMsgList = new ArrayList<>();

                    unReadMsgList.addAll(addUserUnReadMsgMap.get(user));

                    if(unReUserListMap.containsKey(user) && unReUserListMap.get(user).size() > 0) {
                        unReadMsgList.addAll(unReUserListMap.get(user));
                    }

                    sendUnReadMsg(user, unReadMsgList);

                }

            }

            if(deleteList.size() > 0) {
                unReadMsgService.delete(deleteList);
            }
        }
    }

    class DealWithNoChatUserAndUnReadMsgThread implements Runnable {
        @Override
        public void run() {
            try {
                addNoChatUserToChatRoom();
                addOfflineUserUnReadMsgToCS();
            } catch (Exception e) {
                ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
            }
        }
    }

    private ChatMsg trans(ChatMessage msg) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setFromId(msg.getFromId());
        chatMsg.setFromName(msg.getFromName());
        chatMsg.setFromHeadImg(msg.getFromHeadImg());
        chatMsg.setSendToId(msg.getSendToId());
        chatMsg.setSendToName(msg.getSendToName());
        chatMsg.setSendToHeadImg(msg.getSendHeadImg());
        chatMsg.setMsgType((short) msg.getMsgType());
        chatMsg.setCreateTime(msg.getCreateTime());
        chatMsg.setContent(msg.getContent());
        chatMsg.setSource((short)msg.getSource());
        return chatMsg;
    }

    public boolean isChating(User u) {
        return scheduler.isChating(u);
    }

}
