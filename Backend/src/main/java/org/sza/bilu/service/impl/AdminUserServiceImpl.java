package org.sza.bilu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sza.bilu.entity.AdminUser;
import org.sza.bilu.mapper.AdminUserMapper;
import org.sza.bilu.service.AdminUserService;

/**
 * 管理员用户服务实现类
 */
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Override
    public AdminUser findByUsername(String username) {
        LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminUser::getUsername, username);
        return this.getOne(queryWrapper);
    }
} 