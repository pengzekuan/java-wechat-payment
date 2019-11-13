package com.km.peter.payment.service;

import com.km.peter.http.Response;
import com.km.peter.payment.Payment;
import com.km.peter.payment.enums.PaymentScene;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import org.junit.Test;

public class WechatPayServiceTest {

    @Test
    public void testUnifiedOrder() {
        Payment payment = new WechatPayService("AFAEFAW53242342423", "1231231230", "key", PaymentScene.WAP,
                "王者荣耀", "http://game.qq.com");

        UnifiedOrderModel params = new UnifiedOrderModel("oWZkgwp5MfVg6lvRuvNquWr2MjGA", "orderNo", 30, "body",
                "127.0.0.1", "http://notify");
        Response response = null;
        try {
            response = payment.unifiedOrder(params);
            System.out.println(response.getData());
        } catch (RequestFailedException e) {
            e.printStackTrace();
        }

    }
}
