package com.atguigu.gmall.cart.config;

import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author zqq
 * @create 2021-02-24 11:44
 */
// 拦截请求判断用户是否登录
@Component
public class CartInterceptor implements HandlerInterceptor {

    // 存储用户信息
    private static final ThreadLocal<UserInfo> THREAD_LOCAL=new ThreadLocal<>();

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = new UserInfo();
        // 1. 判断是否有userKey
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKey());
        if (StringUtils.isBlank(userKey)){
            userKey=UUID.randomUUID().toString();
            //将游客id放到cookie中
            CookieUtils.setCookie(request,response,jwtProperties.getUserKey(),userKey  ,jwtProperties.getExpire());
        }
        //将游客id设置到用户信息中
        userInfo.setUserKey(userKey);
        // 2. 判断是否登录
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        if (!StringUtils.isBlank(token)){
            // 解析token
            String userId = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey()).get("userId").toString();

            if (!StringUtils.isBlank(userId)){
                userInfo.setUserId(Long.valueOf(userId));
            }
        }

        THREAD_LOCAL.set(userInfo);
        // 放行
        return true;
    }
    // 对外暴露查看userInfo的接口
    public static  UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 必须关闭THREAD_LOCAL
        THREAD_LOCAL.remove();
    }
}
