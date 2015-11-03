package com.pdk.chat.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig  implements ApplicationContextAware {
	 private static ApplicationContext applicationContext;
	    public void setApplicationContext(ApplicationContext arg0)
	            throws BeansException {
	        applicationContext = arg0;
	    }
	    /**
	     * 获取applicationContext对象
	     * @return
	     */
	    public static ApplicationContext getApplicationContext(){
	        return applicationContext;
	    }
	    /**
	     * 根据bean的id来查找对象
	     * @return
	     */

	    public static Object getBean(String id){
	        return applicationContext.getBean(id);
	    }
	    /**
	     * 根据bean的class来查找对象
	     * @param c
	     * @return
	     */
	    public static <T>T getBean(Class<T> c){
	        return applicationContext.getBean(c);
	    }
	    
	
}
