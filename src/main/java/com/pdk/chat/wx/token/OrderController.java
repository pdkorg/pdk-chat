package com.pdk.chat.wx.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.dto.SimpleOrder;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.wx.dto.PayInfo;
import com.pdk.chat.wx.dto.Unifiedorder;
import com.pdk.chat.wx.dto.UnifiedorderReturn;
import com.pdk.chat.wx.dto.WebReturnInfo;
import com.pdk.chat.wx.service.OrderService;
import com.pdk.chat.wx.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by kangss on 2015/9/23
 */


@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WxConfigInfo wxConfigInfo;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private WeixinUrlInfo weixinUrlInfo;

    @RequestMapping("/pay/{orderId}")
    @ResponseBody
    public WebReturnInfo<PayInfo> pay(@PathVariable("orderId") String orderId,
                                        @RequestParam(value = "openId", required = false)String openId,
                                        @RequestParam(value = "userId", required = false)String userId,
                                        @RequestParam(value = "tip", required = false)BigDecimal tip){
        WebReturnInfo<PayInfo> info = new WebReturnInfo<>();
        if (!StringUtils.isEmpty(orderId))
            try {
                SimpleOrder order = orderService.reviewOrder(orderId,userId);
                UnifiedorderReturn ur = createUnifiedorder(openId,order,tip);
                SortedMap<String,String> params = new TreeMap<>();
                String timestamp = Sha1Util.getTimeStamp();
                String packages = "prepay_id=" + ur.getPrepay_id();
                params.put("appId", ur.getAppid());
                params.put("timeStamp", timestamp);
                params.put("nonceStr", ur.getNonce_str());
                params.put("package", packages);
                params.put("signType", "MD5");
                params.put("key", wxConfigInfo.getPartnerkeykey());

                PayInfo payInfo = new PayInfo();
                payInfo.setOpenid(openId);
                payInfo.setOrdercode(order.getOrderCode());
                payInfo.setAppid(ur.getAppid());
                payInfo.setTimeStamp(timestamp);
                payInfo.setNonceStr(ur.getNonce_str());
                payInfo.setPackages(packages);
                payInfo.setFinalsign(SignUtil.createMD5Sign(params));
                info.setData(payInfo);
                info.setSuccess(true);
            } catch (Exception e) {
                info.setSuccess(false);
                if(e instanceof WeixinBusinessException)
                    info.setMsg(e.getMessage());
                else
                    info.setMsg("支付时出现异常");
            }
        return info;
    }

    private Unifiedorder newUnifiedorder(String openId,SimpleOrder order,BigDecimal tip){
        tip = tip == null?BigDecimal.ZERO:tip;
        BigDecimal money = order.getMny().subtract(order.getCouponMny());
        money = (money.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : money).add(tip);
        money = money.setScale(2,BigDecimal.ROUND_HALF_UP);
        int total_fee =Double.valueOf(money.doubleValue()*100).intValue();
        String appid = wxConfigInfo.getWxappid();
        String key = wxConfigInfo.getPartnerkeykey();
        String currTime = TenpayUtil.getCurrTime();
        String mch_id =  wxConfigInfo.getPartner();
        String nonce_str = currTime.substring(8,currTime.length()) + String.valueOf(TenpayUtil.buildRandom(4));
        String body = "跑的快";
        String spbill_create_ip = request.getRemoteAddr();

        String notify_url = getPdkManagerPayUrl(order.getOrderId(), order.getCouponId(),money, order.getCouponMny(), tip);

        String outTradeNo = order.getOrderCode()+"_"+System.currentTimeMillis();
        String trade_type = "JSAPI";
        SortedMap<String,String> packageParams = new TreeMap<>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("body", body);
        packageParams.put("attach", order.getUserId());
        packageParams.put("out_trade_no", outTradeNo);
        packageParams.put("total_fee", String.valueOf(total_fee));
        packageParams.put("spbill_create_ip", spbill_create_ip);
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);
        packageParams.put("openid", openId);
        packageParams.put("key", key);

        String sign = SignUtil.createMD5Sign(packageParams);
        Unifiedorder unifiedorder = new Unifiedorder();
        unifiedorder.setAppid(appid);
        unifiedorder.setMch_id(mch_id);
        unifiedorder.setNonce_str(nonce_str);
        unifiedorder.setBody(body);
        unifiedorder.setAttach(order.getUserId());
        unifiedorder.setOut_trade_no(outTradeNo);
        unifiedorder.setTotal_fee(total_fee);
        unifiedorder.setSpbill_create_ip(spbill_create_ip);
        unifiedorder.setNotify_url(notify_url);
        unifiedorder.setTrade_type(trade_type);
        unifiedorder.setOpenid(openId);
        unifiedorder.setSign(sign);
        return unifiedorder;
    }

    private String getPdkManagerPayUrl(String orderId,String couponId,BigDecimal actMny,BigDecimal couponMny,BigDecimal tip){
        return PdkManagerUrlInfo.payOrder
                .replace("{orderId}", orderId)
                .replace("{actMny}",actMny.toString())
                .replace("{couponId}", StringUtils.isEmpty(couponId) ? "nouse" : couponId)
                .replace("{couponMny}", couponMny.toString())
                .replace("{tip}", tip.toString());
    }

    private UnifiedorderReturn createUnifiedorder(String openId,SimpleOrder order,BigDecimal tip) throws Exception {
        Unifiedorder wxOrder = newUnifiedorder(openId, order, tip);
        String xml =  WeixinParseUtil.transMsg2Xml(wxOrder);
        String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        InputStream is = HttpSend.request(createOrderURL, xml, RequestMethod.POST);
        UnifiedorderReturn ur = WeixinParseUtil.paserXml(is,UnifiedorderReturn.class);
        if (ur == null || StringUtils.isEmpty(ur.getPrepay_id()))
            throw new WeixinBusinessException("订单支付异常,微信订单未找到");
        if(!"SUCCESS".equals(ur.getReturn_code()))
            throw new WeixinBusinessException(ur.getReturn_msg());
        if(!"SUCCESS".equals(ur.getResult_code()))
            throw new WeixinBusinessException("异常："+ur.getErr_code()+":"+ur.getErr_code_des());
        return ur;
    }

    @RequestMapping("/user")
    public String userOrderList(@RequestParam(value = "tag", required = false) String tag,
                                @RequestParam(value = "code") String code,
                                @RequestParam(value = "state", required = false) String state,
                                Map<String, Object> model) {

        try {

            model.put("backUrl",weixinUrlInfo.getAuthorize(wxConfigInfo.getPdkchaturl()+"/order/user?tag=1"));

            String json = HttpSend.send(weixinUrlInfo.getOauth2(code));

            JSONObject jsonObject = JSON.parseObject(json);

            String openId = jsonObject.getString("openid");

            if(openId != null ) {
                model.put("openId", openId);
                model.put("queryUserOrderUrl", PdkManagerUrlInfo.queryUserOrderUrl.replace("{sourceId}", openId));
            }

        } catch (WeixinBusinessException e) {
            ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
        }

        return "wx/order_list";
    }

    @RequestMapping("/user/{sourceId}")
    public String userOrderList(@PathVariable String sourceId,
                                 Map<String, Object> model) {

        model.put("backUrl",weixinUrlInfo.getAuthorize(wxConfigInfo.getPdkchaturl()+"/order/user?tag=1"));

        if (sourceId != null) {
            model.put("openId", sourceId);
            model.put("queryUserOrderUrl", PdkManagerUrlInfo.queryUserOrderUrl.replace("{sourceId}", sourceId));
        }

        return "wx/order_list";
    }

    @RequestMapping("/{orderId}")
    public String userOrderDetail(@PathVariable("orderId") String orderId, @RequestParam("openId") String openId, Map<String, Object> model) throws WeixinBusinessException {

        if(orderId == null) {
            //@todo 胡博脑子短路，不知道该怎么写，等神智清醒时再补充
        }

        model.put("queryOrderDetailUrl", PdkManagerUrlInfo.queryOrderDetailUrl.replace("{orderId}", orderId));
        model.put("openId", openId);

        return "wx/order_detail";
    }

    @RequestMapping("/score/employee/{orderId}")
    public String employeeScore(@PathVariable("orderId") String orderId, @RequestParam("openId") String openId, Map<String, Object> model) throws WeixinBusinessException {

        model.put("employeeScoreUrl", PdkManagerUrlInfo.employeeScoreUrl.replace("{sourceId}", openId));
        model.put("checkEmployeeScoreUrl", PdkManagerUrlInfo.checkEmployeeScoreUrl);
        model.put("openId", openId);
        model.put("orderId", orderId);

        return "wx/employee_score";
    }

    @RequestMapping("/runner/{orderId}")
    public String runnerOrderDetail(@PathVariable("orderId") String orderId, Map<String, Object> model) throws WeixinBusinessException {

        if(orderId == null) {
            //@todo 胡博脑子短路，不知道该怎么写，等神智清醒时再补充
        }

        model.put("queryOrderDetailUrl", PdkManagerUrlInfo.queryOrderDetailUrl.replace("{orderId}", orderId));

        return "wx/order_detail_runner";
    }

}
