package com.pdk.chat.dao;

import com.pdk.chat.model.UnReadMsg;
import com.pdk.chat.util.DBConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UnReadMsgDao{

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(List<UnReadMsg> datas) {
        mongoTemplate.insert(datas, UnReadMsg.class);
    }

    public void delete(List<UnReadMsg> datas) {

        Set<String> idSet = new HashSet<>();

        for (UnReadMsg msg : datas) {
            idSet.add(msg.getId());
        }

        mongoTemplate.remove(new Query().addCriteria(Criteria.where("_id").in(idSet)), UnReadMsg.class);
    }

    public List<String> queryUnReadUserList() {
        Query query = new Query(Criteria.where("dr").is(DBConst.DR_NORMAL));

        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "createTime")));

        List<UnReadMsg> unReadMsgList = mongoTemplate.find(query, UnReadMsg.class);

        List<String> result = new ArrayList<>();

        for (UnReadMsg unReadMsg : unReadMsgList) {
            if(!result.contains(unReadMsg.getUserId())) {
                result.add(unReadMsg.getUserId());
            }
        }

        return result;
    }

    public List<UnReadMsg> queryUnReadMsg(List<String> userIds) {
        Query query = new Query(Criteria.where("userId").in(userIds).and("dr").is(DBConst.DR_NORMAL));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "createTime")));
        return mongoTemplate.find(query, UnReadMsg.class);
    }


}