package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-01-20 21:20
 */
@FeignClient(value = "sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
