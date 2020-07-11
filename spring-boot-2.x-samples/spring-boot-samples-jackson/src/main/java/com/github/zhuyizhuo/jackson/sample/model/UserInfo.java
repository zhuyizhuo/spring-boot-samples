package com.github.zhuyizhuo.jackson.sample.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.zhuyizhuo.jackson.sample.customize.CustomeJackSon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuo
 */
public class UserInfo {

    @JsonSerialize(using = CustomeJackSon.Serialize.class)
    @JsonDeserialize(using = CustomeJackSon.Deserializer.class)
    private Long id;
    private Long height;
    private String nickName;
    private String name;
    private String email;
    private List<Car> cars;

    public UserInfo() {
    }

    public UserInfo(Long id, String name) {
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Car> getCars() {
        return this.cars;
    }

    public void addCar(Car car) {
        if (this.cars == null){
            this.cars = new ArrayList<>();
        }
        this.cars.add(car);
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }
}
