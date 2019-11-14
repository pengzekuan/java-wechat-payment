package com.km.peter.payment.model;

import java.util.List;

public class Order {

    private String wechatAppId;

    private String merchantId;

    private String openId;

    private String orderNo;

    private String transNo;

    private int amount;

    private String attach;

    private String payInfo;

    /**
     * 支付时间
     * 格式：yyyyMMddHHmmss
     */
    private String payTime;

    private String tradeStatus;
    /**
     * 退款信息
     *
     * @return
     */
    private List<RefundOrder> refundOrders;

    public Order() {
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getWechatAppId() {
        return wechatAppId;
    }

    public void setWechatAppId(String wechatAppId) {
        this.wechatAppId = wechatAppId;
    }

    public List<RefundOrder> getRefundOrders() {
        return refundOrders;
    }

    public void setRefundOrders(List<RefundOrder> refundOrders) {
        this.refundOrders = refundOrders;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }
}
