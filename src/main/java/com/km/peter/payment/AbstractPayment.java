package com.km.peter.payment;

import com.km.peter.http.HTTPClientRequest;
import com.km.peter.http.Request;
import com.km.peter.http.RequestFactory;
import com.km.peter.http.Response;
import com.km.peter.payment.annotation.AllinPay;
import com.km.peter.payment.annotation.HstyPay;
import com.km.peter.payment.annotation.WechatPay;
import com.km.peter.payment.exception.FieldMissingException;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;
import com.km.peter.payment.util.XMLUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AbstractPayment implements Payment {

    /**
     * 统一下单接口
     */
    protected String unifiedURI;
    /**
     * 扫码支付接口
     */
    protected String scanPayURI;
    /**
     * 退款接口
     */
    protected String refundURI;
    /**
     * 取消订单接口
     */
    protected String cancelURI;
    /**
     * 订单查询接口
     */
    protected String queryURI;

    /**
     * 退款单查询接口
     */
    protected String refundQueryURI;

    /**
     * 公众号appId
     */
    protected String wechatAppId;

    /**
     * 商户号
     */
    protected String merchantId;

    /**
     * 商户支付密钥
     */
    protected String key;

    public AbstractPayment(String wechatAppId, String merchantId, String key) {
        this.wechatAppId = wechatAppId;
        this.merchantId = merchantId;
        this.key = key;
    }

    protected final Map<String, Object> getUnifiedParams(UnifiedOrderModel model,
                                                         Class<? extends Annotation> annotationClass)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
            FieldMissingException {

        Map<String, Object> map = new HashMap<>();

        Class<?> clz = model.getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();

            field.setAccessible(true);
            boolean required = false;
            String realField = null;
            String defaultValue = "";
            String fieldType = null;

            if ("sign".equals(fieldName)) {
                continue;
            }

            if (annotationClass.equals(HstyPay.class) && field.isAnnotationPresent(HstyPay.class)) {
                HstyPay annotation = field.getDeclaredAnnotation(HstyPay.class);
                required = annotation.required();
                realField = annotation.value();
                defaultValue = annotation.defaultValue();
                fieldType = annotation.type();
            }

            if (annotationClass.equals(AllinPay.class) && field.isAnnotationPresent(AllinPay.class)) {
                AllinPay annotation = field.getDeclaredAnnotation(AllinPay.class);
                required = annotation.required();
                realField = annotation.value();
                defaultValue = annotation.defaultValue();
                fieldType = annotation.type();
            }

            if (annotationClass.equals(WechatPay.class) && field.isAnnotationPresent(WechatPay.class)) {
                WechatPay annotation = field.getDeclaredAnnotation(WechatPay.class);
                required = annotation.required();
                realField = annotation.value();
                defaultValue = annotation.defaultValue();
                fieldType = annotation.type();
            }

            if (realField == null) {
                continue;
            }

            String getMethodStr = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            Method getter = clz.getDeclaredMethod(getMethodStr);

            Object value = getter.invoke(model);

            value = value != null ? value : defaultValue;

            if ("".equals(String.valueOf(value)) || "0".equals(String.valueOf(value))) {
                if (required) {
                    throw new FieldMissingException(realField + " is required");
                }
                continue;
            }

            if (fieldType.equals(int.class.getSimpleName().toLowerCase())) {
                map.put(realField, Integer.valueOf(String.valueOf(value)));
            } else {
                map.put(realField, String.valueOf(value));
            }
        }

        // 签名
        map.put("sign", sign(map));

        return map;
    }

    protected String sign(Map<String, Object> map) {

        Map<String, Object> mapCopy = new TreeMap<>(String::compareTo);
        mapCopy.putAll(map);

        StringBuilder builder = new StringBuilder();

        // 拼接
        for (String key : mapCopy.keySet()) {
            builder.append(key).append("=").append(mapCopy.get(key)).append("&");
        }

        builder.append("key=").append(this.key);

        return md5(builder).toString().toUpperCase();
    }

    protected StringBuilder md5(StringBuilder builder) {
        // md5加密
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(builder.toString().getBytes(StandardCharsets.UTF_8));
            byte[] bytes = digest.digest();

            builder.setLength(0);
            for (byte b : bytes) {
                if ((b & 0xff) < 0x10) {
                    builder.append(0);
                }
                builder.append(Integer.toHexString(b & 0xff));
            }

            // 字符转换为大写
            return builder;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected Object paramConvertor(Map<String, Object> params) {
        return XMLUtil.map2XmlString(params);
    }

    protected Response unifiedOrder(UnifiedOrderModel params, Map<String, String> header,
                                    Class<? extends Annotation> annotationClass) throws NoSuchMethodException, FieldMissingException,
            IllegalAccessException, InvocationTargetException {
        params.setWechatAppId(this.wechatAppId);
        params.setMerchantId(this.merchantId);
        Request request = RequestFactory.instance(HTTPClientRequest.class);
        return request.post(this.unifiedURI, null,
                this.paramConvertor(this.getUnifiedParams(params, annotationClass)), header);
    }

    @Override
    public Response unifiedOrder(UnifiedOrderModel params) throws RequestFailedException {
//        return this.unifiedOrder(this.unifiedURI, params);
        return null;
    }

    @Override
    public Response cancel(String orderNo) {
        return null;
    }

    @Override
    public Response refund(String orderNo) {
        return null;
    }

    @Override
    public Response refund(String orderNo, int amount) {
        return null;
    }

    @Override
    public Response query(String orderNo) {
        return null;
    }

    @Override
    public Object paymentNotify(Object response) {
        return null;
    }
}
