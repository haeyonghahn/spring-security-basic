# 스프링 시큐리티 기본
## MySQL DB 생성
```sql
create database security;
```
## 시큐리티 첫 기본 화면
![시큐리티 기본 화면](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EA%B8%B0%EB%B3%B8%ED%99%94%EB%A9%B4.PNG)
디폴트 아이디 : user   
디폴트 비밀번호 : Using generated security password
![시큐리티 디폴트 비밀번호](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8.png)
## 로그인 화면
![로그인 화면](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EB%A1%9C%EA%B7%B8%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80.PNG)
`WebMvcConfig`에 `ViewResolver`로 셋팅되어 있는 경로의 페이지가 나오게 됩니다.
## 구글 로그인 준비
`https://console.cloud.google.com/`접속
![구글 로그인](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EA%B5%AC%EA%B8%80%EB%A1%9C%EA%B7%B8%EC%9D%B8.PNG)

새 프로젝트를 만든 후에 OAuth 동의 화면에서 User Type을 외부로 체크하고 만든다.
![OAuth동의화면](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/OAuth%EB%8F%99%EC%9D%98%ED%99%94%EB%A9%B4.PNG)

앱 이름을 만들고 저장한다.
![앱이름](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EC%95%B1%EC%9D%B4%EB%A6%84.PNG)

사용자 인증 정보에서 OAuth 클라이언트 ID를 만든다.   
승인된 리디렉션 URI 에 `http://localhost:8080/login/oauth2/code/google`을 적어준다. 해당 URI는 AccessToken 을 받기 위한 경로를 지정해주면 된다.
`http://localhost:8080`까지는 자유롭게 적을 수 있다. 그 뒤에 경로는   
- 구글 : `/login/oauth2/code/google`   
- 페이스북 : `/login/oauth2/code/facebook`으로 고정되어 있다. 
![OAuth 클라이언트이름](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EC%82%AC%EC%9A%A9%EC%9E%90%EC%9D%B8%EC%A6%9D%EC%A0%95%EB%B3%B4.PNG)
```java
@GetMapping("/login/oauth2/code/facebook") -> 필요없음
public String ...
```
해당 경로를 지정해주지 않아도 oauth2 라이브러리가 알아서 처리해준다.

OAuth 생성
![OAuth 생성](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/OAuth%EC%83%9D%EC%84%B1.PNG)

## dependency 추가
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

## application.yaml 설정
```yaml
security:
      oauth2:
         client:
            registration:
               google:
                  client-id: OAuth 생성할 때 만들어진 client-id를 넣어준다.
                  client-secret: OAuth 생성할 때 만들어진 client-secret을 넣어준다.
                  scope:
                  - email
                  - profile
```
## loginForm.html
```html
<!-- oauth2 라이브러리가 제공해주는 url이라서 마음대로 지정하면 안된다. -->
<a href="/oauth2/authorization/google">구글 로그인</a>
```
## 404에러
![404에러](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/404%EC%97%90%EB%9F%AC.PNG)

## SecurityConfig 설정
해당 부분을 추가해준다.
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	...
	.and()
	.oauth2Login()
	.loginPage("/loginForm");
}
```
![로그인성공](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/login%EC%83%9D%EC%84%B1.PNG)

## 403 에러
로그인이 되고나서 후처리가 필요하다.
![로그인성공후처리필요](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/403%EC%97%90%EB%9F%AC.PNG)

## 구글 회원 프로필 정보 받기
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	...
	.and()
	.oauth2Login()
	.loginPage("/loginForm")
	.userInfoEndpoint()
	/*
	 * 구글 로그인이 완료된 뒤의 후처리가 필요함  
	 * */
	.userService(principalOAuth2UserService);
}
```
```java
@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// OAuth 로그인 완료된 후 여기서 후처리한다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
		System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
		System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
		// 구글로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken 요청
		// userRequest 정보 -> loadUser함수 호출 -> 구글로부터 회원프로필을 받아준다.
		System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
		/*
		 * {sub=, 
		 * name=한해용, 
		 * given_name=해용, 
		 * family_name=한, 
		 * picture=https://lh3.googleusercontent.com/a/AATXAJyu3m1BtyhpFj6bYhJaiImdpk1C4dTFephjwtA=s96-c, 
		 * email=yong80211@gmail.com, 
		 * email_verified=true, 
		 * locale=ko}
		 * 
		 * username = "google_'sub번호'"
		 * password = "암호화(겟인데어)"
		 * email = "yong80211@gmail.com"
		 * role = "ROLE_USER"
		 * provider = "google" -> 어떤방식으로 로그인했는지 알려주는 컬럼
		 * providerId = "'sub번호'"
		 * */
		System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		return super.loadUser(userRequest);
	}
}
```
## User 정보를 찾을 수 있는 방법
- 1. Authentication 객체를 활용하여 찾을 수 있다.
- 2. @AuthenticationPrincipal 어노테이션을 활용하여 찾을 수 있다.

- 일반 로그인 테스트
```java
@GetMapping("/test/login")
public @ResponseBody String testLogin(
	Authentication authentication,
	@AuthenticationPrincipal PrincipalDetails userDetails) {	// DI(의존성주입)
	System.out.println("/test/login ================");
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
```
- oauth 로그인 테스트
```java
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
```
그렇다면, 세션 정보를 전달받기 위해서는 `@AuthenticationPrincipal`을 사용하여 커스터마이징한 `PrincipalDetails`객체를 사용하여   
세션 정보를 전달받는다.
```java
@GetMapping("/user")
public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails userDetails) {
	return "user";
}
```
그런데, 일반 로그인이 아니라, 구글 로그인으로 했을 때 해당 컨트롤러에서 `@AuthenticationPrincipal OAuth2User oauth`으로 써야하는 것인가?
아니다. 커스터마이징한 `PrincipalDetails`객체에서 `UserDetails`과 `OAuth2User`를 상속받는다.
```java
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
	...
}
```
![PrincipalDetails](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/PrincipalDetails.PNG)

