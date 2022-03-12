package com.example.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * 这里只是简单的演示权限控制
 */
@Slf4j
@WebFilter(filterName = "websocketAuthFilter", urlPatterns = "/*")
public class TokenAuthenticationFilter  implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("start to auth request validate...111");
        HttpServletRequest request = (HttpServletRequest) req;

        String accessToken = request.getHeader("access-token");

        if (Objects.isNull(accessToken)) {
            accessToken = Optional.ofNullable(WebUtils.getCookie(request, "access-token")).map(Cookie::getValue).orElse(null);
        }
        if (Objects.isNull(accessToken)) {
            accessToken = request.getParameter("access-token");
        }
        log.info("token:{}", accessToken);
        // TODO 这里自行定义权限控制逻辑
        if("123456".equals(accessToken)) {
            log.info("auth success");
            chain.doFilter(request, response);
        } else {
            log.error("auth failed");
        }
    }
}
