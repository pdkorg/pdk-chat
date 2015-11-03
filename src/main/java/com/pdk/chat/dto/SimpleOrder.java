package com.pdk.chat.dto;

import java.math.BigDecimal;

/**
 * Created by kangss on 2015/10/12.
 */
public class SimpleOrder {
    private String orderId;
    private String userId;
    private BigDecimal mny = BigDecimal.ZERO;
    private String couponId;
    private BigDecimal couponMny = BigDecimal.ZERO;
    private Short payStatus = 0;
    private String orderCode;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getMny() {
        return mny;
    }

    public void setMny(BigDecimal mny) {
        if(mny != null)
            this.mny = mny;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public BigDecimal getCouponMny() {
        return couponMny;
    }

    public void setCouponMny(BigDecimal couponMny) {
        if(couponMny != null)
            this.couponMny = couponMny;
    }

    public Short getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Short payStatus) {
        if(payStatus != null)
            this.payStatus = payStatus;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
