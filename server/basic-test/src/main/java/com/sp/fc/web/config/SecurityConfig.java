package com.sp.fc.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //사용자를 추가하고 싶은 경우 사용 (WebSecurity 설정 > application.yml)
        //withUser 설정시 application.yml에 유저정보는 적용되지 않는다.
        //password 설정시 암호화 encode를 사용해야 오류가 발생하지 않는다.
        auth.inMemoryAuthentication()
                .withUser(User.builder()
                    .username("user2")
                    .password(passwordEncoder().encode("2222"))
                    .roles("USER")
                ).withUser(User.builder()
                    .username("admin")
                    .password(passwordEncoder().encode("3333"))
                    .roles("ADMIN")
                );
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        //스프링의 기본 설정은 BCryptPasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests((requests) ->
            requests.antMatchers("/").permitAll()
                .anyRequest().authenticated()
        );
        http.formLogin();
        http.httpBasic();
    }
}
