package com.km.peter.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.km.peter.http.Response;
import com.km.peter.payment.Payment;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import org.junit.Test;

public class HstyPayServiceTest {

    @Test
    public void testUnifiedOrder() {
        Payment payment = new HstyPayService("appId", "merchantId", "key");

        UnifiedOrderModel params = new UnifiedOrderModel("oWZkgwp5MfVg6lvRuvNquWr2MjGA", "orderNo", 30, "body",
                "127.0.0.1", "http://notify");
        Response response = null;
        try {
            response = payment.unifiedOrder(params);
            System.out.println(new ObjectMapper().writeValueAsString(response));
        } catch (RequestFailedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCancelOrder() throws JsonProcessingException {
        Payment payment = new HstyPayService("appId", "merchantId", "key");

        Response response = payment.cancel("123123123");
        System.out.println(new ObjectMapper().writeValueAsString(response));
    }
}
