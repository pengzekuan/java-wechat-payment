package com.km.peter.payment.service;

import com.km.peter.http.Response;
import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.HstyPay;
import com.km.peter.payment.enums.TradeStatus;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.StringHelper;
import com.km.peter.payment.util.XMLUtil;
import com.km.peter.payment.vo.UnifiedOrderVO;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 汇旺财支付接口
 */
public class HstyPayService extends AbstractPayment {

    public static final String VERSION = "2.0";

    private static final String REQUEST_URI = "https://pay.hstypay.com/v2/pay/gateway";

    private static final String CONTENT_TYPE = "application/xml";

    private static final String UNIFIED_ORDER_SERVICE = "pay.weixin.jspay";

    private static final String CANCEL_ORDER_SERVICE = "unified.trade.close";

    private static final String REFUND_ORDER_SERVICE = "unified.trade.refund";

    private static final String QUERY_ORDER_SERVICE = "unified.trade.query";

    private static final String QUERY_REFUND_ORDER_SERVICE = "unified.trade.refundquery";

    public HstyPayService(String wechatAppId, String merchantId, String key) {
        super(wechatAppId, merchantId, key);
        this.unifiedURI = REQUEST_URI;
        this.cancelURI = REQUEST_URI;
        this.queryURI = REQUEST_URI;
        this.refundURI = REQUEST_URI;
        this.scanPayURI = REQUEST_URI;
        this.refundQueryURI = REQUEST_URI;
        this.contentType = CONTENT_TYPE;
        this.header.put("Content-Type", CONTENT_TYPE);
    }

    private static Map<String, Object> response2Map(Response response) throws RequestFailedException {
        if (response == null || !response.isSuccess()) {
            throw new RequestFailedException("REQUEST_ERROR");
        }

        Map<String, Object> map = XMLUtil.xml2Map(String.valueOf(response.getData()));

        Map<String, Object> res = new HashMap<>();

        if (!"0".equals(String.valueOf(map.get("status")))) {
            res.put("message", map.get("message"));
            return res;
        }

        if (!"0".equals(String.valueOf(map.get("result_code")))) {
            res.put("message", map.get("err_code") + ":" + map.get("err_msg"));
            return res;
        }

        res.putAll(map);
        return res;
    }

    @Override
    protected Object paramConvertor(Map<String, Object> params) {
        System.out.println(XMLUtil.map2XmlString(params));
        return XMLUtil.map2XmlString(params);
    }

    @Override
    public Response unifiedOrder(UnifiedOrderModel params) throws RequestFailedException {
        params.setService(UNIFIED_ORDER_SERVICE);
        try {
            Response response = this.unifiedOrder(params, HstyPay.class);

            if (response == null || !response.isSuccess()) {
                throw new RequestFailedException("REQUEST_ERROR");
            }

            Map<String, Object> map = response2Map(response);

            if (map.containsKey("message")) {
                return new Response("REQUEST_FAILED", String.valueOf(map.get("message")));
            }

            UnifiedOrderVO vo = new UnifiedOrderVO();
            vo.setAmount(params.getTotalFee());
            vo.setOrderNo(params.getOrderNo());
            vo.setTransStatus(TradeStatus.NOTPAY.getKey());
            vo.setPayInfo(String.valueOf(map.get("pay_info")));
            return new Response(vo);
        } catch (NoSuchMethodException | FieldMissingException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e);
        }

        return new Response("UNKNOWN_ERROR", "未知错误");
    }

    @Override
    public Response cancel(String orderNo) throws RequestFailedException {
        Map<String, Object> params = new HashMap<>();
        params.put("service", CANCEL_ORDER_SERVICE);
        params.put("version", VERSION);
        params.put("charset", CHARSET);
        params.put("sign_type", SIGN_TYPE);
        params.put("mch_id", this.merchantId);
        params.put("out_trade_no", orderNo);
        params.put("nonce_str", StringHelper.nonceStr());
        params.put("sign", this.sign(params));
        Response response = this.post(this.cancelURI, params);

        Map<String, Object> map = response2Map(response);

        if (map.containsKey("message")) {
            return new Response("REQUEST_FAILED", String.valueOf(map.get("message")));
        }

        return new Response(null);
    }
}
