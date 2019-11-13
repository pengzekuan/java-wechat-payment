package com.km.peter.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.km.peter.http.Response;
import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.AllinPay;
import com.km.peter.payment.enums.TradeStatus;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.vo.UnifiedOrderVO;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 通联支付接口
 */
public class AllinPayService extends AbstractPayment {

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
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        try {
            Response response = this.unifiedOrder(params, header, AllinPay.class);
            if (response == null || !response.isSuccess()) {
                throw new RequestFailedException("REQUEST_ERROR");
            }

            ObjectMapper mapper = new ObjectMapper();
            HashMap res = mapper.readValue(String.valueOf(response.getData()), HashMap.class);
            String retCode = String.valueOf(res.get("retcode"));
            if ("SUCCESS".equals(retCode) && "0000".equals(String.valueOf(res.get("trxstatus")))) {

                String orderNo = String.valueOf(res.get("reqsn"));
                String transNo = String.valueOf(res.get("trxid"));
                String payInfo = String.valueOf(res.get("payinfo"));
                UnifiedOrderVO vo = new UnifiedOrderVO();
                vo.setAmount(params.getTotalFee());
                vo.setOrderNo(orderNo);
                vo.setTransNo(transNo);
                vo.setPayInfo(payInfo);
                vo.setTransStatus(TradeStatus.NOTPAY.getKey());
                return new Response(vo);
            }
            return new Response("REQUEST_FAILED:",
                    res.get("retmsg") + ";" + res.get("errmsg"));

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | JsonProcessingException | FieldMissingException e) {
            System.err.println(e);
        }

        return new Response("UNKNOWN_ERROR", "未知错误");
    }
}
