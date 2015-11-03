package com.pdk.chat.dao;

import com.pdk.chat.model.ChatMsg;
import com.pdk.chat.util.DateRange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hubo on 15/9/26
 */
@Repository
public class ChatMsgDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(List<ChatMsg> datas) {
        mongoTemplate.insert(datas, ChatMsg.class);
    }

    public List<ChatMsg> queryUserChatMsgPage(String userId, Date timeThreshold, int pageNum, int pageSize) {
        int startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("fromId").is(userId), Criteria.where("sendToId").is(userId));
        criteria.andOperator(Criteria.where("createTime").lt(timeThreshold).and("dr").is((short) 0));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.addCriteria(criteria).skip(startRow).limit(pageSize);
        return mongoTemplate.find(query, ChatMsg.class);
    }


    public List<ChatMsg> queryUserChatMsgPage(String userId, Date startDate, Date endDate, String content, int start, int length) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("fromId").is(userId), Criteria.where("sendToId").is(userId));

        List<Criteria> andOperatorList = new ArrayList<>();

        andOperatorList.add(Criteria.where("createTime").lt(endDate));
        andOperatorList.add(Criteria.where("dr").is((short) 0));

        if(startDate != null) {
            andOperatorList.add(Criteria.where("createTime").gte(startDate));
        }

        if(StringUtils.isNotEmpty(content)) {
            Pattern pattern = Pattern.compile("^.*" + content+ ".*$", Pattern.CASE_INSENSITIVE);
            andOperatorList.add(Criteria.where("content").regex(pattern));
        }

        criteria.andOperator(andOperatorList.toArray(new Criteria[andOperatorList.size()]));

        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.addCriteria(criteria).skip(start).limit(length);

        return mongoTemplate.find(query, ChatMsg.class);
    }

    public long queryUserChatMsgCount(String userId, Date startDate, Date endDate, String content) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("fromId").is(userId), Criteria.where("sendToId").is(userId));

        List<Criteria> andOperatorList = new ArrayList<>();

        andOperatorList.add(Criteria.where("createTime").lt(endDate));
        andOperatorList.add(Criteria.where("dr").is((short) 0));

        if(startDate != null) {
            andOperatorList.add(Criteria.where("createTime").gte(startDate));
        }

        if(StringUtils.isNotEmpty(content)) {
            Pattern pattern = Pattern.compile("^.*" + content+ ".*$", Pattern.CASE_INSENSITIVE);
            andOperatorList.add(Criteria.where("content").regex(pattern));
        }

        criteria.andOperator(andOperatorList.toArray(new Criteria[andOperatorList.size()]));
        query.addCriteria(criteria);

        return mongoTemplate.count(query, ChatMsg.class);
    }

    public List<ChatMsg> queryUserByCsId(String csId, DateRange dateRange){

        List<ChatMsg> result = new ArrayList<>();

        Criteria criteria = new Criteria();

        List<Criteria> andOperatorList = new ArrayList<>();
        andOperatorList.add(Criteria.where("sendToId").is(csId));
        andOperatorList.add(Criteria.where("createTime").gte(dateRange.getStart()));
        andOperatorList.add(Criteria.where("createTime").lte(dateRange.getEnd()));

        criteria.andOperator(andOperatorList.toArray(new Criteria[andOperatorList.size()]));

        GroupBy groupBy = GroupBy.key("fromId").initialDocument("{}")
                .reduceFunction("function(curr, result){}");

        GroupByResults<ChatMsg> results = mongoTemplate.group(criteria, "pdk_chat_chatmsg", groupBy, ChatMsg.class);

        for (ChatMsg msg : results) {
            result.add(msg);
        }

        return result;

    }
}
