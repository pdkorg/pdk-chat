package com.pdk.chat.util;

import java.util.HashMap;
import java.util.Map;

public enum EmoticonsEnum {
	Emoticons_0("/::)","0.gif"),
	Emoticons_1("/::~","1.gif"),
	Emoticons_2("/::B","2.gif"),
	Emoticons_3("/::|","3.gif"),
	Emoticons_4("/:8-)","4.gif"),
	Emoticons_5("/::<","5.gif"),
	Emoticons_6("/::$","6.gif"),
	Emoticons_7("/::X","7.gif"),
	Emoticons_8("/::Z","8.gif"),
	Emoticons_9("/::'(","9.gif"),
	Emoticons_10("/::-|","10.gif"),
	Emoticons_11("/::@","11.gif"),
	Emoticons_12("/::P","12.gif"),
	Emoticons_13("/::D","13.gif"),
	Emoticons_14("/::O","14.gif"),
	Emoticons_15("/::(","15.gif"),
	Emoticons_16("/::+","16.gif"),
	Emoticons_17("/:--b","17.gif"),
	Emoticons_18("/::Q","18.gif"),
	Emoticons_19("/::T","19.gif"),
	Emoticons_20("/:,@P","20.gif"),
	Emoticons_21("/:,@-D","21.gif"),
	Emoticons_22("/::d","22.gif"),
	Emoticons_23("/:,@o","23.gif"),
	Emoticons_24("/::g","24.gif"),
	Emoticons_25("/:|-)","25.gif"),
	Emoticons_26("/::!","26.gif"),
	Emoticons_27("/::L","27.gif"),
	Emoticons_28("/::>","28.gif"),
	Emoticons_29("/::,@","29.gif"),
	Emoticons_30("/:,@f","30.gif"),
	Emoticons_31("/::-S","31.gif"),
	Emoticons_32("/:?","32.gif"),
	Emoticons_33("/:,@x","33.gif"),
	Emoticons_34("/:,@@","34.gif"),
	Emoticons_35("/::8","35.gif"),
	Emoticons_36("/:,@!","36.gif"),
	Emoticons_37("/:!!!","37.gif"),
	Emoticons_38("/:xx","38.gif"),
	Emoticons_39("/:bye","39.gif"),
	Emoticons_40("/:wipe","40.gif"),
	Emoticons_41("/:dig","41.gif"),
	Emoticons_42("/:handclap","42.gif"),
	Emoticons_43("/:&-(","43.gif"),
	Emoticons_44("/:B-)","44.gif"),
	Emoticons_45("/:<@","45.gif"),
	Emoticons_46("/:@>","46.gif"),
	Emoticons_47("/::-O","47.gif"),
	Emoticons_48("/:>-|","48.gif"),
	Emoticons_49("/:P-(","49.gif"),
	Emoticons_50("/::'|","50.gif"),
	Emoticons_51("/:X-)","51.gif"),
	Emoticons_52("/::*","52.gif"),
	Emoticons_53("/:@x","53.gif"),
	Emoticons_54("/:8*","54.gif"),
	Emoticons_55("/:pd","55.gif"),
	Emoticons_56("/:<W>","56.gif"),
	Emoticons_57("/:beer","57.gif"),
	Emoticons_58("/:basketb","58.gif"),
	Emoticons_59("/:oo","59.gif"),
	Emoticons_60("/:coffee","60.gif"),
	Emoticons_61("/:eat","61.gif"),
	Emoticons_62("/:pig","62.gif"),
	Emoticons_63("/:rose","63.gif"),
	Emoticons_64("/:fade","64.gif"),
	Emoticons_65("/:showlove","65.gif"),
	Emoticons_66("/:heart","66.gif"),
	Emoticons_67("/:break","67.gif"),
	Emoticons_68("/:cake","68.gif"),
	Emoticons_69("/:li","69.gif"),
	Emoticons_70("/:bome","70.gif"),
	Emoticons_71("/:kn","71.gif"),
	Emoticons_72("/:footb","72.gif"),
	Emoticons_73("/:ladybug","73.gif"),
	Emoticons_74("/:shit","74.gif"),
	Emoticons_75("/:moon","75.gif"),
	Emoticons_76("/:sun","76.gif"),
	Emoticons_77("/:gift","77.gif"),
	Emoticons_78("/:hug","78.gif"),
	Emoticons_79("/:strong","79.gif"),
	Emoticons_80("/:weak","80.gif"),
	Emoticons_81("/:share","81.gif"),
	Emoticons_82("/:v","82.gif"),
	Emoticons_83("/:@)","83.gif"),
	Emoticons_84("/:jj","84.gif"),
	Emoticons_85("/:@@","85.gif"),
	Emoticons_86("/:bad","86.gif"),
	Emoticons_87("/:lvu","87.gif"),
	Emoticons_88("/:no","88.gif"),
	Emoticons_89("/:ok","89.gif"),
	Emoticons_90("/:love","90.gif"),
	Emoticons_91("/:<L>","91.gif"),
	Emoticons_92("/:jump","92.gif"),
	Emoticons_93("/:shake","93.gif"),
	Emoticons_94("/:<O>","94.gif"),
	Emoticons_95("/:circle","95.gif"),
	Emoticons_96("/:kotow","96.gif"),
	Emoticons_97("/:turn","97.gif"),
	Emoticons_98("/:skip","98.gif"),
	Emoticons_99("/:oY","99.gif");
	private String code;
	private String pic;
	public static Map<String ,EmoticonsEnum> codeMap = new HashMap<String, EmoticonsEnum>();
	public static Map<String ,EmoticonsEnum> picMap = new HashMap<String, EmoticonsEnum>();
	private EmoticonsEnum(String code,String pic){
		this.code = code;
		this.pic = pic;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	
	private static void init(){
		for(EmoticonsEnum emoticon :EmoticonsEnum.values()){
			codeMap.put(emoticon.getCode(), emoticon); 
			picMap.put(emoticon.getPic(), emoticon); 
		}
	}
	public static EmoticonsEnum getByCode(String code) {
		if(codeMap.isEmpty())
			init();
		return codeMap.get(code);
	}
	
	public static EmoticonsEnum getByPic(String pic) {
		if(picMap.isEmpty())
			init();
		return picMap.get(pic);
	}
	
	
	
	
}
