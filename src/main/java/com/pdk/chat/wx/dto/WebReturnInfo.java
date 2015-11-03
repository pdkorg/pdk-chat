package com.pdk.chat.wx.dto;


/**
 * Created by kangss on 2015/9/23
 */
public class WebReturnInfo<T> {
	private boolean success;

	private String msg;

	private T data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
