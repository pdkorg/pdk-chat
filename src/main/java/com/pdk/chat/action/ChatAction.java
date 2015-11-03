package com.pdk.chat.action;

import com.pdk.chat.dto.ChatMsgJson;
import com.pdk.chat.dto.ChatMsgQueryArgWrapper;
import com.pdk.chat.dto.User;
import com.pdk.chat.dto.UserJson;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.model.ChatMsg;
import com.pdk.chat.service.ChatMsgService;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.util.WiXinMessageUtil;
import com.pdk.chat.websocket.manage.ChatManager;
import com.pdk.chat.websocket.msg.ChatMessageSession;
import com.pdk.chat.wx.session.WeixinSessionManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by hubo on 2015/8/21
 */
@Controller
@RequestMapping("/")
public class ChatAction {

    private static Logger log = ChatLoggerUtil.getChatLogger();

    @Autowired
    private ChatManager chatManager;

    @Autowired
    private ChatMsgService chatMsgService;

    @RequestMapping(value = "/{csId}/users", method = RequestMethod.GET)
    @ResponseBody
    public List<User> getUserList(@PathVariable String csId) {

        List<User> userList = new ArrayList<>();

        User cs = new User();
        cs.setId(csId);

        userList.addAll(chatManager.getUserList(cs));

        return userList;

    }

    @RequestMapping(value = "/{csId}/close/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> closeUserFromChatRoom(@PathVariable String csId, @PathVariable String userId) {

        Map<String, Object> result = new HashMap<>();

        User cs = new User();
        cs.setId(csId);

        User user = new User();
        user.setId(userId);

        chatManager.removeUserFromChatRoom(user);

        result.put("result", "success");

        return result;

    }


    @RequestMapping("/{sourceCSId}/change/{userId}/to/{destCSId}")
    @ResponseBody
    public Map<String, Object> repeatDispatchUser(@PathVariable("sourceCSId")String sourceCSId, @PathVariable("userId")String destUserId, @PathVariable("destCSId")String destCSId) {

        Map<String, Object> result = new HashMap<>();

        User sourceCS = chatManager.getCSOrUser(sourceCSId);

        User distUser = chatManager.getCSOrUser(destUserId);

        User distCS = chatManager.getCSOrUser(destCSId);

        synchronized (chatManager) {

            chatManager.removeUserFromChatRoom(distUser);

            boolean addResult = chatManager.addUserToDesignatedChatRoom(distCS, distUser);

            if(addResult) {
                result.put("result", "success");
            } else {
                result.put("result", "error");
                result.put("errorMsg", "房间人数已满");
            }

        }

        return result;
    }

    @RequestMapping("/online/cs")
    @ResponseBody
    public List<User> getOnlineCSList() {

        List<User> result = new ArrayList<>();

        List<User> onlineCSList = chatManager.getOnlineCSList();

        for (User cs : onlineCSList) {
            if(chatManager.findChatRoomByCS(cs).hasPosition()) {
                result.add(cs);
            }
        }

        return result;
    }

