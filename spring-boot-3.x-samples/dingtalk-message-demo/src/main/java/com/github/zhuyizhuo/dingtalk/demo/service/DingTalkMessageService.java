package com.github.zhuyizhuo.dingtalk.demo.service;

/**
 * 钉钉消息服务接口
 */
public interface DingTalkMessageService {

    /**
     * 发送文本消息
     * @param content 消息内容
     * @param atMobiles 需要@的手机号列表
     * @param isAtAll 是否@所有人
     * @return 是否发送成功
     */
    boolean sendTextMessage(String content, String[] atMobiles, boolean isAtAll);

    /**
     * 发送Markdown消息
     * @param title 消息标题
     * @param text 消息内容（Markdown格式）
     * @param atMobiles 需要@的手机号列表
     * @param isAtAll 是否@所有人
     * @return 是否发送成功
     */
    boolean sendMarkdownMessage(String title, String text, String[] atMobiles, boolean isAtAll);

    /**
     * 发送工作通知消息
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @return 是否发送成功
     */
    boolean sendWorkNoticeMessage(String userId, String title, String content);
}