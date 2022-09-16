package com.bob.cdms.filter;


import com.bob.cdms.constant.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

import static com.bob.cdms.constant.AdminPath.ADMIN_PATH;

/**
 * 管理员登录过滤器
 * @author zengxuan
 * @since  2022/9/11
 *
 */

@WebFilter
@Slf4j
public class AdminFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("AdminFilter加载成功");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI();
        log.info("请求路径为：{}", path);
        for (String adminPath : ADMIN_PATH) {
            if (path.startsWith(adminPath)) {
                log.info("侦测到管理员接口，进行管理员登录校验");
                String username = request.getHeader("username");
                String password = request.getHeader("password");
                if (Admin.USERNAME.equals(username) && Admin.PASSWORD.equals(password)) {
                    log.info("管理员登录成功");
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                } else {
                    log.info("管理员登录失败");
                    //URI重定向
                    response.sendRedirect("/system/401");
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }

            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }
}
