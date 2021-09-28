package test;

import com.alibaba.fastjson.JSON;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class KafkaSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    //构造器方式注入  kafkaTemplate
    public KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String msg) {
        Message message = new Message();
        message.setId(System.currentTimeMillis());
        message.setMsg(msg);
        message.setSendTime(new Date());
        //对 topic =  kafka-springboot-001 的发送消息
        String data = JSON.toJSONString(message);
        System.out.println("发送消息:TOPIC:" + KafkaConstant.TOPIC + ", message:" + data);
        kafkaTemplate.send(KafkaConstant.TOPIC, "order", data);
    }

}
