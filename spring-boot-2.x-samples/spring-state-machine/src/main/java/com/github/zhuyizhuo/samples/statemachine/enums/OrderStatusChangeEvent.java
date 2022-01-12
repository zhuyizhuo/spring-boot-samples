package com.github.zhuyizhuo.samples.statemachine.enums;

public enum OrderStatusChangeEvent {
    /** 三方业务接口返回成功 */
    THIRD_PARTIES_RETURN_SUCCESS,
    /** 三方业务接口返回失败 */
    THIRD_PARTIES_RETURN_FAILED,
    /** 调用三方超时 */
    CALL_TRIPARTITE_TIMEOUT,

    /** 支付成功 */
    PAYMENT_SUCCESSFUL,
    /** 支付失败 */
    PAYMENT_FAILED,
    /** 支付超时 */
    PAYMENT_OVERTIME,
    /** 取消订单 */
    CANCEL,
    ;
}
