package com.github.zhuyizhuo.sample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "customize")
public class CustomizeProperties {
    /** 自定义静态文件目录 */
    private static String staticLocations;
    /** 自定义本地文件目录 */
    private static String uploadPath;

    public static String getStaticLocations() {
        return staticLocations;
    }

    public void setStaticLocations(String staticLocations) {
        CustomizeProperties.staticLocations = staticLocations;
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        CustomizeProperties.uploadPath = uploadPath;
    }
}
