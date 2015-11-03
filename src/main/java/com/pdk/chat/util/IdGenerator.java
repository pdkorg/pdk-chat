package com.pdk.chat.util;

/**
 * Created by hubo on 15/8/11
 */
public class IdGenerator {

    public static final String SYSTEM_ID = "0002";

    public static String generateId(String moduleCode) {
        if(moduleCode == null || moduleCode.length() != 2) {
            throw new IllegalArgumentException("module code illegal !");
        }
        return SYSTEM_ID + moduleCode + SequenceGenerator.getInstance().nextId();
    }

    public static String generateId() {
        return generateId("CT");
    }


}
