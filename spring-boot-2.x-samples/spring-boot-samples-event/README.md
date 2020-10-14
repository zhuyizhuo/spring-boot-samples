# spring-boot 发布自定义事件
1. 演示如何使用自定义事件 
2. 事件监听默认为同步执行，如需异步可通过注解改为异步执行

## 验证
第一步 : 启动 EventApplication ,观察控制台输出如下:
```console
.. INFO 42447 --- [           main] c.g.z.event.sample.EventApplication      : Started EventApplication in 16.85 seconds (JVM running for 17.363)
.. INFO 42447 --- [           main] c.g.z.e.sample.listener.MyEventListener  : 监听到事件 start:MyEvent{name='1235'}
.. INFO 42447 --- [           main] c.g.z.e.sample.listener.MyEventListener  : 监听到事件 end:MyEvent{name='1235'}
.. INFO 42447 --- [           main] c.g.z.event.sample.EventApplication      : event published.
```
观察控制台输出同为 main 线程，并且输出顺序为同步输出

第二步 : 打开 MyEventListener 方法 onApplicationEvent 上的注解 @Async，再次观察控制台输出
```console
.. INFO 42466 --- [           main] c.g.z.event.sample.EventApplication      : Started EventApplication in 16.793 seconds (JVM running for 17.286)
.. INFO 42466 --- [           main] c.g.z.event.sample.EventApplication      : event published.
.. INFO 42466 --- [         task-1] c.g.z.e.sample.listener.MyEventListener  : 监听到事件 start:MyEvent{name='1235'}
.. INFO 42466 --- [         task-1] c.g.z.e.sample.listener.MyEventListener  : 监听到事件 end:MyEvent{name='1235'}
```
此时控制台输出变为两个线程，并且输出顺序可看出为异步输出