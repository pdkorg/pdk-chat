package com.pdk.chat.wx.util;

import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeixinParseUtil {

	public static final String TYPE_TEXT = "text";

	public static final String TYPE_IMAGE = "image";

	public static final String TYPE_LINK = "link";

	public static final String TYPE_LOCATION = "location";

	public static final String TYPE_VOICE = "voice";

	public static final String TYPE_EVENT = "event";

	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	public static final String EVENT_TYPE_CLICK = "CLICK";

	public static final String EVENT_TYPE_LOCATION = "LOCATION";


	@SuppressWarnings("unchecked")
	private static Map<String, String> parse2Map(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<>();
		InputStream inputStream = request.getInputStream();
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList)
			map.put(e.getName(), e.getText());
		inputStream.close();
		return map;
	}



	public static WeixinMessageReceive parseXml(HttpServletRequest request) throws Exception  {
		Map<String, String> map = parse2Map(request);
		Class<? extends WeixinMessageReceive> clazz = getReceiveClass(map);
		WeixinMessageReceive message = clazz.newInstance();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			Method method;
			try {
				method = clazz.getMethod("set" + entry.getKey(),String.class);
			} catch (NoSuchMethodException e) {
				continue;
			}
			method.invoke(message,entry.getValue());
		}
		return message;
	}

	public static <T extends Object> T paserXml(InputStream is,Class<T> clazz) throws Exception{
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller un = context.createUnmarshaller();
		return (T) un.unmarshal(is);
	}

	private static Class<? extends WeixinMessageReceive> getReceiveClass(Map<String, String> map){
		String type = map.get("MsgType");
		if(TYPE_EVENT.equals(type)){
			String event = map.get("Event");
			EventMessageTypeEnum eventEnum = EventMessageTypeEnum.getByEvent(event);
			if(eventEnum == null)
				return WeixinMessageReceive.class;
			return eventEnum.getReceiveClazz();
		}else {
			MessageTypeEnum typeEnum = MessageTypeEnum.getByWxmsgType(type);
			if (typeEnum == null)
				return WeixinMessageReceive.class;
			return typeEnum.getReceiveClazz();
		}
	}

	public static String transMsg2Xml(Object object){
		xstream.alias("xml", object.getClass());
		return xstream.toXML(object);
	}

	private static XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer(
			"_-", "_")) {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out, xmlFriendlyReplacer()) {
				boolean cdata = true;
				@SuppressWarnings("unchecked")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}
				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});

}
