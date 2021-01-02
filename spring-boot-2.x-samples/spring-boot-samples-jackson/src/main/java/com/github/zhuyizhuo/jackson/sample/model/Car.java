package com.github.zhuyizhuo.jackson.sample.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhuo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Car {

    private Long id;
    @JsonProperty("carName")
    private String name;

    public Car() {
    }

    public Car(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
