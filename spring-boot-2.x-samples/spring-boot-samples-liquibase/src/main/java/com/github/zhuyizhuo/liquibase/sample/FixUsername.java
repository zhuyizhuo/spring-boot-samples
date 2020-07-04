package com.github.zhuyizhuo.liquibase.sample;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixUsername implements CustomTaskChange {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Database database) throws CustomChangeException {
        //TODO 此处实现复杂业务逻辑  操作数据库之类
        System.out.println("执行业务逻辑。。");
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
