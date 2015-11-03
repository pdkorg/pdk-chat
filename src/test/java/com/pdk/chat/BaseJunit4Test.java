package com.pdk.chat;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by hubo on 2015/8/28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "parent", locations = {"classpath:spring.xml", "classpath:spring-mongodb.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class BaseJunit4Test {

}
