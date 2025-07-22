package org.sza.bilu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.sza.bilu.entity.AdminUser;
import org.sza.bilu.service.AdminUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security用户详情服务实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminUserService adminUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询数据库中的用户
        AdminUser adminUser = adminUserService.findByUsername(username);
        
        if (adminUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        
        // 检查用户状态
        if (adminUser.getStatus() != 1) {
            throw new UsernameNotFoundException("用户已被禁用");
        }

        // 创建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + adminUser.getRole().toUpperCase()));

        // 返回UserDetails对象
        return new User(
                adminUser.getUsername(),
                adminUser.getPassword(), // 数据库中已加密的密码
                true,    // 启用
                true,    // 账号未过期
                true,    // 凭证未过期
                true,    // 账号未锁定
                authorities
        );
    }
} 