# rocket mq 使用示例
1、本地部署 MQ 服务
2、修改配置文件 name-server
3、启动 ProducerApplication 查看日志

## Producer
发消息肯定要必备如下几个条件：

指定生产组名（不能用默认的，会报错）

配置 name-server 地址（必须）

指定topic name（必须）

指定tag/key（可选）

验证消息是否发送成功：消息发送完后可以启动消费者进行消费，也可以去管控台上看消息是否存在。