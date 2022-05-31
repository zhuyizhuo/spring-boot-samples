package com.github.zhuyizhuo.sample.log4j2.interceptor;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 日志拦截器 <br>
 *
 * @author zhuo <br>
 */
@Component
public class LogInterceptor implements HandlerInterceptor {
    private final static String KEY_SESSION_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        ThreadContext.put(KEY_SESSION_ID, requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
        ThreadContext.clearAll();
    }

}
