package com.km.peter.payment.util;

public class StringHelper {

    public static String nonceStr() {
        return String.valueOf(System.currentTimeMillis());
    }
}
