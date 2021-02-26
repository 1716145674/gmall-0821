package com.atguigu.gmall.gateway.config;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author zqq
 * @create 2021-02-23 18:49
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    private String pubKeyPath;
    private String cookieName;

    private PublicKey publicKey;

    // 获取到公钥私钥对象
    @PostConstruct
    private void init(){
        try {
            this.publicKey=RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("公钥不存在");
            e.printStackTrace();

        }


    }

}