## 구글 로그인 및 자동 회원가입
일반 로그인을 할 때는 `PrincipalDetails`객체에 `User`만 있지만,   
구글 로그인을 하게되면 `User`와 `attributes`까지 들어가게 된다.
```java
@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// OAuth 로그인 완료된 후 여기서 후처리한다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		...
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
		String provider = userRequest.getClientRegistration().getClientId();	// google
		String providerId = oauth2User.getAttribute("sub");			// 109782179000729307295
		String username = provider + "_" + providerId;				// google_109782179000729307295
		String password = bCryptPasswordEncoder.encode("겟인데어");		     // 비밀번호는 크게 의미없다.
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		if(userEntity == null) {
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
```
## 페이스북 로그인
`https://developers.facebook.com/` 접속
![페이스북로그인](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%ED%8E%98%EC%9D%B4%EC%8A%A4%EB%B6%81%20%EB%A1%9C%EA%B7%B8%EC%9D%B8.PNG)
![상세정보](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EC%83%81%EC%84%B8%EC%A0%95%EB%B3%B4.png)
![제품추가](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EC%A0%9C%ED%92%88%EC%B6%94%EA%B0%80.png)
![웹](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EC%9B%B9.PNG)
![기본설정](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EA%B8%B0%EB%B3%B8%EC%84%A4%EC%A0%95.png)

### scope
`https://developers.facebook.com/docs/facebook-login/web`
```yaml
facebook:
  client-id: 
  client-secret: 
  scope: # 로그인하는 사용자에게 웹페이지가 공개 프로필과 이메일에 액세스할 권한을 요청. 임의로 설정할 수 없다.
  - email
  - public_profile
```
![scope](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/scope.png)

## 페이스북과 구글 로그인 변수 명칭 다른 정보
clientId 가 담겨있는 변수가 provider(페이스북, 구글 등등)가 다르기 때문에 interface를 선언하여 값을 셋팅해준다.   
또한, 쉽게 유지보수를 할 수 있다.
```java
/*
 * clientId 가 담겨있는 변수가 provider마다 다르기 때문에
 * 만들어진 인터페이스이다.
 * */
public interface OAuth2UserInfo {
	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
}
```
```java
public class FacebookUserInfo implements OAuth2UserInfo {
	private Map<String, Object> attributes; // oauth2User.getAttributes()
	
	public FacebookUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProviderId() {
		return (String) attributes.get("id");
	}
	...
}
```
```java
public class GoogleUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes; // oauth2User.getAttributes()
	
	public GoogleUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProviderId() {
		return (String) attributes.get("sub");
	}
}
```
`PrincipalOAuth2UserService`에서 provider를 분기시켜 객체를 선언해준다.
```java
...
...
OAuth2UserInfo oAuth2UserInfo = null;
if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
	System.out.println("구글 로그인 요청");
	oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
} else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
	System.out.println("페이스북 로그인 요청");
	oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
} else {
	System.out.println("구글과 페이스북만 지원");
}

//String provider = userRequest.getClientRegistration().getRegistrationId();	// google, facebook
//System.out.println("provider : " + provider);
//String providerId = oauth2User.getAttribute("sub");		// 109782179000729307295
//String username = provider + "_" + providerId;		// google_109782179000729307295
//String password = bCryptPasswordEncoder.encode("겟인데어");    // 비밀번호는 크게 의미없다.
//String email = oauth2User.getAttribute("email");
//String role = "ROLE_USER";
//아래내용으로 수정
String provider = oAuth2UserInfo.getProvider();
String providerId = oAuth2UserInfo.getProviderId();
String username = provider + "_" + providerId;
String password = bCryptPasswordEncoder.encode("겟인데어");
String email = oAuth2UserInfo.getEmail();
String role = "ROLE_USER";
...
...
```
## 네이버 로그인
`https://developers.naver.com`접속
![네이버로그인](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EB%84%A4%EC%9D%B4%EB%B2%84%EB%A1%9C%EA%B7%B8%EC%9D%B8.png)
![네이버로그인2](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/%EB%84%A4%EC%9D%B4%EB%B2%84%EB%A1%9C%EA%B7%B8%EC%9D%B82.PNG)
```yaml
naver:
  client-id: 
  client-secret: 
  scope:
  - name
  - email
  client-name: Naver
  authorization-grant-type: authorization_code
  redirect-uri: http://localhost:8080/login/oauth2/code/naver
```
OAuth2 연동시 네이버는 Spring Security를 공식적으로 지원하지 않아서, 기본 설정을 따로 추가해줘야 한다. 그래서 위의 내용만 기입하고 실행시켜보면 해당 오류가 발생한다.
![naver오류](https://github.com/haeyonghahn/spring-security-basic/blob/master/images/naver%EC%98%A4%EB%A5%98.PNG)

그러므로 네이버 소셜 로그인 개발 가이드`https://developers.naver.com/docs/login/devguide/devguide.md#2-2-1-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8`에 따라서, 해당 내용을 기입해준다.
```yaml
provider:
 naver:
  authorization-uri: https://nid.naver.com/oauth2.0/authorize # 네이버로그인을 클릭하면 해당 주소가 호출된다.
  token-uri: https://nid.naver.com/oauth2.0/token
  user-info-uri: https://openapi.naver.com/v1/nid/me
  user-name-attribute: response # 회원정보를 json으로 받는데 response라는 키값으로 네이버가 리턴해준다. getAttributes: {resultcode=00, message=success, response={id=, email=, name=한해용}}
```
