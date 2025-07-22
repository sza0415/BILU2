package org.sza.bilu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        // 使用BCrypt加密，更安全
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 允许跨域
            .cors(cors -> cors.configure(http))
            // 禁用CSRF，前后端分离不需要
            .csrf(csrf -> csrf.disable())
            // 配置请求授权
            .authorizeHttpRequests(authorize -> authorize
                // Swagger UI和Knife4j资源允许所有访问
                .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", 
                             "/doc.html", "/swagger-ui.html", "/configuration/**").permitAll()
                // admin角色可以访问管理页面
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // API接口，需要ADMIN角色
                // .requestMatchers("/api/module/secrecy/**").hasRole("ADMIN")
                // API接口，暂时允许所有访问（开发阶段）
                .requestMatchers("/api/module/secrecy/**").permitAll()
                .requestMatchers("/api/case/**").permitAll()
                .requestMatchers("/api/record/**").permitAll()
                // 其他API需要认证
                .anyRequest().authenticated()
            )
            // 配置表单登录
            .formLogin(form -> form
                // 前后端分离，使用默认登录页面
                .permitAll()
            )
            // 配置登出
            .logout(logout -> logout
                .permitAll()
            )
            // 记住我功能
            .rememberMe(remember -> remember
                .key("bilu-remember-me")
                .tokenValiditySeconds(86400) // 1天有效期
            );
            
        return http.build();
    }
}