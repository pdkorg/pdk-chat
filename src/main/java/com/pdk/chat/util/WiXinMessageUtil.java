package com.pdk.chat.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hubo on 2015/8/29
 */
public class WiXinMessageUtil {

    public static boolean containsFaceImage(String content) {
        return content != null
                && content.matches(".*(<img.*src=[\"']http://.*"+ CommonUtil.getContextPath() + "/face/" + "\\d{1,2}\\.gif)+.*");
    }


    public static String transWx2Msg(String baseUrl, String wxMsg) {
        c:while (StringUtils.isNotEmpty(StringUtils.trim(wxMsg))
                && wxMsg.contains("/:")) {

            int firstIndex = wxMsg.indexOf("/:");
            int nextIndex = firstIndex + 3;

            while(EmoticonsEnum.getByCode(wxMsg.substring(firstIndex, nextIndex)) == null) {
                nextIndex++;
                if(nextIndex > wxMsg.length() ) {
                    break c;
                }
            }

            wxMsg = wxMsg.substring(0, firstIndex) + "<img src='" + baseUrl + "/static/img/face/"
                    + EmoticonsEnum.getByCode(wxMsg.substring(firstIndex, nextIndex)).getPic()+"' />"
                    + (nextIndex == wxMsg.length() ? "" : wxMsg.substring(nextIndex, wxMsg.length()));
        }

        return wxMsg;
    }

    public static String transMsg2Wx(String mes){
        StringBuilder buffer = new StringBuilder(mes);
        int end =  buffer.length();
        for(int start = 0 ; start < end;){
            int[] pos = getStartPosAndEndPos(buffer.toString(),"<img",">",start);
            if(pos[0] == -1 || pos[1] == -1){
                break;
            }
            start = pos[0];
            end = pos[1];
            String img = buffer.substring(start, end);
            String pic = getPicName(img);
            String code = pic2Code(pic);
            if(code != null && !code.equals("")){
                buffer.delete(start, end);
                buffer.insert(start, code);
                start += code.length();
            }
        }
        return buffer.toString();
    }

    private static String getPicName(String imgTag){
        int start = imgTag.indexOf("/face/")+"/face/".length();
        if (start == -1)
            return "";
        int end = imgTag.indexOf("\"", start);
        return imgTag.substring(start, end);
    }

    private static int[] getStartPosAndEndPos(String mes,String startString,String endString,int start){
        int pos[] = new int[2];
        pos[0] = mes.indexOf(startString,start);
        if(pos[0]  == -1){
            pos[1] = -1;
            return pos;
        }
        pos[1] = mes.indexOf(endString,pos[0]);
        if(pos[1] != -1)
            pos[1] +=endString.length();
        return pos;
    }

    public static String pic2Code(String pic){
        EmoticonsEnum emonticon = EmoticonsEnum.getByPic(pic);
        if(emonticon == null)
            return "";
        return emonticon.getCode();
    }

}
