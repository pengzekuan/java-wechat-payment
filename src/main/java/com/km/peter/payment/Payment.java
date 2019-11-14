package com.km.peter.payment;

import com.km.peter.http.Response;
import com.km.peter.payment.exception.RequestFailedException;
import com.km.peter.payment.param.UnifiedOrderModel;

public interface Payment {

    /**
     * 统一下单接口
     *
     * @param params 下单参数
     * @return
     */
    Response unifiedOrder(UnifiedOrderModel params) throws RequestFailedException;

    /**
     * 关闭订单接口
     *
     * @param orderNo 平台订单号
     * @return
     */
    Response cancel(String orderNo) throws RequestFailedException;

    /**
     * 退款接口
     *
     * @param orderNo 平台订单号
     * @return
     */
    Response refund(String orderNo) throws RequestFailedException;

    /**
     * 指定金额退款接口
     *
     * @param orderNo 平台订单号
     * @param amount  退款金额
     * @return
     */
    Response refund(String orderNo, int amount) throws RequestFailedException;

    /**
     * 交易查询接口
     *
     * @param orderNo 平台订单号
     * @return
     */
    Response query(String orderNo) throws RequestFailedException;

    /**
     * 交易结果处理
     *
     * @param response 支付通知原始数据
     * @return
     */
    Response paymentNotify(Object response) throws RequestFailedException;
}
