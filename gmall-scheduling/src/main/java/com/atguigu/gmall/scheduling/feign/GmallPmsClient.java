package com.atguigu.gmall.scheduling.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-02-02 20:49
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
