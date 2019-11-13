package com.km.peter.payment.enums;

public enum PayChannel {
    HSTY_PAY("hstypay"),
    ALLIN_PAY("allinpay"),
    WECHAT_PAY("wechatpay");

    private String key;

    PayChannel(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
