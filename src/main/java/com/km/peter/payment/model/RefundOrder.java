package com.km.peter.payment.model;

public class RefundOrder {

    /**
     * 用户标识
     */
    private String openId;
    /**
     * 系统订单号
     */
    private String orderNo;
    /**
     * 系统退款单号
     */
    private String refundId;
    /**
     * 平台退款单号
     */
    private String outRefundId;
    /**
     * 退款渠道
     */
    private String refundChannel;
    /**
     * 订单金额
     */
    private int amount;
    /**
     * 退款金额
     */
    private int refundFee;
    /**
     * 退款时间
     */
    private String refundTime;
    /**
     * 退款状态
     */
    private String refundStatus;

    public RefundOrder() {
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getOutRefundId() {
        return outRefundId;
    }

    public void setOutRefundId(String outRefundId) {
        this.outRefundId = outRefundId;
    }

    public String getRefundChannel() {
        return refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel;
    }

    public int getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(int refundFee) {
        this.refundFee = refundFee;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
}
