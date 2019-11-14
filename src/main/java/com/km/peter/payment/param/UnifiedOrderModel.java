package com.km.peter.payment.param;

import com.km.peter.payment.AbstractPayment;
import com.km.peter.payment.annotation.AllinPay;
import com.km.peter.payment.annotation.HstyPay;
import com.km.peter.payment.annotation.WechatPay;
import com.km.peter.payment.service.AllinPayService;
import com.km.peter.payment.service.HstyPayService;
import com.km.peter.payment.util.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 下单接口参数
 */
public class UnifiedOrderModel {

    /**
     * 订单默认有效期30分钟
     */
    private static final int EXPIRE_IN = 30;

    /**
     * 接口类型
     */
    @HstyPay(value = "service", defaultValue = "pay.weixin.jspay")
    private String service;
    /**
     * 版本号
     */
    @HstyPay(value = "version", defaultValue = HstyPayService.VERSION)
    @AllinPay(value = "version", defaultValue = AllinPayService.VERSION)
    private String version;
    /**
     * 字符集
     */
    @HstyPay(value = "charset", defaultValue = HstyPayService.CHARSET)
    private String charset;
    /**
     * 原生JS
     */
    @HstyPay(value = "is_raw", defaultValue = "1", length = 1)
    private String isRaw;
    /**
     * 1 小程序支付
     */
    @HstyPay(value = "is_minipg", defaultValue = "2")
    private String isMini;
    /**
     * 微信公众号appId
     */
    @WechatPay(value = "appid")
    @HstyPay(value = "sub_appid")
    @AllinPay(value = "sub_appid")
    private String wechatAppId;
    /**
     * 支付商户号id
     */
    @WechatPay(value = "mch_id")
    @HstyPay(value = "mch_id")
    @AllinPay(value = "cusid", length = 15)
    private String merchantId;
    /**
     * 平台分配应用ID
     */
    @AllinPay(value = "appid", length = 8)
    private String applicationId;
    /**
     * 终端设备号（门店或收银设备ID）
     * PC网页或公众号支付，默认 ”WEB“
     */
    @WechatPay(value = "device_info", defaultValue = "WEB")
    @HstyPay(value = "device_info", defaultValue = "WEB")
    private String deviceInfo;
    /**
     * 签名
     */
    @WechatPay(value = "sign")
    @HstyPay(value = "sign", length = 344)
    @AllinPay(value = "sign")
    private String sign;
    /**
     * 签名类型
     * 默认： MD5
     */
    @WechatPay(value = "sign_type", defaultValue = AbstractPayment.SIGN_TYPE)
    @HstyPay(value = "sign_type", defaultValue = AbstractPayment.SIGN_TYPE)
    @AllinPay(value = "signtype", defaultValue = AbstractPayment.SIGN_TYPE)
    private String signType;
    /**
     * 商品描述
     */
    @WechatPay(value = "body", length = 128)
    @HstyPay(value = "body", length = 127)
    @AllinPay(value = "body", length = 100, required = false)
    private String body;
    /**
     * 随机字串
     */
    @WechatPay(value = "nonce_str")
    @HstyPay(value = "nonce_str")
    @AllinPay(value = "randomstr")
    private String nonceStr;
    /**
     * 商品详情
     * 暂不支持
     */
    private String detail;
    /**
     * 附加数据，自定义
     */
    @WechatPay(value = "attach", length = 127, required = false)
    @HstyPay(value = "attach", required = false)
    @AllinPay(value = "remark", required = false)
    private String attach;
    /**
     * 平台订单号
     */
    @WechatPay(value = "out_trade_no")
    @HstyPay(value = "out_trade_no")
    @AllinPay(value = "reqsn")
    private String orderNo;
    /**
     * 货币类型
     * 默认：CNY
     */
    @WechatPay(value = "fee_type", defaultValue = "CNY")
    private String feeType;
    /**
     * 订单总金额
     */
    @WechatPay(value = "total_fee")
    @HstyPay(value = "total_fee")
    @AllinPay(value = "trxamt")
    private int totalFee;
    /**
     * 终端IP，平台服务器IP
     */
    @WechatPay(value = "spbill_create_ip")
    @HstyPay(value = "mch_create_ip")
    @AllinPay(value = "cusip")
    private String remoteIp;
    /**
     * 交易时间
     * 格式：yyyyMMddHHmmss
     */
    @WechatPay(value = "time_start", length = 14)
    @HstyPay(value = "time_start", length = 14)
    private String transTime;
    /**
     * 交易过期时间
     */
    @WechatPay(value = "time_expire", length = 14)
    @HstyPay(value = "time_expire", length = 14)
    private String expireTime;

