package com.atguigu.gmall.cart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallCartApplicationTests {

    @Test
    void contextLoads() {
        ArrayList<String> list = new ArrayList<>();
        try {
            getFanXixng(list);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void getFanXixng(ArrayList list) throws NoSuchMethodException {
        Method method = GmallCartApplicationTests.class.getMethod("getFanXixng", List.class);
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            if (genericParameterType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericParameterType;
                Type[] actualTypeArguments = type.getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    System.out.println(actualTypeArgument);
                    
                }
            }
        }


    }

}
