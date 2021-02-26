package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.gateway.config.JwtProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zqq
 * @create 2021-02-23 19:40
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public GatewayFilter apply(PathConfig config) {

        return new GatewayFilter() {

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                // 1 判断当前的请求是否在拦截名单中 不在直接放行
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();

                List<String> filterPaths = config.getPaths();
                String currentPath = request.getURI().getPath();

                if (!filterPaths.stream().anyMatch(path ->
                        currentPath.startsWith(path)
                )) {
                    return chain.filter(exchange);
                }

                // 2 拿到请求的token信息,同步请求从cookie中获取,异步请求从头信息中获取

                HttpCookie cookie = request.getCookies().getFirst(jwtProperties.getCookieName());
                String token = null;
                if (cookie != null) {
                    token = cookie.getValue();
                } else {
                    token = request.getHeaders().getFirst("token");
                }

                // 3 token不存在的话 冲定向到登录页面

                if (StringUtils.isBlank(token)) {
                    // 重定向到登录
                    // 303状态码表示由于请求对应的资源存在着另一个URI，应使用重定向获取请求的资源
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }

                try {
                    // 4 token存在的话 解析token 判断是否正确 正确 放行
                    Map<String, Object> userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
                    // 判断ip
                    String ip = userInfo.get("ip").toString();
                    String currentIp = IpUtil.getIpAddressAtGateway(request);
                    if (!StringUtils.equals(ip, currentIp)) {
                        // 重定向到登录
                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                        response.setComplete();
                    }


                    // 5 传递登录信息给后续的服务，不需要再次解析jwt
                    request.mutate().header("userId", userInfo.get("userId").toString()).build();
                    exchange.mutate().request(request).build();


                } catch (Exception e) {
                    e.printStackTrace();
                    // 重定向登录
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }
                // 放行
                return chain.filter(exchange);
            }

        };
    }


    @Data
    public static class PathConfig {
        List<String> paths;
    }

    // 告知父类使用此内部内接受参数
    public AuthGatewayFilterFactory() {
        super(PathConfig.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("paths");
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }
}
