package com.github.zhuyizhuo.samples.statemachine.sevice;

import com.github.zhuyizhuo.samples.statemachine.constants.StateMachineConstant;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatusEnum;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatusChangeEvent;
import com.github.zhuyizhuo.samples.statemachine.vo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

@Slf4j
@Configuration
//@EnableStateMachine(name = "orderStateMachine")
@EnableStateMachineFactory(name = "orderStateMachineFactory")
public class OrderStateMachineConfigurer extends EnumStateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusChangeEvent> {

    @Autowired
    private OrderStateMachinePersist orderStateMachinePersist;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusChangeEvent> states)
            throws Exception {
        states.withStates()
                // 初始状态：新建订单
                .initial(OrderStatusEnum.CREATED)
                .states(EnumSet.allOf(OrderStatusEnum.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusChangeEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(OrderStatusEnum.CREATED).target(OrderStatusEnum.PROCESSING)
                .event(OrderStatusChangeEvent.PAYMENT_SUCCESSFUL).action(paySuccess())
                .and()
                .withExternal()
                .source(OrderStatusEnum.PROCESSING).target(OrderStatusEnum.SUCCESS)
                .event(OrderStatusChangeEvent.THIRD_PARTIES_RETURN_SUCCESS).action(thirdPartiesReturnSuccess())
                .and()
                .withExternal()
                .source(OrderStatusEnum.PROCESSING).target(OrderStatusEnum.FAILURE)
                .event(OrderStatusChangeEvent.THIRD_PARTIES_RETURN_FAILED).action(thirdPartiesReturnFail())
                .and()
                .withExternal()
                .source(OrderStatusEnum.CREATED).target(OrderStatusEnum.CANCEL)
                .event(OrderStatusChangeEvent.CANCEL).action(cancleOrder())
                .and()
                .withExternal()
                .source(OrderStatusEnum.PROCESSING).target(OrderStatusEnum.CANCEL)
                .event(OrderStatusChangeEvent.CANCEL).action(cancleOrder())
        ;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatusEnum, OrderStatusChangeEvent> config)
            throws Exception {
        config.withConfiguration()
                .machineId(StateMachineConstant.ORDER_STATE_MACHINE_ID)
        ;
    }

    @Bean
    public StateMachinePersister<OrderStatusEnum, OrderStatusChangeEvent, Order> stateMachinePersist() {
        return new DefaultStateMachinePersister<>(orderStateMachinePersist);
    }

    public Action<OrderStatusEnum, OrderStatusChangeEvent> thirdPartiesReturnSuccess() {
        return context -> log.info("三方返回成功,订单变为成功" );
    }

    private Action<OrderStatusEnum, OrderStatusChangeEvent> thirdPartiesReturnFail() {
        return stateContext -> log.info("三方返回失败,订单变为失败");
    }

    public Action<OrderStatusEnum, OrderStatusChangeEvent> paySuccess() {
        return context -> log.info("支付成功,订单变为处理中" );
    }

    private Action<OrderStatusEnum, OrderStatusChangeEvent> cancleOrder() {
        return stateContext -> log.info("取消订单,source:{},target:{}",stateContext.getSource().getId(),stateContext.getTarget().getId());
    }

}