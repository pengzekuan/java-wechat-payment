package com.km.peter.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.km.peter.http.Response;
import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.WechatPay;
import com.km.peter.payment.enums.PaymentScene;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.param.UnifiedOrderModel;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信原生支付
 */
public class WechatPayService extends AbstractPayment {

    private static final String REQUEST_URI = "https://api.mch.weixin.qq.com/pay/";

    /**
     * 支付场景
     *
     * @see PaymentScene
     */
    private PaymentScene paymentScene;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用标识
     * IOS bundle_id
     * android package_name
     * wap wap_url
     */
    private String identification;

    /**
     * @param wechatAppId    公众号appId
     * @param merchantId     商户号
     * @param key            支付密钥
     * @param paymentScene   支付场景值
     * @param appName        应用名称
     * @param identification 应用标识
     *                       IOS bundle_id
     *                       android package_name
     *                       wap wap_url
     */
    public WechatPayService(String wechatAppId, String merchantId, String key, PaymentScene paymentScene, String appName,
                            String identification) {
        super(wechatAppId, merchantId, key);
        this.paymentScene = paymentScene;
        this.appName = appName;
        this.identification = identification;
        this.unifiedURI = REQUEST_URI + "unifiedorder";
        this.cancelURI = REQUEST_URI + "closeorder";
        this.queryURI = REQUEST_URI + "orderquery";
        this.refundURI = REQUEST_URI + "refund";
        this.scanPayURI = REQUEST_URI + "unifiedorder";
        this.refundQueryURI = REQUEST_URI + "refundquery";
    }

    @Override
    public Response unifiedOrder(UnifiedOrderModel params) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNodes = mapper.createObjectNode();
        ObjectNode root = mapper.createObjectNode();
        root.put("type", this.paymentScene.getKey());
        root.put(this.paymentScene.getAppNameField(), this.appName);
        root.put(this.paymentScene.getIdentificationField(), this.identification);
        jsonNodes.set("h5_info", root);

        try {
            params.setSceneInfo("fawefawefaw");
//            params.setSceneInfo(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNodes));
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/xml");
            return this.unifiedOrder(params, header, WechatPay.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (FieldMissingException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
