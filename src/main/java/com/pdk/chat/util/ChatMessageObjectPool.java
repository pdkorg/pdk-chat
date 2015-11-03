package com.pdk.chat.util;

import com.pdk.chat.websocket.msg.ChatMessage;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by hubo on 2015/9/24
 */
public class ChatMessageObjectPool {

    private volatile static GenericObjectPool<ChatMessage> objectPool;

    static {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(4000); //整个池最大值
        config.setMaxIdle(200);
        config.setMinIdle(50);
        config.setNumTestsPerEvictionRun(Integer.MAX_VALUE); // always test all idle objects
        config.setTestOnBorrow(true);
        config.setTimeBetweenEvictionRunsMillis(10 * 60000L); //-1不启动。默认1min一次
        config.setMinEvictableIdleTimeMillis(10 * 60000L); //可发呆的时间,10mins

        objectPool = new GenericObjectPool<>(new BasePooledObjectFactory<ChatMessage>() {
            @Override
            public ChatMessage create() throws Exception {
                return new ChatMessage();
            }

            @Override
            public PooledObject<ChatMessage> wrap(ChatMessage chatMessage) {
                return new DefaultPooledObject<>(chatMessage);
            }
        }, config);

    }

    private ChatMessageObjectPool() {
    }

    public static ChatMessage borrow(){
        try {
            return objectPool.borrowObject();
        } catch (Exception e) {
            return new ChatMessage();
        }
    }


    public static void returnObj(ChatMessage chatMessage) {
        chatMessage.reset();
        objectPool.returnObject(chatMessage);
    }

    public static void close() {
        objectPool.close();
    }

}
