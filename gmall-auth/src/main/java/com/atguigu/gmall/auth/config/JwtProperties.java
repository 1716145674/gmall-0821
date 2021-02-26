package com.atguigu.gmall.auth.config;

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
    private String priKeyPath;
    private String secret;
    private String cookieName;
    private Integer expire;
    private String unick;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    // 获取到公钥私钥对象
    @PostConstruct
    private void init(){
        try {
            File pubFile = new File(pubKeyPath);
            File pirFile = new File(priKeyPath);
            // 如果任何一个不存在的话重新生成
            if(!pubFile.exists()||!pirFile.exists()){
                RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            }
            this.publicKey=RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey=RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("生成公钥私钥出错了");
            e.printStackTrace();

        }


    }

}
