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
