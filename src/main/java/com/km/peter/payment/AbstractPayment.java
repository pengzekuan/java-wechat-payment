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
import com.km.peter.payment.service.AllinPayService;
import com.km.peter.payment.service.HstyPayService;
import com.km.peter.payment.util.XMLUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AbstractPayment implements Payment {

    public static final String CHARSET = "UTF-8";
    public static final String SIGN_TYPE = "MD5";

    protected boolean debug = false;

    protected Map<String, String> header;

    protected String contentType;
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

    private Request request;

    public AbstractPayment(String wechatAppId, String merchantId, String key) {
        this.wechatAppId = wechatAppId;
        this.merchantId = merchantId;
        this.key = key;
        this.request = RequestFactory.instance(HTTPClientRequest.class);
        this.header = new HashMap<>();
    }

    public static Response paymentNotify(HttpServletRequest request) {
        String contentType = request.getContentType();

        Map<String, Object> map = new HashMap<>();

        if (contentType.startsWith(HstyPayService.CONTENT_TYPE)) {
            StringBuilder stringBuffer = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
                String tmpStr;
                while ((tmpStr = reader.readLine()) != null) {
                    stringBuffer.append(tmpStr);
                }
                System.out.println("stream:" + stringBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    Objects.requireNonNull(reader).close();
                } catch (IOException e) {
                    System.err.println(e);
                }

            }
            map = XMLUtil.xml2Map(stringBuffer.toString());

            return HstyPayService.paymentNotify(map);

        }

        if (contentType.startsWith(AllinPayService.CONTENT_TYPE)) {

            Enumeration<String> keys = request.getParameterNames();
            if (keys == null) {
                return null;
            }
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                map.put(key, request.getParameter(key));
            }

            return AllinPayService.paymentNotify(map);
        }

        return null;
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
                    if (this.debug && Arrays.asList(new String[]{"wechatAppId", "openId"}).contains(fieldName)) {
                        continue;
                    }
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

    protected Map<String, Object> unifiedOrder(UnifiedOrderModel params,
                                               Class<? extends Annotation> annotationClass) throws NoSuchMethodException,
            FieldMissingException, IllegalAccessException, InvocationTargetException, RequestFailedException {
        params.setWechatAppId(this.wechatAppId);
        params.setMerchantId(this.merchantId);
        return this.post(this.unifiedURI, this.getUnifiedParams(params, annotationClass));
    }

    protected Map<String, Object> response2Map(Response response) throws RequestFailedException {
        return new HashMap<>();
    }

    protected Map<String, Object> post(String url, Map<String, Object> params) throws RequestFailedException {
        return this.response2Map(this.request.post(url, null, this.paramConvertor(params), this.header));
    }

    @Override
    public Response unifiedOrder(UnifiedOrderModel params) throws RequestFailedException {
        return null;
    }

    @Override
    public Response cancel(String orderNo) throws RequestFailedException {
        return null;
    }

    @Override
    public Response refund(String orderNo) throws RequestFailedException {
        return null;
    }

    @Override
    public Response refund(String orderNo, int amount) throws RequestFailedException {
        return null;
    }

    @Override
    public Response query(String orderNo) throws RequestFailedException {
        return null;
    }
}
