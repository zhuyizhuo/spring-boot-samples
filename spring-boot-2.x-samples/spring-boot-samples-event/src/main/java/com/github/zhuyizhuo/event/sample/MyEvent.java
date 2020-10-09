package com.github.zhuyizhuo.event.sample;

import org.springframework.context.ApplicationEvent;

public class MyEvent extends ApplicationEvent {

    private String name;

    public MyEvent(Object source) {
        super(source);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyEvent{" +
                "name='" + name + '\'' +
                '}';
    }
}
