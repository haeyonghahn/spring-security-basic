package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
		Authentication authentication,
		@AuthenticationPrincipal PrincipalDetails userDetails) {	// DI(의존성주입)
		System.out.println("/test/login ================");
		// PrincipalDetails은 UserDetails을 상속받고 있어 PrincipalDetails을 사용하고 있다.
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication : " + principalDetails.getUser());
		/* authentication : 
		 * User(id=1, username=한해용, password=$2a$10$YyKRSMY5l/DEoXV3AB.yMOCSLRdvBhRNCJAFCDUJWJiHFhNRP7D0., 
		 * email=yong80211@gmail.com, role=ROLE_USER, 			 
		 * provider=null, providerId=null, loginDate=null, createDate=2022-05-08 21:37:26.0)
		 */
		System.out.println("userDetails : " + userDetails.getUser());	
		/*
		 * @AuthenticationPrincipal 어노테이션 user 정보를 알 수 있다.
		 * userDetails : 
		 * User(id=1, username=한해용, password=$2a$10$YyKRSMY5l/DEoXV3AB.yMOCSLRdvBhRNCJAFCDUJWJiHFhNRP7D0., 
		 * email=yong80211@gmail.com, role=ROLE_USER, 
		 * provider=null, providerId=null, loginDate=null, createDate=2022-05-08 21:37:26.0)
		 */
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
		Authentication authentication,
		@AuthenticationPrincipal OAuth2User oauth) {	// DI(의존성주입)
		System.out.println("/test/oauth/login ================");
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication : " + oauth2User.getAttributes());
		/* authentication : {sub=109782179000729307295, 
		 * name=한해용, given_name=해용, family_name=한, 
		 * picture=https://lh3.googleusercontent.com/a/AATXAJyu3m1BtyhpFj6bYhJaiImdpk1C4dTFephjwtA=s96-c, 
		 * email=yong80211@gmail.com, email_verified=true, locale=ko}
		 */
		System.out.println("oauth2User : " + oauth.getAttributes());
		/* authentication : {sub=109782179000729307295, 
		 * name=한해용, given_name=해용, family_name=한, 
		 * picture=https://lh3.googleusercontent.com/a/AATXAJyu3m1BtyhpFj6bYhJaiImdpk1C4dTFephjwtA=s96-c, 
		 * email=yong80211@gmail.com, email_verified=true, locale=ko}
		 */
		return "OAuth 세션 정보 확인하기";
	}
	
	@GetMapping({"", "/"})
	public String index() {
		// 머스테치 기본폴더 src/main/resources/
		// 뷰리솔버 설정 : templates (prefix), .mustache (suffix)
		return "index";
	}
	
	// OAuth 로그인을 해도 PrincipalDetails
	// 일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails: " + principalDetails.getUser());
		
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);	// 회원가입이 잘되지만 시큐리티로 로그인을 할 수 없다. 패스워드가 암호화되어 있지 않기 때문이다.
		return "redirect:/loginForm";
	}
	
	// @Secured => OR 조건, AND 조건 불가능
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	// @PreAuthorize => and 조건, or 조건 모두 가능
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
