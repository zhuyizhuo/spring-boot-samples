package com.github.zhuyizhuo.wechat.demo.service;

import com.github.zhuyizhuo.wechat.demo.service.WechatMessageService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信消息服务实现类
 */
@Service
@Slf4j
@AllArgsConstructor
public class WechatMessageServiceImpl implements WechatMessageService {

    private final WxMpService wxMpService;
    private final Gson gson;

    @Override
    public boolean sendTextMessage(String openId, String content) {
        try {
            // 构建文本消息
            me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage message = 
                me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage.TEXT().toUser(openId).content(content).build();
            
            // 发送文本消息
            wxMpService.getKefuService().sendKefuMessage(message);
            log.info("发送微信文本消息成功，openId: {}", openId);
            return true;
        } catch (Exception e) {
            log.error("发送微信文本消息失败，openId: {}", openId, e);
            return false;
        }
    }

    @Override
    public boolean sendTemplateMessage(String openId, String templateId, Object data, String url) {
        try {
            // 构建模板消息
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(openId)
                    .templateId(templateId)
                    .url(url)
                    .build();

            // 将data转换为Map并设置到模板消息中
            if (data instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) data;
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Map) {
                        Map<String, String> valueMap = (Map<String, String>) value;
                        WxMpTemplateData wxMpTemplateData = new WxMpTemplateData();
                        wxMpTemplateData.setName(entry.getKey());
                        wxMpTemplateData.setValue(valueMap.get("value"));
                        wxMpTemplateData.setColor(valueMap.get("color"));
                        templateMessage.addData(wxMpTemplateData);
                    } else {
                        WxMpTemplateData wxMpTemplateData = new WxMpTemplateData();
                        wxMpTemplateData.setName(entry.getKey());
                        wxMpTemplateData.setValue(value.toString());
                        wxMpTemplateData.setColor("#173177");
                        templateMessage.addData(wxMpTemplateData);
                    }
                }
            } else {
                // 如果不是Map，转换为JSON字符串并记录日志
                log.warn("模板消息数据格式不正确，应为Map类型: {}", gson.toJson(data));
            }

            // 发送模板消息
            String result = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            log.info("发送微信模板消息成功，openId: {}, result: {}", openId, result);
            return true;
        } catch (Exception e) {
            log.error("发送微信模板消息失败，openId: {}", openId, e);
            return false;
        }
    }
}