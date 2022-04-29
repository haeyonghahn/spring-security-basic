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
