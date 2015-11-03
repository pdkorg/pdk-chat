package com.pdk.chat.wx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.AppConfig;
import com.pdk.chat.wx.dto.JsapiTicket;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;


 

public class GetJsapiTicket {

	private static final long serialVersionUID = 4440739483644821986L;
	private  static JsapiTicket jt = null;
	public static JsapiTicket getJsapiTicket() throws WeixinBusinessException {
			if(checkJsapiTicket())
				return jt;
			resetJsapiTicket();
			return jt;
	}
	
	private synchronized static void  resetJsapiTicket() throws WeixinBusinessException {
		if(checkJsapiTicket())
			return ;

		GetAccessToken getAccessToken = AppConfig.getBean(GetAccessToken.class);
		String getTicketUrl = AppConfig.getBean(WeixinUrlInfo.class).getTicket(getAccessToken.getAccessToken().getAccess_token());
		try {
			String ticketStr = HttpSend.send(getTicketUrl);
			ObjectMapper mapper = new ObjectMapper();
			jt = mapper.readValue(ticketStr, JsapiTicket.class);
			jt.setCreateTime(new Date());
		} catch (Exception e){
			throw new WeixinBusinessException ("获取微信jsapi授权异常",e);
		}
	}
	
	private static boolean checkJsapiTicket(){
		if(jt != null && jt.getCreateTime() != null){
			Calendar c1 = Calendar.getInstance();
			c1.setTime(new Date());   
			Calendar c2 = Calendar.getInstance();
			c2.setTime(jt.getCreateTime());  
			long difference=c1.getTimeInMillis()-c2.getTimeInMillis(); 
			long second=difference/1000;
			if(second < 6600){
				return  true;
			}
		}
		return false;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
