package com.pdk.chat.wx.token;

import com.pdk.chat.dto.User;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.ChatMessageObjectPool;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.dto.WebReturnInfo;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.service.WeixinMessageService;
import com.pdk.chat.wx.session.WeixinSessionManager;
import com.pdk.chat.wx.util.GetAccessToken;
import com.pdk.chat.wx.util.SignUtil;
import com.pdk.chat.wx.util.WeixinParseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/token")
public class TokenController {

	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected WeixinMessageService weixinMessageService;
	@Autowired
	GetAccessToken getAccessToken;

	@RequestMapping(value = "weixinToken", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String weixinToken(@RequestParam(value = "echostr", required = false)String echostr,
                              @RequestParam(value = "signature", required = false)String signature,
                              @RequestParam(value = "timestamp", required = false)String timestamp,
                              @RequestParam(value = "nonce", required = false)String nonce) {

        ChatLoggerUtil.getWxLogger().info("收到微信请求参数[echostr:{}]", echostr);
        ChatLoggerUtil.getWxLogger().info("收到微信请求参数[signature:{}]", signature);
        ChatLoggerUtil.getWxLogger().info("收到微信请求参数[timestamp:{}]", timestamp);
        ChatLoggerUtil.getWxLogger().info("收到微信请求参数[nonce:{}]", nonce);

		if(!StringUtils.isEmpty(echostr)) {
            ChatLoggerUtil.getWxLogger().info("返回给微信echostr：{}", echostr);
			return echostr;
		}

		if(!isWeixinTokenCheck(signature, timestamp, nonce)) {
			return "";
		}

		return  processRequest();
	}

	private boolean isWeixinTokenCheck(String signature, String timestamp, String nonce){
		return SignUtil.checkSignature(signature, timestamp, nonce);
	}

	private String processRequest() {
		String respMessage = null;
        try {
            WeixinMessageReceive message  = WeixinParseUtil.parseXml(request);
            ChatLoggerUtil.getWxLogger().info("收到微信消息：{}", message);
            respMessage = WeixinSessionManager.getInstance().sendMessageToCS(message);
            ChatLoggerUtil.getWxLogger().info("返回给微信的response：{}", respMessage);
        } catch (Exception e) {
            ChatLoggerUtil.getWxLogger().error(e.getMessage(), e);
        }
		return respMessage;
	}

	@RequestMapping(value = "/notice/{userId}", produces = "application/json; charset=utf-8",method = RequestMethod.POST)
	@ResponseBody
	public WebReturnInfo notice (@PathVariable("userId") String userId,
						@RequestParam(value = "type", required = false)int type,
						@RequestParam(value = "content", required = false)String content) {
		WebReturnInfo webReturnInfo = new WebReturnInfo();
		webReturnInfo.setSuccess(true);
		webReturnInfo.setMsg("OK");
		if(StringUtils.isEmpty(content))
			return webReturnInfo;
		ChatMessage message = ChatMessageObjectPool.borrow();
		message.setContent(content);
		message.setMsgType(type);
		message.setSendToId(userId);
		try {
			WeixinSessionManager manager = WeixinSessionManager.getInstance();
			User user = manager.getUser(userId);

			if(user != null && manager.hasWeixinMessageSession(user.getSourceId())){
				manager.getWeixinMessageSession(user.getSourceId()).sendMessage(message);
			}
		} catch (BusinessException e) {
			webReturnInfo.setSuccess(false);
			webReturnInfo.setMsg(e.getMessage());
		}finally {
			ChatMessageObjectPool.returnObj(message);
		}
		return webReturnInfo;
	}

	@RequestMapping(value = "/reset", produces = "application/json; charset=utf-8")
	@ResponseBody
	public WebReturnInfo resetAccessToken () {
		WebReturnInfo info = new WebReturnInfo();
		try {
			getAccessToken.resetAccessToken();
			info.setSuccess(true);
		} catch (WeixinBusinessException e) {
			info.setSuccess(false);
			info.setMsg(e.getMessage());
		}
		return info;
	}

}
