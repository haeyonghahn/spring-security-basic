package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// OAuth 로그인 완료된 후 여기서 후처리한다.
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
		System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
		System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
		// 구글로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken 요청
		// userRequest 정보 -> loadUser함수 호출 -> 구글로부터 회원프로필을 받아준다.
		System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
		OAuth2User oauth2User = super.loadUser(userRequest);
		/*
		 * {sub=109782179000729307295, 
		 * name=한해용, 
		 * given_name=해용, 
		 * family_name=한, 
		 * picture=https://lh3.googleusercontent.com/a/AATXAJyu3m1BtyhpFj6bYhJaiImdpk1C4dTFephjwtA=s96-c, 
		 * email=yong80211@gmail.com, 
		 * email_verified=true, 
		 * locale=ko}
		 * 
		 * username = "google_109782179000729307295"
		 * password = "암호화(겟인데어)"
		 * email = "yong80211@gmail.com"
		 * role = "ROLE_USER"
		 * provider = "google"
		 * providerId = "109782179000729307295"
		 * */
		System.out.println("getAttributes: " + oauth2User.getAttributes());
		// 회원가입을 강제로 진행해볼 것이다. (User 객체를 만들어보기 위해서)
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		} else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
		} else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			/*
			 * getAttributes: {
			 * resultcode=00, 
			 * message=success, 
			 * response={id=DDv7j0e6gbz15v4ALeTEt4iXg6jhPfE0OUmZabF3scs, email=youg1322@naver.com, name=한해용}
			 * }
			 * */
			oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oauth2User.getAttributes().get("response"));
		} else {
			System.out.println("구글과 페이스북만 네이버만 지원");
		}
		
//		String provider = userRequest.getClientRegistration().getRegistrationId();	// google, facebook
//		System.out.println("provider : " + provider);
//		String providerId = oauth2User.getAttribute("sub");						// 109782179000729307295
//		String username = provider + "_" + providerId;							// google_109782179000729307295
//		String password = bCryptPasswordEncoder.encode("겟인데어");				// 비밀번호는 크게 의미없다.
//		String email = oauth2User.getAttribute("email");
//		String role = "ROLE_USER";
// 		아래내용으로 수정
		String provider = oAuth2UserInfo.getProvider();
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider + "_" + providerId;
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		if(userEntity == null) {
			System.out.println("로그인이 최초입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
			
		} else {
			System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
		}
		// 해당 객체로 세션 정보가 생성된다.
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
