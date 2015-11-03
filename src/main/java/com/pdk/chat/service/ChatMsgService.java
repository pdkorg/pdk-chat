package com.pdk.chat.service;

import com.pdk.chat.dao.ChatMsgDao;
import com.pdk.chat.dto.ChatMsgQueryArgWrapper;
import com.pdk.chat.dto.User;
import com.pdk.chat.model.ChatMsg;
import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.util.DBConst;
import com.pdk.chat.util.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hubo on 2015/8/31
 */

@Service
public class ChatMsgService{

    @Autowired
    private ChatMsgDao chatMsgDao;

    public List<ChatMsg> queryUserChatMsgPage(String userId, Date timeThreshold,  int pageNum, int pageSize) {
        return chatMsgDao.queryUserChatMsgPage(userId, timeThreshold, pageNum, pageSize);
    }

    public List<ChatMsg> queryUserChatMsgPage(Date startDate, Date endDate, ChatMsgQueryArgWrapper wrapper) {
        return chatMsgDao.queryUserChatMsgPage(wrapper.getUserId(), startDate, endDate, wrapper.getContent(), wrapper.getStart(), wrapper.getLength());
    }

    public long queryUserChatMsgCount(Date startDate, Date endDate, ChatMsgQueryArgWrapper wrapper) {
        return chatMsgDao.queryUserChatMsgCount(wrapper.getUserId(), startDate, endDate, wrapper.getContent());
    }

    /**
     *
     * @return
     */
    public List<User> queryCsChatUserListToday(String csId) {

        List<User> userList = new ArrayList<>();

        List<ChatMsg> chatMsgList = chatMsgDao.queryUserByCsId(csId, new DateRange(CommonUtil.startOfTodDay(), CommonUtil.endOfTodDay()));

        for (ChatMsg chatMsg : chatMsgList) {
            User u = new User();
            u.setId(chatMsg.getFromId());
            userList.add(u);
        }

        return userList;
    }

    public void save(List<ChatMsg> datas) {

        if(datas.size() == 0){
            return;
        }

        Date tsDate = new Date();
        for (ChatMsg data : datas) {
            data.setTs(tsDate);
            data.setDr(DBConst.DR_NORMAL);
        }

        chatMsgDao.insert(datas);
    }


}
