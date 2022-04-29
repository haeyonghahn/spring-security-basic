package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

// CRUD 함수를 JpaRepository가 들고 있다.
// @Repository라는 어노테이션이 없어도 IoC가 된다. JpaRepository가 상속을 했기 때문이다.
public interface UserRepository extends JpaRepository<User, Integer> {

}
