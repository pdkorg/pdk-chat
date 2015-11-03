package com.pdk.chat.service;

import com.pdk.chat.dao.UnReadMsgDao;
import com.pdk.chat.dto.User;
import com.pdk.chat.model.ChatMsg;
import com.pdk.chat.model.UnReadMsg;
import com.pdk.chat.util.DBConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hubo on 2015/8/25
 */
@Service
public class UnReadMsgService{

    @Autowired
    private UnReadMsgDao dao;

    public List<UnReadMsg> queryUnReadMsg(List<User> userList) {

        if(userList == null || userList.size() == 0) {
            return new ArrayList<>();
        }

        List<String> userIdList = new ArrayList<>();

        for (User u : userList) {
            userIdList.add(u.getId());
        }

        return dao.queryUnReadMsg(userIdList);
    }

    public List<User> queryUnReadUserList() {
        List<User> result = new ArrayList<>();

        for (String userId : dao.queryUnReadUserList()) {
            result.add(new User(userId));
        }

        return result;
    }

    public void save(List<UnReadMsg> datas) {
        if(datas.size() == 0){
            return;
        }

        Date tsDate = new Date();
        for (UnReadMsg data : datas) {
            data.setTs(tsDate);
            data.setDr(DBConst.DR_NORMAL);
        }

        dao.insert(datas);
    }

    public void delete(List<UnReadMsg> datas) {

        if(datas.size() == 0) {
            return;
        }

        dao.delete(datas);
    }

}
