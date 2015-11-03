package com.pdk.chat.wx.util;

import com.pdk.chat.util.AppConfig;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

//@Component
public class SignUtil {

	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] {AppConfig.getBean(WxConfigInfo.class).getToken(), timestamp, nonce };
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md;
		String tmpStr = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return tmpStr != null && tmpStr.equals(signature.toUpperCase());
	}

	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		return new String(tempArr);
	}

	public static String createMD5Sign(SortedMap<String, String> packageParams) {
		StringBuilder sb = new StringBuilder();
		Set<?> es = packageParams.entrySet();
		Iterator<?> it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k).append("=").append(v).append("&");
			}
		}
		// sb.append("key=" + this.getKey());
		sb.append("key=").append(packageParams.get("key"));
		return MD5Util.MD5Encode(sb.toString(), "UTF-8")
				.toUpperCase();
	}
}
