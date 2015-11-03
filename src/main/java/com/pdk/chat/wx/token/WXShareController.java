package com.pdk.chat.wx.token;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.wx.dto.JsapiTicket;
import com.pdk.chat.wx.service.WXShareService;
import com.pdk.chat.wx.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/wxshare")
public class WXShareController {
	

	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected WXShareService wxShareService;
	@Autowired
	protected WxConfigInfo wxConfigInfo;
	@Autowired
	private WeixinUrlInfo weixinUrlInfo;

	@RequestMapping(value = "init", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String init(@RequestParam(value = "url") String url,Map<String,Object> map) throws Exception {
		JSONObject config = new JSONObject();
		JsapiTicket jt = GetJsapiTicket.getJsapiTicket();
		SortedMap<String,String> signParams = new  TreeMap<>();
		String nonceStr = Sha1Util.getNonceStr();
		String timestamp =  Sha1Util.getTimeStamp();
		signParams.put("jsapi_ticket",  jt.getTicket());
		signParams.put("noncestr",nonceStr);
		signParams.put("timestamp", timestamp);
		signParams.put("url", url);
		String signature =  Sha1Util.createSHA1Sign(signParams);
		config.put("appId", wxConfigInfo.getWxappid());
		config.put("timestamp", timestamp);
		config.put("nonceStr", nonceStr);
		config.put("signature",signature);
		map.put("appId", wxConfigInfo.getWxappid());
		map.put("timestamp", timestamp);
		map.put("nonceStr", nonceStr);
		map.put("signature", signature);
		return config.toString();
	}

	@RequestMapping(value = "share",produces = "application/json; charset=utf-8")
    @ResponseBody
    public String share(@RequestParam(value = "openId") String openId) {
		try {
			JSONObject couponJson = wxShareService.shareGetGift(openId);
			return couponJson.toJSONString();
		} catch (WeixinBusinessException e) {
			ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
			JSONObject res  = new JSONObject();
			res.put("success", false);
			res.put("msg",  e.getMessage());
			return res.toJSONString();
		}
    }
	@RequestMapping(value = "showCoupon",produces = "application/json; charset=utf-8")
	public String showCoupon(@RequestParam(value = "couponMny") String couponMny,
							 @RequestParam(value = "couponMinPayMny") String couponMinPayMny,
							 @RequestParam(value = "flowTypeName") String flowTypeName,
							 @RequestParam(value = "endDate") String endDate,
							 @RequestParam(value = "openId") String openId,
							 Map<String,Object> map){
		map.put("couponMny",couponMny);
		map.put("couponMinPayMny",couponMinPayMny);
		map.put("flowTypeName",flowTypeName);
		map.put("endDate",endDate);
		map.put("openId",openId);

		return "wx/share_coupon";
	}

	@RequestMapping("/fx")
	public String fx(@RequestParam(value = "code") String code,Map<String,Object> map) throws WeixinBusinessException {
		String json = HttpSend.send(weixinUrlInfo.getOauth2(code));
		JSONObject jsonObject = JSON.parseObject(json);
		map.put("openId",jsonObject.getString("openid"));
		return "wx/fx";
	}

	@RequestMapping("/fx/{openId}")
	public String fx_openId(@PathVariable(value = "openId") String openId,Map<String,Object> map) throws WeixinBusinessException {
		map.put("openId",openId);
		return "wx/fx";
	}
	
}
