package com.pdk.chat.interceptor;

import com.pdk.chat.dto.User;
import com.pdk.chat.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by hubo on 2015/8/20
 */
public class ClientIdHandshakeInterceptor extends HttpSessionHandshakeInterceptor{

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        boolean result = super.beforeHandshake(request, response, wsHandler, attributes);
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            String id = serverRequest.getServletRequest().getParameter("id");
            String name = serverRequest.getServletRequest().getParameter("name");
            String code = serverRequest.getServletRequest().getParameter("code");
            String headImg = serverRequest.getServletRequest().getParameter("headImg");
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setHeaderImgPath(headImg);
            if(StringUtils.isNotEmpty(StringUtils.trim(code))) {
                user.setCode(code);
            }
            attributes.put("client", user);
            attributes.put("baseUrl", CommonUtil.getBaseUrl(serverRequest.getServletRequest()));
        }
        return result;
    }
}
