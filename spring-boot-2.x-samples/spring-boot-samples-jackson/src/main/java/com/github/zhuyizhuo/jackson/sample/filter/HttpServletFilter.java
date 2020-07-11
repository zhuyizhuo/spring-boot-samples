package com.github.zhuyizhuo.jackson.sample.filter;

import com.github.zhuyizhuo.jackson.sample.wrapper.ParameterRequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 入参处理 Filter
 * @author zhuo
 */
@Component
public class HttpServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new ParameterRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {

    }
}
