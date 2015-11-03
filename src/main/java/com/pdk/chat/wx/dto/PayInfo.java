package com.pdk.chat.wx.dto;

/**
 * Created by kangss on 2015/9/23
 *
 */
public class PayInfo {
	private String openid;
	private String ordercode;
    private String appid;
    private String timeStamp;
    private String nonceStr;
    private String packages;
    private String finalsign;
    
	public String getAppid() {
		return appid;
	}
	
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public String getNonceStr() {
		return nonceStr;
	}
	
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	
	public String getPackages() {
		return packages;
	}
	public void setPackages(String packages) {
		this.packages = packages;
	}
	public String getFinalsign() {
		return finalsign;
	}
	public void setFinalsign(String finalsign) {
		this.finalsign = finalsign;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getOrdercode() {
		return ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
}
