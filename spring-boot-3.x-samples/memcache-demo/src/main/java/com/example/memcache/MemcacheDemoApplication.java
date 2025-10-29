package com.example.memcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class MemcacheDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemcacheDemoApplication.class, args);
    }

    /**
     * 应用启动后打印访问地址
     */
    @Component
    public static class StartupRunner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) throws Exception {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                String ip = inetAddress.getHostAddress();
                int port = 8082; // 默认端口，可以通过配置文件修改
                
                System.out.println("\n===========================================");
                System.out.println("应用已启动成功！");
                System.out.println("访问地址：http://" + ip + ":" + port);
                System.out.println("测试页面：http://" + ip + ":" + port + "/");
                System.out.println("===========================================");
            } catch (UnknownHostException e) {
                System.out.println("\n===========================================");
                System.out.println("应用已启动成功！");
                System.out.println("访问地址：http://localhost:8082");
                System.out.println("测试页面：http://localhost:8082/");
                System.out.println("===========================================");
            }
        }
    }
}