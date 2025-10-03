package com.github.zhuyizhuo.wechat.demo.service;

/**
 * 微信消息服务接口
 */
public interface WechatMessageService {

    /**
     * 发送文本消息
     * @param openId 用户的openId
     * @param content 消息内容
     * @return 是否发送成功
     */
    boolean sendTextMessage(String openId, String content);

    /**
     * 发送模板消息
     * @param openId 用户的openId
     * @param templateId 模板ID
     * @param data 模板数据
     * @param url 点击模板消息跳转的URL
     * @return 是否发送成功
     */
    boolean sendTemplateMessage(String openId, String templateId, Object data, String url);

}