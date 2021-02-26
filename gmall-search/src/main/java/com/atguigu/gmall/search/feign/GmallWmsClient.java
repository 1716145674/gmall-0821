package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.GamllWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-01-28 13:32
 */
@FeignClient("wms-service")
public interface GmallWmsClient  extends GamllWmsApi {
}
