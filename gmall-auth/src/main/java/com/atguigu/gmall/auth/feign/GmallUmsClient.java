package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @author zqq
 * @create 2021-02-23 18:18
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
