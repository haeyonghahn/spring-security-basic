package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOAuth2UserService;

@Configuration
@EnableWebSecurity	// 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)	// @Secured 어노테이션 활성화, @PreAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PrincipalOAuth2UserService principalOAuth2UserService;
	
	// 해당 메소드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()	// 인증만 되면 들어갈 수 있는 주소	
			// manager 주소로 들어오면 권한 확인
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")	
			// admin 주소로 들어오면 권한 확인
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			// 다른 주소로 들어오는 것은 허용
			.anyRequest().permitAll()
			.and()
			// 권한이 없는 페이지로 접근할 때 "/login" 페이지로 이동하도록 한다.
			.formLogin()
			.loginPage("/loginForm")
			.loginProcessingUrl("/login")	// login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
			.defaultSuccessUrl("/")
			.and()
			// 구글 로그인이 완료
			.oauth2Login()
			.loginPage("/loginForm")	
			/*
			 * 1.코드받기(인증되었다) 2.엑세스토큰(권한) 3.사용자프로필 정보를 가져오고 
			 * 4-1.그 정보를 토대로 회원가입을 자동으로 진행시킨다.
			 * 4-2.그런데 구글이 들고있는 프로필이 이메일,전화번호,이름,아이디 밖에 없다.
			 * 근데 나는 쇼핑몰을 할 경우, 집주소가 필요할것이고
			 * 백화점몰을 할 경우, vip등급이나 일반등급이 필요할 것이다.
			 * 이럴땐 추후 회원가입을 해야한다.
			 * Tip. 로그인이 완료되면 코드를 받는것이 아니라 (엑세스 토큰과 사용자프로필정보를 한 번에 받는다)
			 * */
			.userInfoEndpoint()
			/*
			 * 구글 로그인이 완료된 뒤의 후처리가 필요함  
			 * */
			.userService(principalOAuth2UserService);
			
	}
}
