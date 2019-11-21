package com.km.peter.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.km.peter.http.Response;
import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.AllinPay;
import com.km.peter.payment.enums.PayStatus;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.model.Order;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.StringHelper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 通联支付接口
 */
public class AllinPayService extends AbstractPayment {

    public static final String VERSION = "11";
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String REQUEST_URI = "https://vsp.allinpay.com/apiweb/unitorder/";
    private String applicationId;

    public AllinPayService(String wechatAppId, String merchantId, String applicationId, String key) {
        super(wechatAppId, merchantId, key);
        this.applicationId = applicationId;
        this.unifiedURI = REQUEST_URI + "pay";
        this.cancelURI = REQUEST_URI + "cancel";
        this.queryURI = REQUEST_URI + "query";
        this.refundURI = REQUEST_URI + "refund";
        this.scanPayURI = REQUEST_URI + "scanqrpay";
        this.refundQueryURI = REQUEST_URI + "query";
        this.header.put("Content-Type", CONTENT_TYPE);
    }

    public static Response paymentNotify(Map<String, Object> map) {

        String orderNo = String.valueOf(map.get("cusorderid"));

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setMerchantId(map.get("cusid") + "|" + map.get("appid"));
        order.setOpenId(String.valueOf(map.get("acct")));
        order.setTransNo(String.valueOf(map.get("trxid")));
        order.setAmount(Integer.valueOf(String.valueOf(map.get("trxamt"))));
        order.setPayTime(String.valueOf(map.get("paytime")));
        order.setTradeStatus(PayStatus.SUCCESS.getKey());

        return new Response(order);
    }

    @Override
    protected String sign(Map<String, Object> map) {

        Map<String, Object> mapCopy = new TreeMap<>(String::compareTo);
        mapCopy.putAll(map);
        mapCopy.put("key", this.key);

        StringBuilder builder = new StringBuilder();

        // 拼接
        for (String key : mapCopy.keySet()) {
            builder.append(key).append("=").append(mapCopy.get(key)).append("&");
        }

        builder.deleteCharAt(builder.length() - 1);

        return md5(builder).toString().toUpperCase();
    }

    @Override
    protected Object paramConvertor(Map<String, Object> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : params.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
        }

        System.out.println("---" + nameValuePairs);

        return nameValuePairs;
    }

    @Override
    public Response unifiedOrder(UnifiedOrderModel params) throws RequestFailedException {
        params.setApplicationId(this.applicationId);

        try {
            Map<String, Object> res = this.unifiedOrder(params, AllinPay.class);

            if (res.containsKey("message")) {
                return new Response("", String.valueOf(res.get("message")));
            }

            String orderNo = String.valueOf(res.get("reqsn"));
            String transNo = String.valueOf(res.get("trxid"));
            String payInfo = String.valueOf(res.get("payinfo"));
            Order vo = new Order();
            vo.setAmount(params.getTotalFee());
            vo.setOrderNo(orderNo);
            vo.setTransNo(transNo);
            vo.setPayInfo(payInfo);
            vo.setTradeStatus(PayStatus.NOTPAY.getKey());
            return new Response(vo);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | FieldMissingException e) {
            System.err.println(e);
        }

        return new Response("UNKNOWN_ERROR", "未知错误");
    }

    @Override
    public Response cancel(String orderNo) throws RequestFailedException {
        Map<String, Object> response = this.post(this.cancelURI, this.queryParams(orderNo, null));

        if (response.containsKey("message")) {
            return new Response("", String.valueOf(response.get("message")));
        }

        return new Response(null);
    }

    @Override
    public Response refund(String orderNo, int amount) throws RequestFailedException {
        Map<String, Object> params = new HashMap<>();
        params.put("trxamt", amount);
        Map<String, Object> response = this.post(this.refundURI, this.queryParams(orderNo, params));

        if (response.containsKey("message")) {
            return new Response("", String.valueOf(response.get("message")));
        }

        return new Response(null);
    }

    private Map<String, Object> queryParams(String orderNo, Map<String, Object> params) {

        if (params == null) {
            params = new HashMap<>();
        }

        params.put("cusid", this.merchantId);
        params.put("appid", this.applicationId);
        params.put("version", VERSION);
        params.put("reqsn", orderNo);
        params.put("randomstr", StringHelper.nonceStr());
        params.put("sign", this.sign(params));
        return params;
    }

    @Override
    public Response query(String orderNo) throws RequestFailedException {

        Map<String, Object> response = this.post(this.queryURI, this.queryParams(orderNo, null));

        if (response.containsKey("message")) {
            return new Response("", String.valueOf(response.get("message")));
        }

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setMerchantId(response.get("cusid") + "|" + response.get("appid"));
        order.setTradeStatus(String.valueOf(response.get("payStatus")));

        order.setOpenId(String.valueOf(response.get("acct")));
        order.setTransNo(String.valueOf(response.get("trxid")));
        order.setAmount(Integer.valueOf(String.valueOf(response.get("trxamt"))));
        order.setAttach(String.valueOf(response.get("attach")));
        order.setPayTime(String.valueOf(response.get("fintime")));

        return new Response(order);

    }

    @Override
    protected Map<String, Object> response2Map(Response response) throws RequestFailedException {
        Map<String, Object> res = new HashMap<>();

        if (response == null || !response.isSuccess()) {
            throw new RequestFailedException("REQUEST_ERROR");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> objectMap = mapper.readValue(response.getBytes(), HashMap.class);

            if (!"SUCCESS".equals(String.valueOf(objectMap.get("retcode")))) {
                res.put("message", String.valueOf(objectMap.get("retcode")));
                return res;
            }

            String status = String.valueOf(objectMap.get("trxstatus"));

            if ("1001".equals(status)) {
                res.put("message", "交易不存在");
                return res;
            }

            if (status.startsWith("3")) {
                res.put("message", objectMap.get("trxstatus") + ":" + objectMap.get("errmsg"));
                return res;
            }

            String tradeType = String.valueOf(objectMap.get("trxcode"));

            if (!"0000".equals(status)) {
                res.put("message", status + ":" + objectMap.get("errmsg"));
                return res;
            }

            String payStatus;

            switch (tradeType) {
                case "VSP501":
                    payStatus = PayStatus.SUCCESS.getKey();
                    break;
                case "VSP503":
                    payStatus = PayStatus.REFUND.getKey();
                    break;
                default:
                    payStatus = PayStatus.CLOSED.getKey();
                    break;
            }

            res.putAll(objectMap);
            res.put("payStatus", payStatus);
        } catch (IOException e) {
            System.err.println(e);

        }
        return res;
    }
}
