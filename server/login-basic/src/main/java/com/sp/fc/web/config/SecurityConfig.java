package com.sp.fc.web.config;

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

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthDetails customAuthDetails;

    public SecurityConfig(CustomAuthDetails customAuthDetails) {
        this.customAuthDetails = customAuthDetails;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser(
                    User.withDefaultPasswordEncoder() //테스트시 withDefaultPasswordEncoder 사용
                            .username("user1")
                            .password("1111")
                            .roles("USER")
            ).withUser(
            User.withDefaultPasswordEncoder()
                    .username("admin")
                    .password("2222")
                    .roles("ADMIN")
        );
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
                    .authenticationDetailsSource(customAuthDetails) //details를 커스텀할때 사용
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
                        PathRequest.toStaticResources().atCommonLocations()
                );
    }
}
