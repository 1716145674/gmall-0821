package com.atguigu.gmall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallAuthApplicationTests {

    @Autowired
    private ThreadLocalTest localTest;
    @Test
    void contextLoads() {
//        for (int i = 0; i <10 ; i++) {
            new Thread(()->{
                localTest.test();
                localTest.test();
            }).start();
//        }
    }

}
