package com.github.zhuyizhuo.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class AnnotationApplication {
    @Autowired
    ListConfig config;
    @Autowired
    ListDemo listDemo;
    @Autowired
    ArrayDemo arrayDemo;
    @Value("${jwt.auth.log:false}")
    private boolean enableLog;

    public static void main(String[] args) {
        SpringApplication.run(AnnotationApplication.class, args);
    }

    @Autowired(required = false)
    TestPro testPro;

    /**
     * 测试 @ConditionalOnProperty 使用
     */
    @PostConstruct
    void test(){
        if (testPro != null){
            testPro.sayHello();
        } else {
            System.out.println("testPro is null");
        }
    }

    @PostConstruct
    void array2List() {
        System.out.println("array2List print:");
        System.out.println(listDemo.getArray1());
        System.out.println(listDemo.getArray2());
        System.out.println(listDemo.getArray3());
        System.out.println();
    }

    @PostConstruct
    void arrayDemo() {
        System.out.println("arrayDemo print:");
        String[] array1 = arrayDemo.getArray1();
        for (int i = 0; i < array1.length ; i++) {
            System.out.println(array1[i]);
        }
        int[] array2 = arrayDemo.getArray2();
        for (int i = 0; i < array2.length ; i++) {
            System.out.println(array2[i]);
        }
        double[] array3 = arrayDemo.getArray3();
        for (int i = 0; i < array3.length ; i++) {
            System.out.println(array3[i]);
        }
        String[] array4 = arrayDemo.getArray4();
        System.out.println("array4 length:" + array4.length);
        double[] array5 = arrayDemo.getArray5();
        System.out.println("array5 length:" + array5.length);
        System.out.println();
    }

    @PostConstruct
    void list() {
        System.out.println("enable log:" + enableLog);
        System.out.println("demo list:" + config.getList());
    }
}
