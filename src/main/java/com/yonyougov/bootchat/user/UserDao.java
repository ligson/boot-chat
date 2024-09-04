package com.yonyougov.bootchat.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao  extends JpaRepository<User, String>, QuerydslPredicateExecutor<User> {

}
