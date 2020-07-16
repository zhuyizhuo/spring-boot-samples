package com.github.zhuyizhuo.japidocs.sample.vo;

/**
 * 用户对象
 * @author zhuo
 */
public class UserVO {
    /** 用户 ID */
    private Long id;
    /** 用户名 */
    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
