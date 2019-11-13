package com.km.peter.payment.enums;

public enum PaymentScene {
    IOS("IOS", "app_name", "bundle_id"),
    ANDROID("Android", "app_name", "package_name"),
    WAP("Wap", "wap_name", "wap_url");

    private String key;

    private String appNameField;
    private String identificationField;

    PaymentScene(String key, String appNameField, String identificationField) {
        this.key = key;
        this.appNameField = appNameField;
        this.identificationField = identificationField;
    }

    PaymentScene(String key) {
        this.key = key;
    }

    public String getAppNameField() {
        return appNameField;
    }

    public void setAppNameField(String appNameField) {
        this.appNameField = appNameField;
    }

    public String getIdentificationField() {
        return identificationField;
    }

    public void setIdentificationField(String identificationField) {
        this.identificationField = identificationField;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