    @RequestMapping("/chatmsg/{userId}")
    @ResponseBody
    public List<ChatMsg> queryUserChatMsg(@PathVariable("userId") String userId, int pageNum) {

        Date timeBase = chatManager.getUserAddChatRoomTime(chatManager.getCSOrUser(userId));

        List<ChatMsg> result = chatMsgService.queryUserChatMsgPage(userId, CommonUtil.toDate(CommonUtil.formatDate(timeBase)), pageNum, 10);

        Collections.sort(result, new Comparator<ChatMsg>() {
            @Override
            public int compare(ChatMsg o1, ChatMsg o2) {
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        });

        for (ChatMsg chatMsg : result) {
            if(chatMsg.getFromId().equals(userId)) {
                chatMsg.setContent(WiXinMessageUtil.transWx2Msg(CommonUtil.getResourcePath(), chatMsg.getContent()));
            }
        }

        return result;
    }

    @RequestMapping(value = "/chatmsg/record", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> queryUserChatMsg(ChatMsgQueryArgWrapper argWrapper) {

        Map<String, Object> result = new HashMap<>();

        if(argWrapper.getUserId() == null) {
            result.put("draw", argWrapper.getDraw());
            result.put("recordsTotal", 0);
            result.put("recordsFiltered", 0);
            result.put("data", new ArrayList<ChatMsgJson>());
            return result;
        }

        if(argWrapper.getEndDateStr() == null) {
            argWrapper.setEndDateStr("2050-01-01 00:00:00");
        }

        Date endDate = CommonUtil.toDate(argWrapper.getEndDateStr());

        Date startDate = null;

        if(argWrapper.getStartDateStr() != null) {
            startDate = CommonUtil.toDate(argWrapper.getStartDateStr());
        }

        long total = chatMsgService.queryUserChatMsgCount(startDate, endDate, argWrapper);

        List<ChatMsg> chatMsgList = chatMsgService.queryUserChatMsgPage(startDate, endDate, argWrapper);

        List<ChatMsgJson> dataList = new ArrayList<>();

        int index = argWrapper.getStart() + 1;

        for (ChatMsg chatMsg : chatMsgList) {

            String csId;
            String csName;
            String userId;
            String userName;

            if(argWrapper.getUserId().equals(chatMsg.getFromId())) {
                userId = chatMsg.getFromId();
                userName = chatMsg.getFromName();
                csId = chatMsg.getSendToId();
                csName = chatMsg.getSendToName();
            }else {
                userId = chatMsg.getSendToId();
                userName = chatMsg.getSendToName();
                csId = chatMsg.getFromId();
                csName = chatMsg.getFromName();
            }

            dataList.add(new ChatMsgJson(index, csId, csName, userId, userName, chatMsg));

            index++;
        }

        result.put("draw", argWrapper.getDraw());
        result.put("recordsTotal", total);
        result.put("recordsFiltered", total);
        result.put("data", dataList);

        return result;
    }

    @RequestMapping("/chat/rm/cs/{csId}")
    @ResponseBody
    public Map<String, Object> removeCS(@PathVariable("csId") String csId) {
        Map<String, Object> result = new HashMap<>();
        User cs = new User(csId);
        ChatMessageSession session = chatManager.getCSMessageSession(cs);
        if(session.isOpen()) {
            session.close();
        }else {
            chatManager.removeCS(cs);
        }
        result.put("result", "success");
        return result;
    }

    @RequestMapping("/chat/rm/u/{sourceId}")
    @ResponseBody
    public Map<String, Object> removeWxUser(@PathVariable("sourceId") String sourceId) {
        Map<String, Object> result = new HashMap<>();
        try {
            WeixinSessionManager.getInstance().removeWeixinMessageSession(sourceId);
        } catch (BusinessException e) {
            result.put("result", "error");
            result.put("errorMsg", e.getMessage());
        }
        result.put("result", "success");
        return result;
    }

    @RequestMapping("/chat/today/u")
    @ResponseBody
    public Map<String, Object> queryUserListByCSFormToday(String csId) {

        Map<String, Object> result = new HashMap<>();

        List<UserJson> resultData = new ArrayList<>();

        List<User> userInSession = chatManager.getOnlineUserList();

        Map<String, User> userMapInSession = new HashMap<>();

        for (User user : userInSession) {
            userMapInSession.put(user.getId(), user);
        }

        List<User> userInDB = chatMsgService.queryCsChatUserListToday(csId);

        for (User user : userInDB) {
            if(userMapInSession.containsKey(user.getId()) && !chatManager.isChating(user)) {
                User u = userMapInSession.get(user.getId());
                resultData.add(new UserJson(u.getId(), u.getSourceId(), u.getName(), u.getHeaderImgPath()));
            }
        }

        result.put("result", "success");
        result.put("data", resultData);

        return result;
    }


    @RequestMapping(value = "/chat/today/u",  method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addUserToChatRoomFormToday(String userId, String csId) throws BusinessException {

        Map<String, Object> result = new HashMap<>();

        User user = chatManager.getCSOrUser(userId);

        User cs = chatManager.getCSOrUser(csId);

        synchronized (chatManager) {

            List<User> userInSessions = chatManager.getOnlineUserList();

            if(!userInSessions.contains(user)){
                result.put("result", "error");
                result.put("errorMsg", "用户已下线，不能加入！");
            }else if(chatManager.isChating(user))  {
                result.put("result", "error");
                result.put("errorMsg", "用户已经在客服聊天服务中，不能重复加入！");
            } else {
                boolean addResult = chatManager.addUserToDesignatedChatRoom(cs, user);

                if(addResult) {
                    result.put("result", "success");
                } else {
                    result.put("result", "error");
                    result.put("errorMsg", "房间人数已满，不能加入！");
                }

                result.put("result", "success");
            }

        }
        return result;
    }

}
