package com.pdk.chat.util;

import com.pdk.chat.dto.User;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hubo on 2015/8/25.
 */
public class WebSocketUtil {

    public static User getUserInfo(WebSocketSession session){
        return (User) session.getAttributes().get("client");
    }

}
