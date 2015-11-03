package com.pdk.chat.wx.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.wx.util.HttpSend;
import com.pdk.chat.wx.util.PdkManagerUrlInfo;
import com.pdk.chat.wx.util.WeixinUrlInfo;
import com.pdk.chat.wx.util.WxConfigInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by hubo on 2015/9/29
 */
@Controller
@RequestMapping("/coupons")
public class CouponsController {

    @Autowired
    private WeixinUrlInfo weixinUrlInfo;
    @Autowired
    private WxConfigInfo wxConfigInfo;

    @RequestMapping("/user")
    public String userCoupons(@RequestParam(value = "tag", required = false) String tag,
                              @RequestParam(value = "code") String code,
                              @RequestParam(value = "state", required = false) String state,
                              Map<String, Object> model) {

        model.put("backUrl",weixinUrlInfo.getAuthorize(wxConfigInfo.getPdkchaturl()+"/coupons/user?tag=1"));

        try {
            String json = HttpSend.send(weixinUrlInfo.getOauth2(code));

            JSONObject jsonObject = JSON.parseObject(json);

            String openId = jsonObject.getString("openid");

            if(openId != null ) {
                model.put("openId", openId);
                model.put("effectCouponUrl", PdkManagerUrlInfo.effectCouponUrl.replace("{sourceId}", openId));
                model.put("overdueCouponUrl", PdkManagerUrlInfo.overdueCouponUrl.replace("{sourceId}", openId));
            }

        } catch (WeixinBusinessException e) {
            ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
        }

        return "wx/coupons";
    }

    @RequestMapping("/user/{sourceId}")
    public String userCoupons(@PathVariable String sourceId,
                              Map<String, Object> model) {

        model.put("openId", sourceId);
        model.put("effectCouponUrl", PdkManagerUrlInfo.effectCouponUrl.replace("{sourceId}", sourceId));
        model.put("overdueCouponUrl", PdkManagerUrlInfo.overdueCouponUrl.replace("{sourceId}", sourceId));
        model.put("backUrl",weixinUrlInfo.getAuthorize(wxConfigInfo.getPdkchaturl()+"/coupons/user?tag=1"));

        return "wx/coupons";
    }

    @RequestMapping("/score/{sourceId}")
    public String scoreCoupon(@PathVariable String sourceId,
                              BigDecimal couponMny, BigDecimal couponMinPayMny, String couponType, String endDate,
                              Map<String, Object> model) {

        model.put("openId", sourceId);
        model.put("couponMny", couponMny);
        model.put("couponMinPayMny", couponMinPayMny);
        model.put("couponType", couponType);
        model.put("endDate", endDate);

        return "wx/score_coupon";
    }

}
