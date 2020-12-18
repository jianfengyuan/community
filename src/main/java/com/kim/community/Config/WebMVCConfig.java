package com.kim.community.Config;

import com.kim.community.Controller.interceptor.Alphainterceptor;
import com.kim.community.Controller.interceptor.LoginRequiredInterceptor;
import com.kim.community.Controller.interceptor.LoginTicketInterceptor;
import com.kim.community.Controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
//    Alphainterceptor alphainterceptor;
    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css",
                                    "/**/*.js",
                                    "/**/*.png",
                                    "/**/*.jpg",
                                    "/**/*.jepg");
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.jepg");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.jepg");
    }
}
