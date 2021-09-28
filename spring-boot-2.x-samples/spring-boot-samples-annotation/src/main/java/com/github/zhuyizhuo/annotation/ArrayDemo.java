package com.github.zhuyizhuo.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArrayDemo {

    @Value("${demo.array1}")
    private String[] array1;
    @Value("${demo.array2}")
    private int[] array2;
    @Value("${demo.array3}")
    private double[] array3;
    @Value("${demo.array4:}")
    private String[] array4;
    @Value("${demo.array5:123.1,4.56}")
    private double[] array5;

    public String[] getArray1() {
        return array1;
    }

    public void setArray1(String[] array1) {
        this.array1 = array1;
    }

    public int[] getArray2() {
        return array2;
    }

    public void setArray2(int[] array2) {
        this.array2 = array2;
    }

    public double[] getArray3() {
        return array3;
    }

    public void setArray3(double[] array3) {
        this.array3 = array3;
    }

    public String[] getArray4() {
        return array4;
    }

    public void setArray4(String[] array4) {
        this.array4 = array4;
    }

    public double[] getArray5() {
        return array5;
    }

    public void setArray5(double[] array5) {
        this.array5 = array5;
    }
}
