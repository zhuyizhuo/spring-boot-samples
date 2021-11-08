package test;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import javax.annotation.PostConstruct;

@EnableKafka
@SpringBootApplication
public class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("topic1")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Autowired
    private KafkaSender kafkaSender;

    @PostConstruct
    public void sendMessage() throws InterruptedException {
        for (int i = 0; i < 300; i++) {
            kafkaSender.send("测试发送消息");
            Thread.sleep(1500);
        }
    }

}
