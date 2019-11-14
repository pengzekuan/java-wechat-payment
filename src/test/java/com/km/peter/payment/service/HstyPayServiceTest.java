package com.km.peter.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.km.peter.http.Response;
import com.km.peter.payment.Payment;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.model.Order;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.StringHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HstyPayServiceTest {

    private Payment payment;

    private String openId = "oWZkgwp5MfVg6lvRuvNquWr2MjGA";

    private String orderPrefix = "test_";

    private String remoteIp = "127.0.0.1";

    private String notifyUrl = "http://notify";

    @Before
    public void before() {
        String wechatAppId = "wxb767f1a6d4b4502d";
        String merchantId = "10300029484";
        String key = "wLtghjre4qTrpSPYP7qwUNdFWbrLgAJJ";

        this.payment = new HstyPayService(wechatAppId, merchantId, key);
    }

    @Test
    public void testUnifiedOrder() throws RequestFailedException, JsonProcessingException {
        String orderNo = orderPrefix + StringHelper.nonceStr();
        UnifiedOrderModel params = new UnifiedOrderModel(openId,
                orderNo, 1, "body",
                remoteIp, notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        Assert.assertTrue(response.isSuccess());
        Order order = (Order) response.getData();
        Assert.assertEquals(orderNo, order.getOrderNo());
    }

    @Test
    public void testQuery() throws RequestFailedException {
        String orderNo = orderPrefix + StringHelper.nonceStr();
        UnifiedOrderModel params = new UnifiedOrderModel(openId,
                orderNo, 1, "body",
                remoteIp, notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        Assert.assertTrue(response.isSuccess());
        Response queryRes = this.payment.query(orderNo);
        Assert.assertTrue(queryRes.isSuccess());
        Order order = (Order) queryRes.getData();
        Assert.assertEquals(orderNo, order.getOrderNo());
    }

    @Test
    public void testCancelOrder() throws JsonProcessingException, RequestFailedException {

        String orderNo = orderPrefix + StringHelper.nonceStr();
        UnifiedOrderModel params = new UnifiedOrderModel(openId,
                orderNo, 1, "body",
                remoteIp, notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        Assert.assertTrue(response.isSuccess());
        Order order = (Order) response.getData();
        Assert.assertEquals(orderNo, order.getOrderNo());

        Response cancelRes = this.payment.cancel(orderNo);
        Assert.assertTrue(cancelRes.isSuccess());
    }

    @Test
    public void testRefund() throws RequestFailedException {
        String orderNo = orderPrefix + StringHelper.nonceStr();
        UnifiedOrderModel params = new UnifiedOrderModel(openId,
                orderNo, 1, "body",
                remoteIp, notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        Assert.assertTrue(response.isSuccess());

        Response refundRes = this.payment.refund(orderNo);
        Assert.assertTrue(refundRes.isSuccess());
    }

}
