package com.github.zhuyizhuo.samples.statemachine.sevice;

import com.github.zhuyizhuo.samples.statemachine.constants.StateMachineConstant;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatus;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatusChangeEvent;
import com.github.zhuyizhuo.samples.statemachine.vo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 持久化
 */
@Slf4j
@Component
public class OrderStateMachinePersist implements StateMachinePersist<OrderStatus, OrderStatusChangeEvent, Order> {
    
    @Override
    public void write(StateMachineContext<OrderStatus, OrderStatusChangeEvent> stateMachineContext, Order order) throws Exception {
        order.setStatus(stateMachineContext.getState().name());
        log.info("模拟持久化,order:{}", order);
    }

    @Override
    public StateMachineContext<OrderStatus, OrderStatusChangeEvent> read(Order order) throws Exception {
        log.info("初始化状态为:{}", order);
        // 注意状态机的初识状态与配置中定义的一致
        return StringUtils.hasText(order.getStatus()) ?
                new DefaultStateMachineContext<>(OrderStatus.valueOf(order.getStatus()), null, null, null, null, StateMachineConstant.ORDER_STATE_MACHINE_ID) :
                new DefaultStateMachineContext<>(OrderStatus.CREATED, null, null, null, null, StateMachineConstant.ORDER_STATE_MACHINE_ID);
    }
}
