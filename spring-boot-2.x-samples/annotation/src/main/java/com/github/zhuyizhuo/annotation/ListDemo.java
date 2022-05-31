package com.github.zhuyizhuo.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ListDemo {

    @Value("#{'${demo.array1:}'.empty ? null : '${demo.array1:}'.split(',')}")
    private List<String> array1;
    @Value("#{'${demo.array2:}'.empty ? null : '${demo.array2:}'.split(',')}")
    private List<Integer> array2;
    @Value("#{'${demo.array3:}'.empty ? null : '${demo.array3:}'.split(',')}")
    private List<Double> array3;

    public List<String> getArray1() {
        return array1;
    }

    public void setArray1(List<String> array1) {
        this.array1 = array1;
    }

    public List<Integer> getArray2() {
        return array2;
    }

    public void setArray2(List<Integer> array2) {
        this.array2 = array2;
    }

    public List<Double> getArray3() {
        return array3;
    }

    public void setArray3(List<Double> array3) {
        this.array3 = array3;
    }
}
