package com.github.zhuyizhuo.samples.statemachine;

import com.github.zhuyizhuo.samples.statemachine.constants.StateMachineConstant;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatus;
import com.github.zhuyizhuo.samples.statemachine.enums.OrderStatusChangeEvent;
import com.github.zhuyizhuo.samples.statemachine.vo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;

@Slf4j
@EnableAsync
@SpringBootApplication
public class StateMachineApplication implements CommandLineRunner {

//    @Autowired
//    private StateMachine<OrderStatus, OrderStatusChangeEvent> stateMachine;
    @Autowired
    private StateMachineFactory<OrderStatus, OrderStatusChangeEvent> stateMachineFactory;
    @Autowired
    private StateMachinePersister<OrderStatus, OrderStatusChangeEvent, Order> persister;

    public static void main(String[] args) {
        SpringApplication.run(StateMachineApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        StateMachine<OrderStatus, OrderStatusChangeEvent> stateMachine = stateMachineFactory.getStateMachine(StateMachineConstant.ORDER_STATE_MACHINE_ID);
        Order order = new Order();
        order.setId("123");
        order.setStatus(OrderStatus.PROCESSING.name());
        persister.restore(stateMachine, order);
//        stateMachine.start();
        log.info("--- PAYMENT_SUCCESSFUL ---");
        stateMachine.sendEvent(OrderStatusChangeEvent.PAYMENT_SUCCESSFUL);
        log.info("--- THIRD_PARTIES_RETURN_FAILED ---");
        Message<OrderStatusChangeEvent> order1 = MessageBuilder
                .withPayload(OrderStatusChangeEvent.THIRD_PARTIES_RETURN_FAILED)
                .setHeader("order", order).build();
        stateMachine.sendEvent(order1);
        log.info("--- THIRD_PARTIES_RETURN_SUCCESS ---");
        stateMachine.sendEvent(OrderStatusChangeEvent.THIRD_PARTIES_RETURN_SUCCESS);
        persister.persist(stateMachine, order);
//        stateMachine.stop();
    }

}
