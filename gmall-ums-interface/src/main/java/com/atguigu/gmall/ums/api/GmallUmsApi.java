package com.atguigu.gmall.ums.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.ums.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zqq
 * @create 2021-02-22 20:18
 */
public interface GmallUmsApi {
    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("ums/user/check/{data}/{type}")
    public ResponseVo<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type);
    @GetMapping("ums/user/query")
    public ResponseVo<UserEntity> queryUser(
            @RequestParam("loginName")String loginName,
            @RequestParam("password")String password
    );
    /**
     * 信息
     */
    @GetMapping("ums/user/{id}")
    @ApiOperation("详情查询")
    public ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id);
}
