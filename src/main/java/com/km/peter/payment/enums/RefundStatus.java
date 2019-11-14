package com.km.peter.payment.enums;

public enum RefundStatus {
    SUCCESS("SUCCESS", ""),
    FAIL("FAIL", ""),
    PROCESSING("PROCESSING", ""),
    NOTSURE("NOTSURE", ""),
    CHANGE("CHANGE", ""),
    ;

    private String key;

    private String description;

    RefundStatus(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
