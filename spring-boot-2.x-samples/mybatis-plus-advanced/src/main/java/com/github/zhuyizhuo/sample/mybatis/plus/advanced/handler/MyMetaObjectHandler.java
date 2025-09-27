package com.github.zhuyizhuo.sample.mybatis.plus.advanced.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 自定义元对象处理器
 * 用于自动填充创建时间和更新时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        // 自动填充逻辑删除字段为未删除状态
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    /**
     * 更新操作时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}