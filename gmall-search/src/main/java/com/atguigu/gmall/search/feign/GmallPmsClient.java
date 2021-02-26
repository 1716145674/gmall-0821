package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-01-28 13:31
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
