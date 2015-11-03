package com.pdk.chat.wx.service;

import com.alibaba.fastjson.JSON;
import com.pdk.chat.dto.SimpleOrder;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.wx.util.HttpSend;
import com.pdk.chat.wx.util.PdkManagerUrlInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by kangss on 2015/9/23
 */
@Service
public class OrderService {

    /**
     * 校验订单是否存，是否已支付,并返回订单信息
     * @param orderId 订单主键
     * @param userId 用户主键
     * @throws WeixinBusinessException 如果订单不存在，或订单已支付，通过exception抛出
     */
    public SimpleOrder reviewOrder(String orderId,String userId) throws WeixinBusinessException {
        String url = PdkManagerUrlInfo.reviewOrder.replace("{orderId}", orderId);
        SimpleOrder order = JSON.parseObject(HttpSend.send(url),SimpleOrder.class);
        if(StringUtils.isEmpty(order.getOrderCode()))
            throw new WeixinBusinessException("订单不存在");
        if(order.getUserId() == null || !order.getUserId().equals(userId))
            throw new WeixinBusinessException("订单用户信息异常");
        if(order.getPayStatus() == 1){
            throw new WeixinBusinessException("订单已支付，无需再次支付");
        }
        return order;
    }


}
