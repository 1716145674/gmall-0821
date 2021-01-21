package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.api.vo.SalesVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zqq
 * @create 2021-01-20 20:50
 */

public interface GmallSmsApi {
    // 保存sku的销售信息
    @PostMapping("sms/skubounds/saveSales")
    public ResponseVo saveSales(@RequestBody SalesVo salesVo);
}
