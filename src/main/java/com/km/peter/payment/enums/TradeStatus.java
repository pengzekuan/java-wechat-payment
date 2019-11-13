package com.km.peter.payment.enums;

public enum TradeStatus {
    SUCCESS("SUCCESS", ""),
    REFUND("REFUND", ""),
    NOTPAY("NOTPAY", ""),
    CLOSED("CLOSED", ""),
    REVOKED("REVOKED", ""),
    USERPAYING("USERPAYING", ""),
    PAYERROR("PAYERROR", "");

    private String key;

    private String description;

    TradeStatus(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
