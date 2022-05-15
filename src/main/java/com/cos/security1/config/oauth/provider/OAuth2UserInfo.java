package com.cos.security1.config.oauth.provider;

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
