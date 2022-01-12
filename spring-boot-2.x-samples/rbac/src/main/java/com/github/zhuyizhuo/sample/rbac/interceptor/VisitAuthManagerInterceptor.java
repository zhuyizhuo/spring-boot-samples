package com.github.zhuyizhuo.sample.rbac.interceptor;

import com.github.zhuyizhuo.sample.rbac.autoconfigure.AuthProperties;
import com.github.zhuyizhuo.sample.rbac.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * class: VisitAuthManagerInterceptor <br>
 * description: 访问权限拦截器 <br>
 * @author zhuo <br>
 */
@Slf4j
public class VisitAuthManagerInterceptor extends HandlerInterceptorAdapter {

    final AuthProperties authProperties;

    public VisitAuthManagerInterceptor(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        /** 支持匿名访问的方法 */
        String urlStr = requestURI.replace(request.getContextPath(), "");
        log.info("requestURI:{},urlStr:{}", requestURI, urlStr);

        if (authProperties.getAnonymous().contains(urlStr)) {
            log.info("{}, support anonymous access!", requestURI);
            return true;
        }
        /** 设置编码*/
        response.setContentType("application/json;charset=utf-8");

        // 判断用户是否登录 没有登录则返回
        Object loginUser = request.getSession().getAttribute(AuthConstant.LOGIN_SESSION_NAME);
        if (loginUser == null){
            /** 流操作经过过滤器 所以在此获取writer   */
            Writer writer = response.getWriter();
            writer.write("{\"code\":998,\"errorMsg\": \"please login!\"}");
            writer.flush();
            return false;
        }

        /** 登陆后免授权的方法 */
        if (authProperties.getLogin().contains(urlStr)) {
            log.info("{}, is login authorization!", requestURI);
            return true;
        }

        /** TODO 获取用户拥有权限 判断用户是否有当前权限 */
        List loginUserRoles = new ArrayList<>();
        log.info("user [{}] had permissions :{}", loginUser, loginUserRoles);
        // 此处 判断 当前路径是否有权限
        if (loginUserRoles.contains(urlStr)){
            return super.preHandle(request, response, handler);
        }
        String[] strings = StringUtils.tokenizeToStringArray(urlStr, "/");
        // 此处 判断 只要父路径在权限内 就可访问所有方法
        if (loginUserRoles.contains(strings[0])){
            return super.preHandle(request, response, handler);
        }

        log.info("url: {}, {} has no permission!", requestURI, loginUser);
        /** 流操作经过过滤器 所以在此获取writer   */
        Writer writer = response.getWriter();
        writer.write("{\"code\":999,\"errorMsg\": \"no permission!\"}");
        writer.flush();
        return false;
    }

}
