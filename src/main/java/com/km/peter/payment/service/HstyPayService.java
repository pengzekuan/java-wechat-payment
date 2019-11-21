package com.km.peter.payment.service;

import com.km.peter.http.Response;
import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.HstyPay;
import com.km.peter.payment.enums.PayStatus;
import com.km.peter.payment.enums.RefundStatus;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.model.Order;
import com.km.peter.payment.model.RefundOrder;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.StringHelper;
import com.km.peter.payment.util.XMLUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇旺财支付接口
 */
public class HstyPayService extends AbstractPayment {

    public static final String VERSION = "2.0";

    public static final String CONTENT_TYPE = "application/xml";

    private static final String REQUEST_URI = "https://pay.hstypay.com/v2/pay/gateway";

    private static final String REFUND_CHANNEL = "ORIGINAL";

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
        this.debug = true;
    }

    public static Response paymentNotify(Map<String, Object> map) {

        if (!"0".equals(map.get("pay_result"))) {
            return new Response("PAYMENT_FAILED", String.valueOf(map.get("pay_info")));
        }

        String orderNo = String.valueOf(map.get("out_trade_no"));

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setWechatAppId(String.valueOf(map.get("sub_appid")));
        order.setOpenId(String.valueOf(map.get("sub_openid")));
        order.setTransNo(String.valueOf(map.get("transaction_id")));
        order.setAmount(Integer.valueOf(String.valueOf(map.get("total_fee"))));
        order.setAttach(String.valueOf(map.get("attach")));
        order.setPayTime(String.valueOf(map.get("time_end")));
        order.setTradeStatus(PayStatus.SUCCESS.getKey());

        return new Response(order);
    }

    @Override
    protected Map<String, Object> response2Map(Response response) throws RequestFailedException {
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

    private Map<String, Object> queryParams(String orderNo, String service, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("service", service);
        params.put("version", VERSION);
        params.put("charset", CHARSET);
        params.put("sign_type", SIGN_TYPE);
        params.put("mch_id", this.merchantId);
        params.put("out_trade_no", orderNo);
        params.put("nonce_str", StringHelper.nonceStr());
        params.put("sign", this.sign(params));
        return params;
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
            Map<String, Object> map = this.unifiedOrder(params, HstyPay.class);

            if (map.containsKey("message")) {
                return new Response("REQUEST_FAILED", String.valueOf(map.get("message")));
            }

            Order vo = new Order();
            vo.setAmount(params.getTotalFee());
            vo.setOrderNo(params.getOrderNo());
            vo.setTradeStatus(PayStatus.NOTPAY.getKey());
            vo.setPayInfo(String.valueOf(map.get("pay_info")));
            return new Response(vo);
        } catch (NoSuchMethodException | FieldMissingException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e);
        }

        return new Response("UNKNOWN_ERROR", "未知错误");
    }

    @Override
    public Response cancel(String orderNo) throws RequestFailedException {

        Map<String, Object> map = this.post(this.cancelURI, queryParams(orderNo, CANCEL_ORDER_SERVICE, null));

        if (map.containsKey("message")) {
            return new Response("REQUEST_FAILED", String.valueOf(map.get("message")));
        }

        return new Response(null);
    }

    @Override
    public Response query(String orderNo) throws RequestFailedException {

        Map<String, Object> map = this.post(this.queryURI, queryParams(orderNo, QUERY_ORDER_SERVICE, null));

        if (map.containsKey("message")) {
            return new Response("REQUEST_FAILED", String.valueOf(map.get("message")));
        }
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setMerchantId(String.valueOf(map.get("mch_id")));
        order.setTradeStatus(String.valueOf(map.get("trade_state")));

        if (PayStatus.SUCCESS.getKey().equals(order.getTradeStatus())) {
            order.setOpenId(String.valueOf(map.get("openid")));
            order.setTransNo(String.valueOf(map.get("transaction_id")));
            order.setAmount(Integer.valueOf(String.valueOf(map.get("total_fee"))));
            order.setAttach(String.valueOf(map.get("attach")));
            order.setPayTime(String.valueOf(map.get("time_end")));
        }
        return new Response(order);
    }

    @Override
    public Response refund(String orderNo) throws RequestFailedException {
        return this.refund(orderNo, 0);
    }

    @Override
    public Response refund(String orderNo, int amount) throws RequestFailedException {

        Response queryRes = this.query(orderNo);

        Order order = (Order) queryRes.getData();

        if (!PayStatus.SUCCESS.getKey().equals(order.getTradeStatus())) {
            return new Response("OPERATION_FAILED", "订单状态不支持退款");
        }

        int totalFee = order.getAmount();

        int refundFee = amount > 0 ? amount : totalFee;

        String refundId = orderNo + StringHelper.nonceStr();

        Map<String, Object> params = new HashMap<>();
        params.put("out_refund_no", refundId);
        params.put("total_fee", totalFee);
        params.put("refund_fee", refundFee);
        params.put("op_user_id", this.merchantId);
        params.put("refund_channel", REFUND_CHANNEL);

        Map<String, Object> map = this.post(this.refundURI, this.queryParams(orderNo, REFUND_ORDER_SERVICE, params));

        if (map.containsKey("message")) {
            return new Response("REQUEST_FAILED", String.valueOf(map.get("message")));
        }

        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOpenId(order.getOpenId());
        refundOrder.setOrderNo(orderNo);
        refundOrder.setAmount(totalFee);
        refundOrder.setRefundFee(refundFee);
        refundOrder.setRefundChannel(REFUND_CHANNEL);
        refundOrder.setRefundId(refundId);
        refundOrder.setRefundStatus(RefundStatus.PROCESSING.getKey());
        refundOrder.setOutRefundId(String.valueOf(map.get("refund_id")));

        List<RefundOrder> refundOrders = new ArrayList<>();
        refundOrders.add(refundOrder);
        order.setRefundOrders(refundOrders);

        return new Response(order);
    }
}
