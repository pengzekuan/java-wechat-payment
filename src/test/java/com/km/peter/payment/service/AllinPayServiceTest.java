package com.km.peter.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.km.peter.http.Response;
import com.km.peter.payment.Payment;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.StringHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class AllinPayServiceTest {

    private String openId = "oWZkgwp5MfVg6lvRuvNquWr2MjGA";

    private String prefix = "test_";

    private String remoteIp = "127.0.0.1";

    private String notifyUrl = "http://notify";

    private Payment payment;

    @Before
    public void before() {
        this.payment = new AllinPayService("wx539072349149ec78", "560731073991L7U", "00171161", "34f5540b12a34b8e80fccfe4dcce0d77");

        System.out.println("currentTime:" + System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        System.out.println("nextTime:" + calendar.getTimeInMillis());
    }

    @Test
    public void testUnifiedOrder() throws RequestFailedException, JsonProcessingException {

        UnifiedOrderModel params = new UnifiedOrderModel(this.openId, this.prefix + StringHelper.nonceStr(), 1, "body",
                this.remoteIp, this.notifyUrl);
        Response response = this.payment.unifiedOrder(params);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void testQuery() throws RequestFailedException {
        Response response = this.payment.query("orderNo");
        Assert.assertTrue(response.isSuccess());
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
