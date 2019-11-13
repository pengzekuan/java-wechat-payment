package com.km.peter.payment.service;

import com.km.peter.http.Response;
import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.HstyPay;
import com.km.peter.payment.enums.TradeStatus;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.XMLUtil;
import com.km.peter.payment.vo.UnifiedOrderVO;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 汇旺财支付接口
 */
public class HstyPayService extends AbstractPayment {

    private static final String REQUEST_URI = "https://pay.hstypay.com/v2/pay/gateway";

    public HstyPayService(String wechatAppId, String merchantId, String key) {
        super(wechatAppId, merchantId, key);
        this.unifiedURI = REQUEST_URI;
        this.cancelURI = REQUEST_URI;
        this.queryURI = REQUEST_URI;
        this.refundURI = REQUEST_URI;
        this.scanPayURI = REQUEST_URI;
        this.refundQueryURI = REQUEST_URI;
    }

    @Override
    protected Object paramConvertor(Map<String, Object> params) {
        System.out.println(XMLUtil.map2XmlString(params));
        return XMLUtil.map2XmlString(params);
    }

    @Override
    public Response unifiedOrder(UnifiedOrderModel params) throws RequestFailedException {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/xml");
        try {
            Response response = this.unifiedOrder(params, header, HstyPay.class);

            if (response == null || !response.isSuccess()) {
                throw new RequestFailedException("REQUEST_ERROR");
            }

            Map<String, Object> map = XMLUtil.xml2Map(String.valueOf(response.getData()));

            if ("0".equals(String.valueOf(map.get("status"))) && "0".equals(String.valueOf(map.get("result_code")))) {
                UnifiedOrderVO vo = new UnifiedOrderVO();
                vo.setAmount(params.getTotalFee());
                vo.setOrderNo(params.getOrderNo());
                vo.setTransStatus(TradeStatus.NOTPAY.getKey());
                vo.setPayInfo(String.valueOf(map.get("pay_info")));
                return new Response(vo);
            }
            return new Response("REQUEST_FAILED", map.get("message") + ";" + map.get("err_msg"));
        } catch (NoSuchMethodException | FieldMissingException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e);
        }

        return new Response("UNKNOWN_ERROR", "未知错误");
    }
}
