package com.atguigu.gmall.auth.controller;

import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.net.www.protocol.http.AuthScheme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zqq
 * @create 2021-02-23 18:07
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("toLogin.html")
    public String toLogin(@RequestParam("returnUrl") String returnUrl, Model model) {
        // 把登录前的页面地址，记录到登录页面，以备将来登录成功，回到登录前的页面
        model.addAttribute("returnUrl",returnUrl);
        return "login";
    }

    //登录
    @PostMapping("login")
    public String login(UserEntity userEntity, HttpServletRequest request, HttpServletResponse response, @RequestParam("returnUrl") String returnUrl) {
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = "http://www.gmall.com";
        }

       this.authService.login(userEntity, request, response);
       return "redirect:" + returnUrl;

    }
}
