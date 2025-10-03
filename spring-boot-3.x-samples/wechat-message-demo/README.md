# 微信消息演示项目

这是一个使用Spring Boot和微信公众号SDK（weixin-java-mp）实现的微信消息推送示例项目。

## 功能特性

- 发送微信文本消息
- 发送微信模板消息

## 配置说明

在application.yml中配置以下微信公众号参数：

```yaml
wechat:
  mp:
    app-id: your-app-id
    secret: your-app-secret
```

## 使用说明

1. 配置微信公众号参数
2. 运行WechatApplication
3. 使用WechatMessageService接口发送消息