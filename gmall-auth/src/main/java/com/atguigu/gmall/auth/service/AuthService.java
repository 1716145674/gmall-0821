package com.atguigu.gmall.auth.service;

import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.exception.AuthException;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author zqq
 * @create 2021-02-23 18:14
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private JwtProperties jwtProperties;

    public void login(UserEntity userEntity, HttpServletRequest request, HttpServletResponse response) {
        try {
            //检查当前用户账号密码是否正确
            UserEntity user = umsClient.queryUser(userEntity.getUsername(), userEntity.getPassword()).getData();
            if (user == null) {
                throw new AuthException("用户名或者密码有误！");
            }
            //如果正确 把用户id翻到载荷中
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId",user.getId());
            map.put("username",user.getUsername());
            //为了防止被别人盗取 拿到用户的ip
            String ip = IpUtil.getIpAddressAtService(request);
            map.put("ip",ip);

            String token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            // 把token翻到cookie中
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);

            // 把用户昵称翻到cookie中,方便页面展示昵称
            CookieUtils.setCookie(request, response, this.jwtProperties.getUnick(), user.getNickname(), this.jwtProperties.getExpire() * 60);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException("用户名或者密码有误！");
        }

    }
}
