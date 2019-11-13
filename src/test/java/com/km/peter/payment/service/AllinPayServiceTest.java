package com.km.peter.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.km.peter.http.Response;
import com.km.peter.payment.Payment;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import org.junit.Test;

public class AllinPayServiceTest {

    @Test
    public void testUnifiedOrder() {
        Payment payment = new AllinPayService("wx539072349149ec78", "560731073991L7U", "00171161", "34f5540b12a34b8e80fccfe4dcce0d77");

        UnifiedOrderModel params = new UnifiedOrderModel("oWZkgwp5MfVg6lvRuvNquWr2MjGA", "orderNo", 30, "body",
                "127.0.0.1", "http://notify");
        Response response = null;
        try {
            response = payment.unifiedOrder(params);
            System.out.println(new ObjectMapper().writeValueAsString(response.getData()));
        } catch (RequestFailedException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
