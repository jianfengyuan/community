package com.kim.community.Controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class Alphainterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(Alphainterceptor.class);
    // 在Controller之前執行
    // 一般返回true, 那Controller的代碼會繼續執行
    // 如果返回false, 這個請求就會被攔截掉
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("請求被攔截");
        logger.debug(handler.toString());
        return true;
    }

    // 在Controller 之後執行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle: " + handler.toString() + "\n" + modelAndView.toString());
    }

    // 在TemplateEngine(模板引擎)之後執行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion" + handler.toString());
    }
}
