package com.example.postgresql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class PostgreSqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgreSqlApplication.class, args);
    }

    @Component
    public class StartupRunner implements ApplicationRunner {

        @Value("${server.port:8080}")
        private String serverPort;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            System.out.println("\n=============================================");
            System.out.println("应用已成功启动！");
            System.out.println("主页面访问地址: http://localhost:" + serverPort + "/index.html");
            System.out.println("默认访问地址: http://localhost:" + serverPort);
            System.out.println("=============================================\n");
        }
    }
}