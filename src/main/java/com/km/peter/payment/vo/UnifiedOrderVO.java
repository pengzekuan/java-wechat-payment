package com.km.peter.payment.vo;

public class UnifiedOrderVO {

    private int amount;
    private String orderNo;
    private String transNo;
    private String transStatus;
    private String payInfo;

    public UnifiedOrderVO() {
    }

    public UnifiedOrderVO(String orderNo, String transNo, String transStatus, String payInfo, int amount) {
        this.orderNo = orderNo;
        this.transNo = transNo;
        this.transStatus = transStatus;
        this.payInfo = payInfo;
        this.amount = amount;
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

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }
}
