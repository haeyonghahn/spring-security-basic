package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity	// 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// 해당 메소드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			// user라는 주소로 들어오면 인증이 필요
			.antMatchers("/user/**").authenticated()	
			// manager 주소로 들어오면 권한 확인
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")	
			// admin 주소로 들어오면 권한 확인
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			// 다른 주소로 들어오는 것은 허용
			.anyRequest().permitAll()
			.and()
			// 권한이 없는 페이지로 접근할 때 "/login" 페이지로 이동하도록 한다.
			.formLogin()
			.loginPage("/loginForm");
	}
}