    @AllinPay(value = "validtime", length = 4, defaultValue = "5") // 单位：分， 最大1440分钟
    private int validTime;
    /**
     * 商品标记
     * 默认：WXG
     */
    @WechatPay(value = "goods_tag", required = false)
    @HstyPay(value = "goods_tag", required = false)
    @AllinPay(value = "goods_tag", required = false)
    private String goodsTag;
    /**
     * 支付通知地址
     */
    @WechatPay(value = "notify_url", length = 256)
    @HstyPay(value = "notify_url", length = 256)
    @AllinPay(value = "notify_url", length = 256)
    private String notifyUrl;
    /**
     * 支付跳转地址
     */
    @HstyPay(value = "callback_url", length = 255, required = false)
    private String callbackUrl;
    /**
     * 交易类型
     * 默认：MWEB H5支付
     */
    @WechatPay(value = "trade_type", defaultValue = "MWEB")
    @AllinPay(value = "paytype", defaultValue = "W02")
    private String tradeType;
    /**
     * 商品ID
     * tradeType=NATIVE必传
     */
    @WechatPay(value = "product_id", required = false)
    private String productId;
    /**
     * 支付方式限定
     * no_credit--指定不能使用信用卡支付
     */
    @WechatPay(value = "limit_pay", defaultValue = "no_credit")
    @HstyPay(value = "limit_credit_pay", defaultValue = "1")
    @AllinPay(value = "limit_pay", defaultValue = "no_credit")
    private String limitPay;
    /**
     * 下单用户标识
     * tradeType=JSAPI必传
     */
    @WechatPay(value = "openid", length = 128)
    @HstyPay(value = "sub_openid", length = 128)
    @AllinPay(value = "acct")
    private String openId;
    /**
     * 电子发票开关
     * 默认：Y
     */
    @WechatPay(value = "receipt", defaultValue = "Y")
    private String receipt;
    /**
     * 场景信息
     * scene_info
     * 1，IOS移动应用
     * {"h5_info": //h5支付固定传"h5_info"
     * {"type": "",  //场景类型
     * "app_name": "",  //应用名
     * "bundle_id": ""  //bundle_id
     * }
     * }
     * <p>
     * 2，安卓移动应用
     * {"h5_info": //h5支付固定传"h5_info"
     * {"type": "",  //场景类型
     * "app_name": "",  //应用名
     * "package_name": ""  //包名
     * }
     * }
     * <p>
     * 3，WAP网站应用
     * {"h5_info": //h5支付固定传"h5_info"
     * {"type": "",  //场景类型
     * "wap_url": "",//WAP网站URL地址
     * "wap_name": ""  //WAP 网站名
     * }
     * }
     */
    @WechatPay(value = "scene_info", length = 256)
    private String sceneInfo;

    public UnifiedOrderModel() {
        init();
    }

    public UnifiedOrderModel(String openId, String orderNo, int amount, String body, String remoteIp, String notifyUrl) {
        init();
        this.openId = openId;
        this.orderNo = orderNo;
        this.totalFee = amount;
        if (body != null) {
            this.body = body;
        }
        this.remoteIp = remoteIp;
        this.notifyUrl = notifyUrl;
    }

    private void init() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        this.transTime = dateFormat.format(date);
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, EXPIRE_IN);
        this.expireTime = dateFormat.format(calendar.getTime());
        this.validTime = EXPIRE_IN;

        this.nonceStr = StringHelper.nonceStr();
        this.body = "暂无说明";
    }

    public int getValidTime() {
        return validTime;
    }

    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }

    public String getWechatAppId() {
        return wechatAppId;
    }

    public void setWechatAppId(String wechatAppId) {
        this.wechatAppId = wechatAppId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLimitPay() {
        return limitPay;
    }

    public void setLimitPay(String limitPay) {
        this.limitPay = limitPay;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getSceneInfo() {
        return sceneInfo;
    }

    public void setSceneInfo(String sceneInfo) {
        this.sceneInfo = sceneInfo;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getIsRaw() {
        return isRaw;
    }

    public void setIsRaw(String isRaw) {
        this.isRaw = isRaw;
    }

    public String getIsMini() {
        return isMini;
    }

    public void setIsMini(String isMini) {
        this.isMini = isMini;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
