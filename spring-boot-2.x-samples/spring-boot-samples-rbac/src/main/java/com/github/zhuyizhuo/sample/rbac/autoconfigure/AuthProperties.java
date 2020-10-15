package com.github.zhuyizhuo.sample.rbac.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth.pattern")
public class AuthProperties {

    private List<String> anonymous;
    private List<String> login;
    private List<String> menu;

}