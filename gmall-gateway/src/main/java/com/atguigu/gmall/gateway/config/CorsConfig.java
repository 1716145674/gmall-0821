package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zqq
 * @create 2021-01-19 10:22
 * 配置全局跨域解决
 */
@Configuration
public class CorsConfig {
    // CorsWebFilter 用于解决WebFlux( gateway)跨域问题
    // CorsFilter 用于解决Servlet( zuul springMvc)跨域问题
    @Bean
    public CorsWebFilter corsWebFilter(){

        // 生成跨域配置类
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许的域名
        corsConfiguration.addAllowedOrigin("http://manager.gmall.com");
        corsConfiguration.addAllowedOrigin("http://api.gmall.com");
        corsConfiguration.addAllowedOrigin("http://www.gmall.com");
        corsConfiguration.addAllowedOrigin("http://gmall.com");
        corsConfiguration.addAllowedOrigin("http://item.gmall.com");
        // 允许的请求方式,* 表示所有
        corsConfiguration.addAllowedMethod("*");
        // 允许的请求头,* 表示所有
        corsConfiguration.addAllowedHeader("*");
        // 允许携带cookie
        corsConfiguration.setAllowCredentials(true);

        // 配置基于url的跨域配置源
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        // 配置源加载配置类,拦截所有的请求
        configurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(configurationSource);
    }
}
