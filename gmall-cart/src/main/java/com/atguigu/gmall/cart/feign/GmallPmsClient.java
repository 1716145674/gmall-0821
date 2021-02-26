package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-02-19 16:37
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
