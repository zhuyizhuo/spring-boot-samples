@echo off

REM 简单的批处理文件，用于运行DirectJdbcConnectionTest

REM 设置类路径，包含target/test-classes和target/classes
set CLASSPATH=target\test-classes;target\classes

REM 打印类路径信息
echo 类路径: %CLASSPATH%

REM 运行DirectJdbcConnectionTest
java com.github.zhuyizhuo.sample.mybatis.plus.advanced.test.DirectJdbcConnectionTest

REM 显示退出码
echo 测试退出码: %ERRORLEVEL%

REM 暂停以便查看输出
pause