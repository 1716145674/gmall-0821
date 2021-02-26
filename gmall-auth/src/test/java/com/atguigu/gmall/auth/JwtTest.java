package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
	private static final String pubKeyPath = "D:\\project\\rsa\\rsa.pub";
    private static final String priKeyPath = "D:\\project\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

//    @BeforeEach
//    public void testGetRsa() throws Exception {
//        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
//    }

    @Test
    public void testGenerateToken() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE2MTQwNzczNTB9.Zbs9pBt9Fea6LQtRjWVIXArHOizs-Mw1JCGJ5X-wCMxV9TDe0xVM6pQ2A9ywM3xUN04PWY36e-ncGoF_U32HIz9yhslNd0VEakkAh2jViyzCs3NwulqEUHDQaAf-DTkW90VmSuOVgMfnb2VuolzsP9_v3TWnyV13cftBWi_CUJ6D_pGEqif20FC_wYfwovwtVYAgqBjKSvLCawsVlOIbLekU8ksXRfBUDNCQHBQhK4i7f_B3XSJYb-tyGqAcGFhBVf3rK3RRp4oKNDs4ZCT6s0zIK9P_bVS3nIYB232xYINiEp01TwasyY1LZj_uzQW8axPf12Q92GkTMZ5r30qpVA";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}