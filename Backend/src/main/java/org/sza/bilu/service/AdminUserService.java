package org.sza.bilu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sza.bilu.entity.AdminUser;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService extends IService<AdminUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    AdminUser findByUsername(String username);
} 