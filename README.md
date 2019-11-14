## 微信支付接口

### 接口集成以下平台支付通道

- 通联收银宝
- 汇商通支付
- 微信官方支付

### 初始化

```
Payment payment
# 通联
payment = new AllinPayService(wechatAppId, merchantId, applicationId, paymentKey);
# 汇商通
payment = new HstyPayService(wechatAppId, merchantId, paymentKey);
# 微信官方
payment = new WechatService(wechatAppId, merchantId, paymentKey); // 暂不可用
```

### 接口列表

- `unifiedOrder(UnifiedOrderModel params) `

  统一下单

  ```java
  UnifiedOrderModel params = new UnifiedOrderModel(openId, orderNo, amount, "body",
                  remoteIp, notifyUrl);
  Response response = payment.unifiedOrder(params);
  Order order = (Order) response.getData();
  ```

- `cancel(String orderNo)`

- `refund(String orderNo)`

- `refund(String orderNo, int amount)`

- `query(String orderNo)`

- `paymentNotify(Object response)`

### 开发计划

- 微信官方接口实现
- 小程序支付开通
- `APP`支付
- 付款码、扫码支付(`Native`)
- 支付宝、`QQ`钱包、云闪付




