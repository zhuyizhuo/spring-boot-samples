package com.example.ehcache.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类
 * 实现Serializable接口，支持缓存序列化
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private Integer age;
    private LocalDateTime createTime;

    // 构造函数
    public User() {
    }

    public User(Long id, String username, String email, Integer age) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createTime=" + createTime +
                '}';
    }
}