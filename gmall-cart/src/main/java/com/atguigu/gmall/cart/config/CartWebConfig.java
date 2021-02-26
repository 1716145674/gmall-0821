package com.atguigu.gmall.cart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zqq
 * @create 2021-02-24 20:35
 */
@Configuration
public class CartWebConfig implements WebMvcConfigurer {
    @Autowired
    private CartInterceptor cartInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartInterceptor).addPathPatterns("/**");
    }
}
