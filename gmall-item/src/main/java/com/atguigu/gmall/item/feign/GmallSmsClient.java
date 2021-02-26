package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zqq
 * @create 2021-02-19 16:38
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
