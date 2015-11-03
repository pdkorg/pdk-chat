package com.pdk.chat.wx.strategy.fatory;

import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.receive.EventMessageReceive;
import com.pdk.chat.wx.strategy.impl.ClickEventMessageStrategy;
import com.pdk.chat.wx.strategy.impl.DefaultEventStrategy;
import com.pdk.chat.wx.strategy.impl.LocationEventMessageStrategy;
import com.pdk.chat.wx.strategy.impl.SubscribeEventMessageStrategy;
import com.pdk.chat.wx.strategy.itf.WeixinEventStrategy;
import com.pdk.chat.wx.util.EventMessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hubo on 2015/9/23
 */
@Component
public class WeixinEventStrategyFactory {

    @Autowired
    private DefaultEventStrategy baseEventStrategy;
    @Autowired
    private SubscribeEventMessageStrategy subscribeEventMessageStrategy;
    @Autowired
    private ClickEventMessageStrategy clickEventMessageStrategy;
    @Autowired
    private LocationEventMessageStrategy locationEventMessageStrategy;

    private WeixinEventStrategy createEventStrategy(EventMessageTypeEnum typeEnum){
        switch (typeEnum) {
            case SUBSCRIBE_EVENT:
            case UNSUBSCRIBE_EVENT:
            case SCAN_EVENT:
                return subscribeEventMessageStrategy;
            case CLICK_EVENT:
                return clickEventMessageStrategy;
            case LOCATION_EVENT:
                return locationEventMessageStrategy;
            case VIEW_EVENT:
            default:
                return baseEventStrategy;
        }
    }

    public WeixinEventStrategy createEventStrategy(WeixinMessageReceive message){
        return createEventStrategy(EventMessageTypeEnum.getByEvent(((EventMessageReceive) message).getEvent()));
    }

}
