package com.github.zhuyizhuo.samples.statemachine.sevice;

import com.github.zhuyizhuo.samples.statemachine.constants.StateMachineConstant;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatusChangeEvent;
import com.github.zhuyizhuo.samples.statemachine.vo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

@Async
@Slf4j
@WithStateMachine(name = StateMachineConstant.ORDER_STATE_MACHINE_ID)
public class EventConfig {

    @OnTransition(target = "CREATED")
    public void create() {
        log.info("订单创建，待支付");
    }

    @OnTransition(source = "CREATED", target = "PROCESSING")
    public void pay() {
        log.info("用户完成支付，订单状态变为处理中, 待调用三方");
    }

    @OnTransition(source = "PROCESSING", target = "SUCCESS")
    public void success() {
        log.info("三方返回成功，订单状态变为成功");
    }

    @OnTransition(source = "PROCESSING", target = "FAILURE")
    public void failure(Message<OrderStatusChangeEvent> message) {
        log.info("三方返回失败，订单状态变为失败,order:{}", message.getHeaders().get("order", Order.class));
    }

}
