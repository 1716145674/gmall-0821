package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("phone", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(wrapper) == 0;
    }

    @Override
    public void register(UserEntity userEntity, String code) {
        // 校验短信验证码
        // String cacheCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + userEntity.getPhone());
        // if (!StringUtils.equals(code, cacheCode)) {
        //     return false;
        // }

        //生成盐
        String salt = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        userEntity.setSalt(salt);

        // 对密码加密
        userEntity.setPassword(DigestUtils.md5Hex(salt+DigestUtils.md5Hex(userEntity.getPassword())));

        // 设置创建时间等
        userEntity.setCreateTime(new Date());
        userEntity.setLevelId(1l);
        userEntity.setStatus(1);
        userEntity.setIntegration(0);
        userEntity.setGrowth(0);
        userEntity.setNickname(userEntity.getUsername());

        // 添加到数据库
        boolean b = this.save(userEntity);

        // if(b){
        // 注册成功，删除redis中的记录
        // this.redisTemplate.delete(KEY_PREFIX + memberEntity.getPhone());
        // }
    }

    @Override
    public UserEntity queryUser(String loginName, String password) {
        List<UserEntity> userEntityList = this.list(new QueryWrapper<UserEntity>().eq("username", loginName).or().eq("phone", loginName)
                .or().eq("email", loginName));
        for (int i = 0; i < userEntityList.size(); i++) {

            if (StringUtils.equals(userEntityList.get(i).getPassword(),DigestUtils.md5Hex(userEntityList.get(i).getSalt() + DigestUtils.md5Hex(password)))){
                return userEntityList.get(i);
            }
        }

        return null;
    }

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

}