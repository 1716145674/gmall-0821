package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.wms.api.GamllWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-02-19 16:39
 */
@FeignClient("wms-service")
public interface GmalllWmsClient extends GamllWmsApi {
}
