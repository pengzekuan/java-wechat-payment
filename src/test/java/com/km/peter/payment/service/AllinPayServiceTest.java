package com.km.peter.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.km.peter.http.Response;
import com.km.peter.payment.Payment;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.StringHelper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AllinPayServiceTest {

    private String openId = "oWZkgwp5MfVg6lvRuvNquWr2MjGA";

    private String prefix = "test_";

    private String remoteIp = "127.0.0.1";

    private String notifyUrl = "http://notify";

    private Payment payment;

    @Before
    public void before() {
        this.payment = new AllinPayService("wx539072349149ec78", "560731073991L7U", "00171161", "34f5540b12a34b8e80fccfe4dcce0d77");
    }

    @Test
    public void testUnifiedOrder() throws RequestFailedException, JsonProcessingException {

        UnifiedOrderModel params = new UnifiedOrderModel(this.openId, this.prefix + StringHelper.nonceStr(), 1, "body",
                this.remoteIp, this.notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        System.out.println(new ObjectMapper().writeValueAsString(response.getData()));
    }

    @Test
    public void testQuery() throws RequestFailedException {
        Payment payment = new AllinPayService("wx841e62d0697c2a8b", "5527310739910K5",
                "00162646", "34f5540b12a34b8e80fccfe4dcce0d77");
        Response response = payment.query("r3rawrfawrfaw");
        System.out.println(new JSONObject(response));

        System.out.println(new JSONObject(this.payment.query("orderNo")));
    }

    @Test
    public void testClose() throws RequestFailedException {
        String orderNo = this.prefix + StringHelper.nonceStr();
        UnifiedOrderModel params = new UnifiedOrderModel(this.openId, orderNo, 1, "body",
                this.remoteIp, this.notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        Assert.assertTrue(response.isSuccess());

        Response cancelRes = this.payment.query(orderNo);
        Assert.assertTrue(cancelRes.isSuccess());
    }
}
