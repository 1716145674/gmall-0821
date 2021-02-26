package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
class GmallItemApplicationTests {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Test
    void contextLoads() {
        System.out.println(threadPoolExecutor);
    }

}
