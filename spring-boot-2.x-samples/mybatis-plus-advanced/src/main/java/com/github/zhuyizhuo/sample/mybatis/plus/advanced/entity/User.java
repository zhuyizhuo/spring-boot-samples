package com.github.zhuyizhuo.sample.mybatis.plus.advanced.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 使用MyBatis-Plus的注解配置逻辑删除、自动填充等功能
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，使用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    // 显式添加setId方法，确保Controller中可以使用
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间，使用自动填充功能
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，使用自动填充功能
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}