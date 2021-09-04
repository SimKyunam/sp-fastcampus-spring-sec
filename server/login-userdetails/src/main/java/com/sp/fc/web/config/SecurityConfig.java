package com.sp.fc.web.config;

import com.sp.fc.user.service.SpUserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SpUserService userService;

    public SecurityConfig(SpUserService userService) {
        this.userService = userService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(request->{
                request
                    .antMatchers("/").permitAll()
                    .anyRequest().authenticated();
            })

            //로그인 페이지 지정하지 않으면 DefaultLoginPageGeneratingFilter 실행
            .formLogin(
                login -> login.loginPage("/login")
                    .permitAll() //permitAll을 하지 않으면 무한 루프에 빠짐 (로그인처리가 안됐기 때문)
                    .defaultSuccessUrl("/", false) //alwaysUse는 로그인 성공시 메인페이지 이동 여부
                    .failureUrl("/login-error")
            )
            .logout(logout -> logout.logoutSuccessUrl("/"))
            .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //리소스 파일들은 스프링 시큐리티를 타지 않도록 설정
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathRequest.toH2Console()
                );
    }
}
